/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.webconsole.internal.obr;


import org.apache.felix.webconsole.internal.BaseManagementPlugin;
import org.osgi.service.log.LogService;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.util.tracker.ServiceTracker;


public class AbstractObrPlugin extends BaseManagementPlugin
{

    // track the optional installer service manually
    private ServiceTracker repositoryAdmin;

    // marker indicating a special version to not install anything
    protected static final String DONT_INSTALL_OPTION = "-";

    protected RepositoryAdmin getRepositoryAdmin()
    {
        if ( repositoryAdmin == null )
        {
            try
            {
                repositoryAdmin = new ServiceTracker( getBundleContext(), RepositoryAdmin.class.getName(), null );
                repositoryAdmin.open();
            }
            catch ( Throwable t )
            {
                getLog().log( LogService.LOG_WARNING, "Cannot create RepositoryAdmin service tracker", t );
                return null;
            }

        }

        return ( RepositoryAdmin ) repositoryAdmin.getService();
    }

}
