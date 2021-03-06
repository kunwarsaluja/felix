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
package org.apache.felix.webconsole.internal.servlet;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;


class ConfigurationListener implements ManagedService, MetaTypeProvider
{

    private final String pid;

    private final OsgiManager osgiManager;

    private ObjectClassDefinition ocd;


    static ServiceRegistration create( OsgiManager osgiManager )
    {
        ConfigurationListener cl = new ConfigurationListener( osgiManager );

        Dictionary props = new Hashtable();
        props.put( Constants.SERVICE_VENDOR, "The Apache Software Foundation" );
        props.put( Constants.SERVICE_DESCRIPTION, "OSGi Management Console Configuration Receiver" );
        props.put( Constants.SERVICE_PID, cl.pid );

        return osgiManager.getBundleContext().registerService( new String[]
            { ManagedService.class.getName(), MetaTypeProvider.class.getName() }, cl, props );
    }


    private ConfigurationListener( OsgiManager osgiManager )
    {
        this.osgiManager = osgiManager;
        this.pid = osgiManager.getClass().getName();
    }


    //---------- ManagedService

    public void updated( Dictionary config )
    {
        osgiManager.updateConfiguration( config );
    }


    //---------- MetaTypeProvider

    public String[] getLocales()
    {
        // there is no locale support here
        return null;
    }


    public ObjectClassDefinition getObjectClassDefinition( String id, String locale )
    {
        if ( !pid.equals( id ) )
        {
            return null;
        }

        if ( ocd == null )
        {

            final ArrayList adList = new ArrayList();
            adList.add( new AttributeDefinitionImpl( OsgiManager.PROP_MANAGER_ROOT, "Root URI",
                "The root path to the OSGi Management Console.", OsgiManager.DEFAULT_MANAGER_ROOT ) );
            adList.add( new AttributeDefinitionImpl( OsgiManager.PROP_DEFAULT_RENDER, "Default Page",
                "The name of the default configuration page when invoking the OSGi Management console.",
                OsgiManager.DEFAULT_PAGE ) );
            adList.add( new AttributeDefinitionImpl( OsgiManager.PROP_REALM, "Realm",
                "The name of the HTTP Authentication Realm.", OsgiManager.DEFAULT_REALM ) );
            adList
                .add( new AttributeDefinitionImpl(
                    OsgiManager.PROP_USER_NAME,
                    "User Name",
                    "The name of the user allowed to access the OSGi Management Console. To disable authentication clear this value.",
                    OsgiManager.DEFAULT_USER_NAME ) );
            adList.add( new AttributeDefinitionImpl( OsgiManager.PROP_PASSWORD, "Password",
                "The password for the user allowed to access the OSGi Management Console.",
                OsgiManager.DEFAULT_PASSWORD ) );

            final LinkedHashMap namesByClassName = new LinkedHashMap();
            final ClassLoader loader = getClass().getClassLoader();
            final String[] defaultPluginsClasses = OsgiManager.PLUGIN_CLASSES;
            for ( int i = 0; i < defaultPluginsClasses.length; i++ )
            {
                try
                {
                    final Object plugin = loader.loadClass( defaultPluginsClasses[i] ).newInstance();
                    if ( plugin instanceof AbstractWebConsolePlugin )
                    {
                        final String name = ( ( AbstractWebConsolePlugin ) plugin ).getTitle();
                        namesByClassName.put( defaultPluginsClasses[i], name );
                    }
                }
                catch ( Throwable t )
                {
                    // ignore
                }
            }
            final String[] classes = ( String[] ) namesByClassName.keySet().toArray(
                new String[namesByClassName.size()] );
            final String[] names = ( String[] ) namesByClassName.values().toArray( new String[namesByClassName.size()] );

            adList.add( new AttributeDefinitionImpl( OsgiManager.PROP_ENABLED_PLUGINS, "Plugins",
                "Select active plugins", AttributeDefinition.STRING, classes, Integer.MIN_VALUE, names, classes ) );

            ocd = new ObjectClassDefinition()
            {

                private final AttributeDefinition[] attrs = ( AttributeDefinition[] ) adList
                    .toArray( new AttributeDefinition[adList.size()] );


                public String getName()
                {
                    return "Apache Felix OSGi Management Console";
                }


                public InputStream getIcon( int arg0 )
                {
                    return null;
                }


                public String getID()
                {
                    return pid;
                }


                public String getDescription()
                {
                    return "Configuration of the Apache Felix OSGi Management Console.";
                }


                public AttributeDefinition[] getAttributeDefinitions( int filter )
                {
                    return ( filter == OPTIONAL ) ? null : attrs;
                }
            };
        }

        return ocd;
    }

    private static class AttributeDefinitionImpl implements AttributeDefinition
    {

        private final String id;
        private final String name;
        private final String description;
        private final int type;
        private final String[] defaultValues;
        private final int cardinality;
        private final String[] optionLabels;
        private final String[] optionValues;


        AttributeDefinitionImpl( final String id, final String name, final String description, final String defaultValue )
        {
            this( id, name, description, STRING, new String[]
                { defaultValue }, 0, null, null );
        }


        AttributeDefinitionImpl( final String id, final String name, final String description, final int type,
            final String[] defaultValues, final int cardinality, final String[] optionLabels,
            final String[] optionValues )
        {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.defaultValues = defaultValues;
            this.cardinality = cardinality;
            this.optionLabels = optionLabels;
            this.optionValues = optionValues;
        }


        public int getCardinality()
        {
            return cardinality;
        }


        public String[] getDefaultValue()
        {
            return defaultValues;
        }


        public String getDescription()
        {
            return description;
        }


        public String getID()
        {
            return id;
        }


        public String getName()
        {
            return name;
        }


        public String[] getOptionLabels()
        {
            return optionLabels;
        }


        public String[] getOptionValues()
        {
            return optionValues;
        }


        public int getType()
        {
            return type;
        }


        public String validate( String arg0 )
        {
            return null;
        }
    }
}