/*
 * Copyright (c)2005-2012 Mark Logic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The use of the Apache License does not indicate that this project is
 * affiliated with the Apache Software Foundation.
 */
package com.marklogic.developer.corb;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.Session;

/**
 * @author Michael Blakeley, michael.blakeley@marklogic.com
 *
 */
public class Transform extends AbstractTask {
    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.Callable#call()
     */
    public String call() throws Exception {
        // try to avoid thread starvation
        Thread.yield();
        Session session = null;
        try {
            session = newSession();
            Request request = session.newModuleInvoke(moduleUri);
            request.setNewStringVariable("URI", inputUri);
            if(properties.containsKey(Manager.URIS_MODULE_METADATA)){
            	request.setNewStringVariable("URIS_MODULE_METADATA", properties.getProperty(Manager.URIS_MODULE_METADATA));
            }
            // try to avoid thread starvation
            Thread.yield();
            String response = session.submitRequest(request).asString();
            session.close();
            session = null;
            return response;
        } finally {
            if (null != session) {
                session.close();
                session = null;
            }
            // try to avoid thread starvation
            Thread.yield();
        }
    }
}
