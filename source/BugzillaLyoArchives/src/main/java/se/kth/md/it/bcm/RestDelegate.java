package se.kth.md.it.bcm;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.md.it.bcm.bugzilla.Bug;
import se.kth.md.it.bcm.bugzilla.BugzillaArchive;
import se.kth.md.it.bcm.resources.BugzillaChangeRequest;
import se.kth.md.it.bcm.resources.Person;

// Start of user code imports
import jakarta.servlet.ServletException;
import jakarta.ws.rs.core.Response;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

import jakarta.ws.rs.WebApplicationException;
// End of user code

public class RestDelegate {

    private static final Logger log = LoggerFactory.getLogger(RestDelegate.class);

    @Inject ResourcesFactory resourcesFactory;

    // Start of user code class_attributes
	public static final String LYO_PRODUCT_NAME = "Lyo";
	public static final int LYO_PRODUCT_ID = 2;
	public final static String REALM = "Bugzilla";
	public static final String USER_DIR_PROPERTY = "user.dir";
	public static final String BUGZ_PROPERTIES_PATH = "/test/resources/bugz.properties";
	public static final String BUGZILLA_URI_KEY = "bugzilla_uri";
	public static final String BUGZILLA_URI_DEFAULT = "https://landfill.bugzilla.org/bugzilla-4.4-branch/";

	public static final String ADMIN_KEY = "admin";
	public static final String ADMIN_DEFAULT = "root@localhost";
	public static final String LATEST_STORE_UPDATE_KEY = "latestStoreUpdate";
	public static final String LATEST_STORE_UPDATE_DEFAULT = "1970-01-01T00:01:00Z";
	public static final String LAST_UPDATE_KEY = "lyo://store/lastUpdate";

	private static String bugzillaUri = null;
	private static String admin = null;
	private static Properties properties;
	private static Date lastStoreUpdate;
	// End of user code
    
    public RestDelegate() {
        log.trace("Delegate is initialized");
    }
    
    // Start of user code class_methods
	public static String getBugzillaUri() {
		return bugzillaUri;
    }

	public static String getAdmin() {
		return admin;
	}

