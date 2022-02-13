/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.thymeleaf.util;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <p>
 *   Utility class for obtaining a correct classloader on which to operate from a
 *   specific class.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.6
 *
 */
public final class ClassLoaderUtils {


    private static final ClassLoader classClassLoader;
    private static final ClassLoader systemClassLoader;
    private static final boolean systemClassLoaderAccessibleFromClassClassLoader;


    static {
        classClassLoader = getClassClassLoader(ClassLoaderUtils.class);
        systemClassLoader = getSystemClassLoader();
        systemClassLoaderAccessibleFromClassClassLoader = isKnownClassLoaderAccessibleFrom(systemClassLoader, classClassLoader);
    }

    
    
    /**
     * <p>
     *   Try to obtain a classloader, following these priorities:
     * </p>
     * <ol>
     *   <li>If there is a <i>thread context class loader</i>, return it.</li>
     *   <li>Else if there is a class loader related to the class passed as argument, return it.</li>
     *   <li>Else return the <i>system class loader</i>.</li>
     * </ol>
     * 
     * @param clazz the class which loader will be obtained in the second step. Can be null (that will
     *              skip that second step).
     * @return a non-null, safe classloader to use.
     */
    public static ClassLoader getClassLoader(final Class<?> clazz) {
        // Context class loader can be null
        final ClassLoader contextClassLoader = getThreadContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        if (clazz != null) {
            // The class loader for a specific class can also be null
            final ClassLoader clazzClassLoader = getClassClassLoader(clazz);
            if (clazzClassLoader != null) {
                return clazzClassLoader;
            }
        }
        // The only class loader we can rely on for not being null is the system one
        return systemClassLoader;
    }


    /**
     * <p>
     *   Obtain a class by name, throwing an exception if it is not present.
     * </p>
     * <p>
     *   First the <em>context class loader</em> will be used. If this class loader is not
     *   able to load the class, then the <em>class class loader</em>
     *   ({@code ClassLoaderUtils.class.getClassLoader()}) will be used if it is different from
     *   the thread context one. Last, the System class loader will be tried.
     * </p>
     * <p>
     *   This method does never return {@code null}.
     * </p>
     *
     * @param className the name of the class to be obtained.
     * @return the loaded class (null never returned).
     * @throws ClassNotFoundException if the class could not be loaded.
     *
     * @since 3.0.3
     *
     */
    public static Class<?> loadClass(final String className) throws ClassNotFoundException {

        ClassNotFoundException notFoundException = null;

        // First try the context class loader
        final ClassLoader contextClassLoader = getThreadContextClassLoader();
        if (contextClassLoader != null) {
            try {
                return Class.forName(className, false, contextClassLoader);
            } catch (final ClassNotFoundException cnfe) {
                notFoundException = cnfe;
                // Pass-through, there might be other ways of obtaining it
                // note anyway that this is not really normal: the context class loader should be
                // either able to resolve any of our application's classes, or to delegate to a class
                // loader that can do that.
            }
        }

        // The thread context class loader might have already delegated to both the class
        // and system class loaders, in which case it makes little sense to query them too.
        if (!isKnownLeafClassLoader(contextClassLoader)) {

            // The context class loader didn't help, so... maybe the class one?
            if (classClassLoader != null && classClassLoader != contextClassLoader) {
                try {
                    return Class.forName(className, false, classClassLoader);
                } catch (final ClassNotFoundException cnfe) {
                    if (notFoundException == null) {
                        notFoundException = cnfe;
                    }
                    // Pass-through, maybe the system class loader can do it? - though it would be *really* weird...
                }
            }

            if (!systemClassLoaderAccessibleFromClassClassLoader) {

                // The only class loader we can rely on for not being null is the system one
                if (systemClassLoader != null && systemClassLoader != contextClassLoader && systemClassLoader != classClassLoader) {
                    try {
                        return Class.forName(className, false, systemClassLoader);
                    } catch (final ClassNotFoundException cnfe) {
                        if (notFoundException == null) {
                            notFoundException = cnfe;
                        }
                        // Pass-through, anyway we have a return null after this...
                    }
                }

            }

        }

        // If we have to throw an exception, we will do so with the highest-level one just in case it provides
        // a useful message. So preferably we will throw the one coming from the thread context class loader,
        // if not then the class class loader, etc.
        throw notFoundException;

    }


