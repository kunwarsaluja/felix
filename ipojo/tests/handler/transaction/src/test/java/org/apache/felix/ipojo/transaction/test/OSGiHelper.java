package org.apache.felix.ipojo.transaction.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

import static org.junit.Assert.fail;


public class OSGiHelper {
    
    /**
     * The bundle context.
     */
    private BundleContext context;
    
    /**
     * List of get references.
     */
    private List<ServiceReference> m_references = new ArrayList<ServiceReference>();
    
    public OSGiHelper(BundleContext context) {
        this.context = context;
    }
    
    public void dispose() {
        // Unget services
        for (int i = 0; i < m_references.size(); i++) {
            context.ungetService((ServiceReference) m_references.get(i));
        }
        m_references.clear();
    }
    
    /**
     * Gets the Bundle Context.
     * @return the bundle context.
     */
    public BundleContext getContext() {
        return context;
    }
    
    /**
     * Returns the service object of a service provided by the specified bundle,
     * offering the specified interface and matching the given filter.
     * 
     * @param bundle the bundle from which the service is searched.
     * @param itf the interface provided by the searched service.
     * @param filter an additional filter (can be {@code null}).
     * @return the service object provided by the specified bundle, offering the
     *         specified interface and matching the given filter.
     */
    public static Object getServiceObject(Bundle bundle, String itf,
            String filter) {
        ServiceReference ref = getServiceReference(bundle, itf, filter);
        if (ref != null) {
            return bundle.getBundleContext().getService(ref);
        } else {
            return null;
        }
    }

    /**
     * Returns the service objects of the services provided by the specified
     * bundle, offering the specified interface and matching the given filter.
     * 
     * @param bundle the bundle from which services are searched.
     * @param itf the interface provided by the searched services.
     * @param filter an additional filter (can be {@code null}).
     * @return the service objects provided by the specified bundle, offering
     *         the specified interface and matching the given filter.
     */
    public static Object[] getServiceObjects(Bundle bundle, String itf,
            String filter) {
        ServiceReference[] refs = getServiceReferences(bundle, itf, filter);
        if (refs != null) {
            Object[] list = new Object[refs.length];
            for (int i = 0; i < refs.length; i++) {
                list[i] = bundle.getBundleContext().getService(refs[i]);
            }
            return list;
        } else {
            return new Object[0];
        }
    }

    /**
     * Returns the service reference of a service provided by the specified
     * bundle, offering the specified interface and matching the given filter.
     * 
     * @param bundle the bundle from which the service is searched.
     * @param itf the interface provided by the searched service.
     * @param filter an additional filter (can be {@code null}).
     * @return a service reference provided by the specified bundle, offering
     *         the specified interface and matching the given filter. If no
     *         service is found, {@code null} is returned.
     */
    public static ServiceReference getServiceReference(Bundle bundle,
            String itf, String filter) {
        ServiceReference[] refs = getServiceReferences(bundle, itf, filter);
        if (refs.length != 0) {
            return refs[0];
        } else {
            // No service found
            return null;
        }
    }

    /**
     * Checks if the service is available.
     * @param itf the service interface
     * @return <code>true</code> if the service is available, <code>false</code>
     *         otherwise.
     */
    public boolean isServiceAvailable(String itf) {
        ServiceReference ref = getServiceReference(itf, null);
        return ref != null;
    }

    /**
     * Checks if the service is available.
     * @param itf the service interface
     * @param pid the service pid
     * @return <code>true</code> if the service is available, <code>false</code>
     *         otherwise.
     */
    public boolean isServiceAvailableByPID(String itf, String pid) {
        ServiceReference ref = getServiceReferenceByPID(itf, pid);
        return ref != null;
    }

    /**
     * Returns the service reference of the service provided by the specified
     * bundle, offering the specified interface and having the given persistent
     * ID.
     * 
     * @param bundle the bundle from which the service is searched.
     * @param itf the interface provided by the searched service.
     * @param pid the persistent ID of the searched service.
     * @return a service provided by the specified bundle, offering the
     *         specified interface and having the given persistent ID.
     */
    public static ServiceReference getServiceReferenceByPID(Bundle bundle,
            String itf, String pid) {
        String filter = "(" + "service.pid" + "=" + pid + ")";
        ServiceReference[] refs = getServiceReferences(bundle, itf, filter);
        if (refs == null) {
            return null;
        } else if (refs.length == 1) {
            return refs[0];
        } else {
            throw new IllegalStateException(
                    "A service lookup by PID returned several providers ("
                            + refs.length + ")" + " for " + itf + " with pid="
                            + pid);
        }
    }

