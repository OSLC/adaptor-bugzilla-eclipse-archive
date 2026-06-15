package se.kth.md.it.bcm.bugzilla;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BugzillaArchive {
    private static final Logger log = LoggerFactory.getLogger(BugzillaArchive.class);
    private static BugzillaArchive instance;
    private final Map<Integer, Bug> bugs = new ConcurrentHashMap<>();
    private final Map<String, List<ProductComponent>> productComponents = new ConcurrentHashMap<>();

    private BugzillaArchive() {
        loadData();
    }

    public static synchronized BugzillaArchive getInstance() {
        if (instance == null) {
            instance = new BugzillaArchive();
        }
        return instance;
    }

    private void loadData() {
        try (InputStream is = getClass().getResourceAsStream("/show_bug.cgi.xml")) {
            if (is == null) {
                log.error("Could not find show_bug.cgi.xml");
                return;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("bug");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Bug bug = new Bug();
                    bug.setId(Integer.parseInt(getElementValue(eElement, "bug_id")));
                    bug.setSummary(getElementValue(eElement, "short_desc"));
                    bug.setStatus(getElementValue(eElement, "bug_status"));
                    bug.setProduct(getElementValue(eElement, "product"));
                    bug.setComponent(getElementValue(eElement, "component"));
                    bug.setVersion(getElementValue(eElement, "version"));
                    bug.setPriority(getElementValue(eElement, "priority"));
                    bug.setPlatform(getElementValue(eElement, "rep_platform"));
                    bug.setOperatingSystem(getElementValue(eElement, "op_sys"));

                    String creationTs = getElementValue(eElement, "creation_ts");
                    if (creationTs != null) {
                        try {
                            bug.setCreationTime(sdf.parse(creationTs));
                        } catch (Exception e) {
                            log.warn("Failed to parse creation_ts: " + creationTs);
                        }
                    }

                    String deltaTs = getElementValue(eElement, "delta_ts");
                    if (deltaTs != null) {
                        try {
                            bug.setLastChangeTime(sdf.parse(deltaTs));
                        } catch (Exception e) {
                            log.warn("Failed to parse delta_ts: " + deltaTs);
                        }
                    }

                    NodeList assignedToNodes = eElement.getElementsByTagName("assigned_to");
                    if (assignedToNodes.getLength() > 0) {
                         Element assignedToElement = (Element) assignedToNodes.item(0);
                         bug.setAssignedTo(assignedToElement.getTextContent());
                         bug.setAssignedToName(assignedToElement.getAttribute("name"));
                    }

                    NodeList reporterNodes = eElement.getElementsByTagName("reporter");
                    if (reporterNodes.getLength() > 0) {
                         Element reporterElement = (Element) reporterNodes.item(0);
                         bug.setReporterEmail(reporterElement.getTextContent());
                         bug.setReporterName(reporterElement.getAttribute("name"));
                    }

                    NodeList longDescNodes = eElement.getElementsByTagName("long_desc");
                    for (int i = 0; i < longDescNodes.getLength(); i++) {
                        Element longDescElement = (Element) longDescNodes.item(i);
                        String commentCount = getElementValue(longDescElement, "comment_count");
                        String isPrivate = longDescElement.getAttribute("isprivate");
                        
                        Comment comment = new Comment();
                        comment.setCommentCount(Integer.parseInt(commentCount));
                        comment.setThetext(getElementValue(longDescElement, "thetext"));
                        
                        String commentIdStr = getElementValue(longDescElement, "commentid");
                        if (commentIdStr != null) {
                            comment.setCommentId(Integer.parseInt(commentIdStr));
                        }
                        
                        NodeList whoNodes = longDescElement.getElementsByTagName("who");
                        if (whoNodes.getLength() > 0) {
                            Element whoElement = (Element) whoNodes.item(0);
                            comment.setWho(whoElement.getTextContent());
                            comment.setWhoName(whoElement.getAttribute("name"));
                        }
                        
                        String bugWhenStr = getElementValue(longDescElement, "bug_when");
                        if (bugWhenStr != null) {
                            try {
                                comment.setBugWhen(sdf.parse(bugWhenStr));
                            } catch (Exception e) {
                                log.warn("Failed to parse bug_when: " + bugWhenStr);
                            }
                        }
                        
                        comment.setPrivate("1".equals(isPrivate));
                        bug.addComment(comment);
                        
                        // The first comment (comment_count = 0) is treated as the bug description
                        if ("0".equals(commentCount)) {
                            bug.setDescription(comment.getThetext());
                        }
                    }

                    bugs.put(bug.getID(), bug);

                    // Populate product components structure
                    productComponents.computeIfAbsent(bug.getProduct(), k -> new ArrayList<>())
                            .add(new ProductComponent(bug.getProduct(), bug.getComponent()));
                }
            }
            log.info("Loaded {} bugs from XML", bugs.size());

        } catch (Exception e) {
            log.error("Error loading Bugzilla data", e);
        }
    }

    private String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    public Bug getBug(int id) {
        return bugs.get(id);
    }

    public List<Bug> getBugsByProductAndComponent(String product, String component) {
        return bugs.values().stream()
                .filter(b -> b.getProduct().equals(product) && b.getComponent().equals(component))
                .collect(Collectors.toList());
    }

    public List<String> getProducts() {
        return new ArrayList<>(productComponents.keySet());
    }

    public List<String> getComponents(String product) {
        List<ProductComponent> pcs = productComponents.get(product);
        if (pcs == null) return new ArrayList<>();
        return pcs.stream().map(ProductComponent::getComponent).distinct().collect(Collectors.toList());
    }

    private static class ProductComponent {
        private String product;
        private String component;

        public ProductComponent(String product, String component) {
            this.product = product;
            this.component = component;
        }

        public String getProduct() { return product; }
        public String getComponent() { return component; }
    }
}