    /**
     * <p>
     *   Try to obtain a class by name, returning {@code null} if not found.
     * </p>
     * <p>
     *   This method works very similarly to {@link #loadClass(String)} but will just return {@code null}
     *   if the class is not found by the sequence of class loaders being tried.
     * </p>
     *
     * @param className the name of the class to be obtained.
     * @return the found class, or {@code null} if it could not be found.
     *
     * @since 3.0.3
     *
     */
    public static Class<?> findClass(final String className) {
        try {
            return loadClass(className);
        } catch (final ClassNotFoundException cnfe) {
            // ignored, we will just return null in this case
            return null;
        }
    }


    /**
     * <p>
     *   Checks whether a class is present at the application's class path.
     * </p>
     * <p>
     *   This method works very similarly to {@link #findClass(String)} but will just return {@code true}
     *   or {@code false} depending on whether the class could be found or not.
     * </p>
     *
     * @param className the name of the class to be checked.
     * @return {@code true} if the class was found (by any class loader), {@code false} if not.
     *
     * @since 3.0.3
     *
     */
    public static boolean isClassPresent(final String className) {
        return findClass(className) != null;
    }




    /**
     * <p>
     *   Try to obtain a resource by name, returning {@code null} if it could not be located.
     * </p>
     * <p>
     *   First the <em>context class loader</em> will be used. If this class loader is not
     *   able to locate the resource, then the <em>class class loader</em>
     *   ({@code ClassLoaderUtils.class.getClassLoader()}) will be used if it is different from
     *   the thread context one. Last, the System class loader will be tried.
     * </p>
     *
     * @param resourceName the name of the resource to be obtained.
     * @return the found resource, or {@code null} if it could not be located.
     *
     * @since 3.0.3
     *
     */
    public static URL findResource(final String resourceName) {

        // First try the context class loader
        final ClassLoader contextClassLoader = getThreadContextClassLoader();
        if (contextClassLoader != null) {
            final URL url = contextClassLoader.getResource(resourceName);
            if (url != null) {
                return url;
            }
            // Pass-through, there might be other ways of obtaining it
            // note anyway that this is not really normal: the context class loader should be
            // either able to resolve any of our application's resources, or to delegate to a class
            // loader that can do that.
        }

        // The thread context class loader might have already delegated to both the class
        // and system class loaders, in which case it makes little sense to query them too.
        if (!isKnownLeafClassLoader(contextClassLoader)) {

            // The context class loader didn't help, so... maybe the class one?
            if (classClassLoader != null && classClassLoader != contextClassLoader) {
                final URL url = classClassLoader.getResource(resourceName);
                if (url != null) {
                    return url;
                }
                // Pass-through, maybe the system class loader can do it? - though it would be *really* weird...
            }

            if (!systemClassLoaderAccessibleFromClassClassLoader) {

                // The only class loader we can rely on for not being null is the system one
                if (systemClassLoader != null && systemClassLoader != contextClassLoader && systemClassLoader != classClassLoader) {
                    final URL url = systemClassLoader.getResource(resourceName);
                    if (url != null) {
                        return url;
                    }
                    // Pass-through, anyway we have a return null after this...
                }

            }

        }

        return null;

    }


    /**
     * <p>
     *   Checks whether a resource is present at the application's class path.
     * </p>
     * <p>
     *   This method works very similarly to {@link #findResource(String)} but will just return {@code true}
     *   or {@code false} depending on whether the resource could be located or not.
     * </p>
     *
     * @param resourceName the name of the resource to be checked.
     * @return {@code true} if the class was located (by any class loader), {@code false} if not.
     *
     * @since 3.0.3
     *
     */
    public static boolean isResourcePresent(final String resourceName) {
        return findResource(resourceName) != null;
    }




    /**
     * <p>
     *   Obtain a resource by name, throwing an exception if it is not present.
     * </p>
     * <p>
     *   First the <em>context class loader</em> will be used. If this class loader is not
     *   able to locate the resource, then the <em>class class loader</em>
     *   ({@code ClassLoaderUtils.class.getClassLoader()}) will be used if it is different from
     *   the thread context one. Last, the System class loader will be tried.
     * </p>
     * <p>
     *   This method does never return {@code null}.
     * </p>
     *
     * @param resourceName the name of the resource to be obtained.
     * @return an input stream on the resource (null never returned).
     * @throws IOException if the resource could not be located.
     *
     * @since 3.0.3
     *
     */
    public static InputStream loadResourceAsStream(final String resourceName) throws IOException {

        final InputStream inputStream = findResourceAsStream(resourceName);
        if (inputStream != null) {
            return inputStream;
        }

        // No way to obtain that resource, so we must raise an IOException
        throw new IOException("Could not locate resource '" + resourceName + "' in the application's class path");

    }