    /**
     * Returns the service reference of all the services provided in the
     * specified bundle, offering the specified interface and matching the given
     * filter.
     * 
     * @param bundle the bundle from which services are searched.
     * @param itf the interface provided by the searched services.
     * @param filter an additional filter (can be {@code null}).
     * @return all the service references provided in the specified bundle,
     *         offering the specified interface and matching the given filter.
     *         If no service matches, an empty array is returned.
     */
    public static ServiceReference[] getServiceReferences(Bundle bundle,
            String itf, String filter) {
        ServiceReference[] refs = null;
        try {
            // Get all the service references
            refs = bundle.getBundleContext().getServiceReferences(itf, filter);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException(
                    "Cannot get service references: " + e.getMessage());
        }
        if (refs == null) {
            return new ServiceReference[0];
        } else {
            return refs;
        }
    }

    /**
     * Returns the service object of a service provided by the local bundle,
     * offering the specified interface and matching the given filter.
     * 
     * @param itf the interface provided by the searched service.
     * @param filter an additional filter (can be {@code null}).
     * @return the service object provided by the local bundle, offering the
     *         specified interface and matching the given filter.
     */
    public Object getServiceObject(String itf, String filter) {
        ServiceReference ref = getServiceReference(itf, filter);
        if (ref != null) {
            m_references.add(ref);
            return context.getService(ref);
        } else {
            return null;
        }
    }

    /**
     * Returns the service object associated with this service reference.
     * 
     * @param ref service reference
     * @return the service object.
     */
    public Object getServiceObject(ServiceReference ref) {
        if (ref != null) {
            m_references.add(ref);
            return context.getService(ref);
        } else {
            return null;
        }
    }

    /**
     * Returns the service objects of the services provided by the local bundle,
     * offering the specified interface and matching the given filter.
     * 
     * @param itf the interface provided by the searched services.
     * @param filter an additional filter (can be {@code null}).
     * @return the service objects provided by the local bundle, offering the
     *         specified interface and matching the given filter.
     */
    public Object[] getServiceObjects(String itf, String filter) {
        ServiceReference[] refs = getServiceReferences(itf, filter);
        if (refs != null) {
            Object[] list = new Object[refs.length];
            for (int i = 0; i < refs.length; i++) {
                m_references.add(refs[i]);
                list[i] = context.getService(refs[i]);
            }
            return list;
        } else {
            return new Object[0];
        }
    }

    /**
     * Returns the service reference of a service provided by the local bundle,
     * offering the specified interface and matching the given filter.
     * 
     * @param itf the interface provided by the searched service.
     * @param filter an additional filter (can be {@code null}).
     * @return a service reference provided by the local bundle, offering the
     *         specified interface and matching the given filter. If no service
     *         is found, {@code null} is returned.
     */
    public ServiceReference getServiceReference(String itf, String filter) {
        return getServiceReference(context.getBundle(), itf, filter);
    }

    /**
     * Returns the service reference of a service provided offering the
     * specified interface.
     * 
     * @param itf the interface provided by the searched service.
     * @return a service reference provided by the local bundle, offering the
     *         specified interface and matching the given filter. If no service
     *         is found, {@code null} is returned.
     */
    public ServiceReference getServiceReference(String itf) {
        return getServiceReference(context.getBundle(), itf, null);
    }

    /**
     * Returns the service reference of the service provided by the local
     * bundle, offering the specified interface and having the given persistent
     * ID.
     * 
     * @param itf the interface provided by the searched service.
     * @param pid the persistent ID of the searched service.
     * @return a service provided by the local bundle, offering the specified
     *         interface and having the given persistent ID.
     */
    public ServiceReference getServiceReferenceByPID(String itf, String pid) {
        return getServiceReferenceByPID(context.getBundle(), itf, pid);
    }