	public BugzillaChangeRequest getBugzillaChangeRequestFromServer(HttpServletRequest httpServletRequest, final String bugId) {
		BugzillaChangeRequest aResource = null;
		try {
			Bug bug = RestDelegate.getBugById(httpServletRequest, bugId);
			if (bug != null) {
				aResource = fromBug(bug, httpServletRequest);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
		}
		return aResource;
	}

	public BugzillaChangeRequest fromBug(Bug bug, HttpServletRequest httpServletRequest)
		throws URISyntaxException, UnsupportedEncodingException {
		BugzillaChangeRequest changeRequest = new BugzillaChangeRequest();
		changeRequest.setIdentifier(Integer.toString(bug.getID()));
		changeRequest.setTitle(bug.getSummary());
		changeRequest.setStatus(bug.getStatus());

		String assignedTo = bug.getAssignedTo();
		if (assignedTo != null) {
			String email = assignedTo;
			Person contributor = new Person();
			HashSet<Person> contributors = new HashSet<Person>();
			contributors.add(contributor);
			changeRequest.setContributor(contributors);
		}

		Date createdDate = bug.getCreationTime();
		changeRequest.setCreated(createdDate);

		Date modifiedDate = bug.getLastChangeTime();
		changeRequest.setModified(modifiedDate);

		changeRequest.setProduct(bug.getProduct());
		changeRequest.setComponent(bug.getComponent());

		String version = bug.getVersion();
		if (version != null) {
			changeRequest.setVersion(version);
		}

		changeRequest.setPriority(bug.getPriority());

		changeRequest.setPlatform(bug.getPlatform());
		changeRequest.setOperatingSystem(bug.getOperatingSystem());

		changeRequest.setAbout(resourcesFactory.constructURIForBugzillaChangeRequest(changeRequest.getIdentifier()));

		return changeRequest;
	}

	public List<BugzillaChangeRequest> changeRequestsFromBugList(final HttpServletRequest httpServletRequest, final List<Bug> bugList, final String serviceProviderId)
    {
    	List<BugzillaChangeRequest> results = new ArrayList<BugzillaChangeRequest>();

        for (Bug bug : bugList) {
        	BugzillaChangeRequest changeRequest = null;
        	try {
        		changeRequest = fromBug(bug, httpServletRequest);
        	} catch (Exception e) {
        		throw new WebApplicationException(e);
        	}

        	if (changeRequest != null) {
        		results.add(changeRequest);
        	}
        }
        return results;
    }

	public static List<Bug> getBugsByComponent(final HttpServletRequest httpServletRequest, final String componentId, int page, int limit) throws IOException, ServletException
    {
    	List<Bug> results=null;

		try {
			final String pageString = httpServletRequest.getParameter("page");

			if (null != pageString) {
				page = Integer.parseInt(pageString);
			}

			String targetComponent = null;
			String targetProduct = null;

			for(String p : BugzillaArchive.getInstance().getProducts()) {
			    for(String c : BugzillaArchive.getInstance().getComponents(p)) {
			        if(Integer.toString(c.hashCode()).equals(componentId)) {
			            targetComponent = c;
			            targetProduct = p;
			            break;
			        }
			    }
			    if(targetComponent != null) break;
			}

			if (targetComponent != null) {
			    results = BugzillaArchive.getInstance().getBugsByProductAndComponent(targetProduct, targetComponent);
			    results = slicePaging(results, page, limit);
			} else {
				results = new ArrayList<>();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}

    	return results;
    }

	public static Bug getBugById(final HttpServletRequest request, final String bugIdString) throws IOException, ServletException
	{
		int bugId = -1;
		Bug bug = null;

		try {
			bugId = Integer.parseInt(bugIdString);
			bug = BugzillaArchive.getInstance().getBug(bugId);
			if (bug == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}

		return bug;
	}

	public static void loadBugzillaProperties() {
		final InputStream stream = RestDelegate.class.getResourceAsStream(
			"/bugz.properties");
		properties = loadBugzillaPropertiesFromStream(stream);
	}

	private static Properties loadBugzillaPropertiesFromStream(final InputStream
		propertyFilePath) {
		Properties props = new Properties();
		try {
			props.load(propertyFilePath);
		} catch (Exception e) {
			log.error("Failed to parse Bugzilla properties from stream");
		}
		return props;
	}

	public static void configureBugzillaInfo(final Properties props) {
		bugzillaUri = props.getProperty(BUGZILLA_URI_KEY, BUGZILLA_URI_DEFAULT);
		bugzillaUri = normalizeUri(bugzillaUri);
		admin = props.getProperty(ADMIN_KEY, ADMIN_DEFAULT);
		logConfigurationProperty(BUGZILLA_URI_KEY, RestDelegate.bugzillaUri);
		logConfigurationProperty(ADMIN_KEY, RestDelegate.admin);
	}

	private static void logConfigurationProperty(final String property, final String
		value) {
		log.info("Property {}='{}'", property, value);
	}

	private static String normalizeUri(String uri) {
		if (uri != null && uri.endsWith("/")) {
			return uri.substring(0, uri.length() - 1);
		}
		return uri;
	}

	private static ServiceProviderInfo[] getServiceProviderInfos()
	{
		var spInfos = new ArrayList<ServiceProviderInfo>();
		List<String> products = BugzillaArchive.getInstance().getProducts();

		for (String p : products) {
		    if (p.equalsIgnoreCase(LYO_PRODUCT_NAME)) {
		        List<String> components = BugzillaArchive.getInstance().getComponents(p);
		        for (String c : components) {
		            ServiceProviderInfo serviceProviderInfo = new ServiceProviderInfo();
		            serviceProviderInfo.name = c;
		            serviceProviderInfo.serviceProviderId = Integer.toString(c.hashCode());
		            spInfos.add(serviceProviderInfo);
		        }
		    }
		}
		return spInfos.toArray(new ServiceProviderInfo[0]);
	}

	private static <T> List<T> slicePaging(List<T> resources, int page, int limit) {
		if (limit <= 0) {
			return resources;
		}
		int fromIndex = page * limit;
		int limitIndex = fromIndex + limit + 1; // TODO: 2016-12-26 remove +1 workaround
		int toIndex = Math.min(limitIndex, resources.size());
		log.debug("Creating a slice [{},{}] on a list of {} elements", fromIndex, toIndex,
			resources.size());
		if (fromIndex >= resources.size()) {
		    return new ArrayList<>();
		}
		return new ArrayList<>(resources.subList(fromIndex, toIndex));
	}
	// End of user code

    public static ServiceProviderInfo[] getServiceProviderInfos(HttpServletRequest httpServletRequest)
    {
        ServiceProviderInfo[] serviceProviderInfos = {};
        
        // Start of user code "ServiceProviderInfo[] getServiceProviderInfos(...)"
		serviceProviderInfos = getServiceProviderInfos();
		// End of user code
        return serviceProviderInfos;
    }

    public List<BugzillaChangeRequest> queryBugzillaChangeRequests(HttpServletRequest httpServletRequest, final String serviceProviderId, String where, String prefix, boolean paging, int page, int limit)
    {
        List<BugzillaChangeRequest> resources = null;
        
        // Start of user code queryBugzillaChangeRequests
		try {
			List<Bug> bugList = getBugsByComponent(
				httpServletRequest, serviceProviderId, page, limit);
			resources = changeRequestsFromBugList(
				httpServletRequest, bugList, serviceProviderId);
		} catch (IOException | ServletException e) {
			log.error("Failed to fetch bugz from Bugzilla instance", e);
		}
		// End of user code
        return resources;
    }

    public BugzillaChangeRequest getBugzillaChangeRequest(HttpServletRequest httpServletRequest, final String bugId)
    {
        BugzillaChangeRequest aResource = null;
        
        // Start of user code getBugzillaChangeRequest
		try {
			aResource = getBugzillaChangeRequestFromServer(httpServletRequest, bugId);
		} catch (Exception e) {
			log.error(String.valueOf(e));
		}
		// End of user code
        return aResource;
    }

    public String getETagFromBugzillaChangeRequest(final BugzillaChangeRequest aResource)
    {
        String eTag = null;
        // Start of user code getETagFromBugzillaChangeRequest
    	Long eTagAsTime = null;

    	if (aResource.getModified() != null) {
    		eTagAsTime = aResource.getModified().getTime();
    	} else if (aResource.getCreated() != null) {
    		eTagAsTime = aResource.getCreated().getTime();
    	} else {
    		eTagAsTime = 0L;
    	}
		eTag = eTagAsTime.toString();
		// End of user code
        return eTag;
    }

}