    /**
     * <p>
     *   Try to obtain a resource by name, returning {@code null} if it could not be located.
     * </p>
     * <p>
     *   This method works very similarly to {@link #loadResourceAsStream(String)} but will just return {@code null}
     *   if the resource cannot be located by the sequence of class loaders being tried.
     * </p>
     *
     * @param resourceName the name of the resource to be obtained.
     * @return an input stream on the resource, or {@code null} if it could not be located.
     *
     * @since 3.0.3
     *
     */
    public static InputStream findResourceAsStream(final String resourceName) {

        // First try the context class loader
        final ClassLoader contextClassLoader = getThreadContextClassLoader();
        if (contextClassLoader != null) {
            final InputStream inputStream = contextClassLoader.getResourceAsStream(resourceName);
            if (inputStream != null) {
                return inputStream;
            }
            // Pass-through, there might be other ways of obtaining it
            // note anyway that this is not really normal: the context class loader should be
            // either able to resolve any of our application's resources, or to delegate to a class
            // loader that can do that.
        }

        // The thread context class loader might have already delegated to both the class
        // and system class loaders, in which case it makes little sense to query them too.
        if (!isKnownLeafClassLoader(contextClassLoader)) {

            // The context class loader didn't help, so... maybe the class one?
            if (classClassLoader != null && classClassLoader != contextClassLoader) {
                final InputStream inputStream = classClassLoader.getResourceAsStream(resourceName);
                if (inputStream != null) {
                    return inputStream;
                }
                // Pass-through, maybe the system class loader can do it? - though it would be *really* weird...
            }

            if (!systemClassLoaderAccessibleFromClassClassLoader) {

                // The only class loader we can rely on for not being null is the system one
                if (systemClassLoader != null && systemClassLoader != contextClassLoader && systemClassLoader != classClassLoader) {
                    final InputStream inputStream = systemClassLoader.getResourceAsStream(resourceName);
                    if (inputStream != null) {
                        return inputStream;
                    }
                    // Pass-through, anyway we have a return null after this...
                }

            }

        }

        return null;

    }




    /*
     * This will return the thread context class loader if it is possible to access it
     * (depending on security restrictions)
     */
    private static ClassLoader getThreadContextClassLoader() {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (final SecurityException se) {
            // The SecurityManager prevents us from accessing, so just ignore it
            return null;
        }
    }


    /*
     * This will return the class class loader if it is possible to access it
     * (depending on security restrictions)
     */
    private static ClassLoader getClassClassLoader(final Class<?> clazz) {
        try {
            return clazz.getClassLoader();
        } catch (final SecurityException se) {
            // The SecurityManager prevents us from accessing, so just ignore it
            return null;
        }
    }


    /*
     * This will return the system class loader if it is possible to access it
     * (depending on security restrictions)
     */
    private static ClassLoader getSystemClassLoader() {
        try {
            return ClassLoader.getSystemClassLoader();
        } catch (final SecurityException se) {
            // The SecurityManager prevents us from accessing, so just ignore it
            return null;
        }
    }


    /*
     * This method determines whether it is known that a this class loader is a a child of another one, or equal to it.
     * The "known" part is because SecurityManager could be preventing us from knowing such information.
     */
    private static boolean isKnownClassLoaderAccessibleFrom(final ClassLoader accessibleCL, final ClassLoader fromCL) {

        if (fromCL == null) {
            return false;
        }

        ClassLoader parent = fromCL;

        try {

            while (parent != null && parent != accessibleCL) {
                parent = parent.getParent();
            }

            return (parent != null && parent == accessibleCL);

        } catch (final SecurityException se) {
            // The SecurityManager prevents us from accessing, so just ignore it
            return false;
        }

    }


    /*
     * This method determines whether it is known that a this class loader is a "leaf", in the sense that
     * going up through its hierarchy we are able to find both the class class loader and the system class
     * loader. This is used for determining whether we should be confident on the thread-context class loader
     * delegation mechanism or rather try to perform class/resource resolution manually on the other class loaders.
     */
    private static boolean isKnownLeafClassLoader(final ClassLoader classLoader) {

        if (classLoader == null) {
            return false;
        }

        if (!isKnownClassLoaderAccessibleFrom(classClassLoader, classLoader)) {
            // We cannot access the class class loader from the specified class loader, so this is not a leaf
            return false;
        }

        // Now we know there is a way to reach the class class loader from the argument class loader, so we should
        // base or results on whether there is a way to reach the system class loader from the class class loader.
        return systemClassLoaderAccessibleFromClassClassLoader;

    }



    
    private ClassLoaderUtils() {
        super();
    }
    
    
    
}