    /**
     * Returns the service reference of all the services provided in the local
     * bundle, offering the specified interface and matching the given filter.
     * 
     * @param itf the interface provided by the searched services.
     * @param filter an additional filter (can be {@code null}).
     * @return all the service references provided in the local bundle, offering
     *         the specified interface and matching the given filter. If no
     *         service matches, an empty array is returned.
     */
    public ServiceReference[] getServiceReferences(String itf, String filter) {
        return getServiceReferences(context.getBundle(), itf, filter);
    }
    
    /**
     * Gets the package admin exposed by the framework.
     * Fails if the package admin is not available. 
     * @return the package admin service.
     */
    public PackageAdmin getPackageAdmin() {
        PackageAdmin pa = (PackageAdmin) getServiceObject(PackageAdmin.class.getName(), null);
        if (pa == null) {
            fail("No package admin available");
        }
        return pa;
    }
    
    /**
     * Refresh the packages.
     * Fails if the package admin service is not available.
     */
    public void refresh() {
        getPackageAdmin().refreshPackages(null);
    }
    
    /**
     * Waits for a service. Fails on timeout.
     * If timeout is set to 0, it sets the timeout to 10s.
     * @param itf the service interface
     * @param filter  the filter
     * @param timeout the timeout
     */
    public void waitForService(String itf, String filter, long timeout) {
        if (timeout == 0) {
            timeout = 10000; // Default 10 secondes.
        }
        ServiceReference[] refs = getServiceReferences(itf, filter);
        long begin = System.currentTimeMillis();
        if (refs.length != 0) {
            return;
        } else {
            while(refs.length == 0) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // Interrupted
                }
                long now = System.currentTimeMillis();
                
                if ((now - begin) > timeout) {
                    fail("Timeout ... no services matching with the request after " + timeout + "ms");
                }
                refs = getServiceReferences(itf, filter);
            }
        }
    }
    
    
    /**
     * Installs a bundle.
     * Fails if the bundle cannot be installed.
     * Be aware that you have to uninstall the bundle yourself.
     * @param url bundle url
     * @return the installed bundle
     */
    public Bundle installBundle(String url) {
        try {
            return context.installBundle(url);
        } catch (BundleException e) {
            fail("Cannot install the bundle " + url + " : " + e.getMessage());
        }
        return null; // Can not happen
    }
    
    /**
     * Installs a bundle.
     * Fails if the bundle cannot be installed.
     * Be aware that you have to uninstall the bundle yourself.
     * @param url bundle url
     * @param stream input stream containing the bundle
     * @return the installed bundle
     */
    public Bundle installBundle(String url, InputStream stream) {
        try {
            return context.installBundle(url, stream);
        } catch (BundleException e) {
            fail("Cannot install the bundle " + url + " : " + e.getMessage());
        }
        return null; // Can not happen
    }
    
    /**
     * Installs and starts a bundle.
     * Fails if the bundle cannot be installed or an error occurs
     * during startup. Be aware that you have to uninstall the bundle
     * yourself.
     * @param url the bundle url
     * @return the Bundle object.
     */
    public Bundle installAndStart(String url) {
        Bundle bundle = installBundle(url);
        try {
            bundle.start();
        } catch (BundleException e) {
           fail("Cannot start the bundle " + url + " : " + e.getMessage());
        }
        return bundle;
    }
    
    /**
     * Installs and starts a bundle.
     * Fails if the bundle cannot be installed or an error occurs
     * during startup. Be aware that you have to uninstall the bundle
     * yourself.
     * @param url the bundle url
     * @param stream input stream containing the bundle
     * @return the Bundle object.
     */
    public Bundle installAndStart(String url, InputStream stream) {
        Bundle bundle = installBundle(url, stream);
        try {
            bundle.start();
        } catch (BundleException e) {
           fail("Cannot start the bundle " + url + " : " + e.getMessage());
        }
        return bundle;
    }
    
    /**
     * Get the bundle by its id.
     * @param bundleId the bundle id.
     * @return the bundle with the given id.
     */
    public Bundle getBundle(long bundleId) {
        return context.getBundle(bundleId);
    }
    
    /**
     * Gets a bundle by its symbolic name.
     * Fails if no bundle matches.
     * @param name the symbolic name of the bundle
     * @return the bundle object.
     */
    public Bundle getBundle(String name) {
        Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            if (name.equals(bundles[i].getSymbolicName())) {
                return bundles[i];
            }
        }
        fail("No bundles with the given symbolic name " + name);
        return null; // should not happen
    }

}
