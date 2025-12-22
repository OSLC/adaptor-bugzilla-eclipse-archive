# OSLC CM server for the the archive of the Lyo bugs in the Eclipse Bugzilla

This project implements an OSLC Change Management (CM) server for accessing the archived bugs of the Eclipse Lyo project in Bugzilla. The server exposes the bugs as OSLC resources, allowing clients to interact with them using standard OSLC protocols. The bugs are loaded from an XML export under `source/BugzillaLyoArchives/src/main/resources/show_bug.cgi.xml`.

## Getting started

```
cd ./source/BugzillaLyoArchives
mvn clean jetty:run
```

Navigate to http://localhost:8180/ afterwards.

## License

```
Copyright (c) 2025 Andrew Berezovskyi
Copyright (c) 2023 KTH Royal Institute of Technology

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

SPDX-License-Identifier: EPL-2.0

---

Copyright (c) 2012 IBM Corporation and Contributors to the Eclipse Foundation.
 
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
and Eclipse Distribution License v. 1.0 which accompanies this distribution.
  
The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
and the Eclipse Distribution License is available at
http://www.eclipse.org/org/documents/edl-v10.php.

SPDX-License-Identifier: EPL-1.0 OR BSD-3-Clause
```
