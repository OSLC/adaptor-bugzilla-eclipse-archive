// Start of user code Copyright
/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Simple
 */
// End of user code

package se.kth.md.it.bcm;


// spotless:off
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import jakarta.ws.rs.core.UriBuilder;

import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import se.kth.md.it.bcm.resources.BugzillaChangeRequest;
import se.kth.md.it.bcm.resources.ChangeRequest;
import se.kth.md.it.bcm.resources.Person;
import se.kth.md.it.bcm.resources.Type;

// Start of user code imports
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import jakarta.ws.rs.core.UriBuilder;

import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import se.kth.md.it.bcm.resources.BugzillaChangeRequest;
import se.kth.md.it.bcm.resources.ChangeRequest;
import se.kth.md.it.bcm.resources.Person;
import se.kth.md.it.bcm.resources.Type;
// End of user code
// spotless:on

// Start of user code pre_class_code
// End of user code

public class ResourcesFactory {

    private String basePath;

    // Start of user code class_attributes
    // End of user code

    public ResourcesFactory(String basePath) {
        this.basePath = basePath;
    }

    // Start of user code class_methods
    // End of user code

    //methods for BugzillaChangeRequest resource
    
    public BugzillaChangeRequest createBugzillaChangeRequest(final String bugId) {
        return new BugzillaChangeRequest(constructURIForBugzillaChangeRequest(bugId));
    }
    
    public URI constructURIForBugzillaChangeRequest(final String bugId) {
        Map<String, Object> pathParameters = new HashMap<String, Object>();
        pathParameters.put("bugId", bugId);
        String instanceURI = "bugz/{bugId}";
    
        final UriBuilder builder = UriBuilder.fromUri(this.basePath);
        return builder.path(instanceURI).buildFromMap(pathParameters);
    }
    
    public Link constructLinkForBugzillaChangeRequest(final String bugId , final String label) {
        return new Link(constructURIForBugzillaChangeRequest(bugId), label);
    }
    
    public Link constructLinkForBugzillaChangeRequest(final String bugId) {
        return new Link(constructURIForBugzillaChangeRequest(bugId));
    }
    

}
