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
package org.thymeleaf;

import java.util.Properties;

import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.VersionUtils;


/**
 * <p>
 *   Class meant to keep some constants related to the version of the Thymeleaf library being used, build date, etc.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class Thymeleaf {

    private static final String STABLE_RELEASE_QUALIFIER = "RELEASE";

    /**
     * @deprecated Deprecated in 3.0.12. Use {@link #getVersion()} instead. Will be removed in Thymeleaf 3.1.
     */
    public static final String VERSION;
    /**
     * @deprecated Deprecated in 3.0.12. Use {@link #getBuildTimestamp()} instead. Will be removed in Thymeleaf 3.1.
     */
    public static final String BUILD_TIMESTAMP;
    /**
     * @deprecated Deprecated in 3.0.12. Use {@link #getVersionMajor()} instead. Will be removed in Thymeleaf 3.1.
     */
    public static final int VERSION_MAJOR;
    /**
     * @deprecated Deprecated in 3.0.12. Use {@link #getVersionMinor()} instead. Will be removed in Thymeleaf 3.1.
     */
    public static final int VERSION_MINOR;
    /**
     * @deprecated Deprecated in 3.0.12. Use {@link #getVersionPatch()} instead. Will be removed in Thymeleaf 3.1.
     */
    public static final int VERSION_BUILD;
    /**
     * @deprecated Deprecated in 3.0.12. Use {@link #getVersionQualifier()} instead. Will be removed in Thymeleaf 3.1.
     */
    public static final String VERSION_TYPE;


    private static final VersionUtils.VersionSpec VERSION_SPEC;


    static {

        String version = null;
        String buildTimestamp = null;
        try {
            final Properties properties = new Properties();
            properties.load(ClassLoaderUtils.loadResourceAsStream("org/thymeleaf/thymeleaf.properties"));
            version = properties.getProperty("version");
            buildTimestamp = properties.getProperty("build.date");
        } catch (final Exception ignored) {
            // Ignored: we don't have such information, might be due to IDE configuration
        }


        VERSION_SPEC = VersionUtils.parseVersion(version, buildTimestamp);

        VERSION = VERSION_SPEC.getVersion();
        BUILD_TIMESTAMP = VERSION_SPEC.getBuildTimestamp();
        VERSION_MAJOR = VERSION_SPEC.getMajor();
        VERSION_MINOR = VERSION_SPEC.getMinor();
        VERSION_BUILD = VERSION_SPEC.getPatch();
        VERSION_TYPE = VERSION_SPEC.getQualifier();

    }







    public static String getVersion() {
        return VERSION_SPEC.getVersion();
    }

    public static String getBuildTimestamp() {
        return VERSION_SPEC.getBuildTimestamp();
    }

    public static int getVersionMajor() {
        return VERSION_SPEC.getMajor();
    }

    public static int getVersionMinor() {
        return VERSION_SPEC.getMinor();
    }

    public static int getVersionPatch() {
        return VERSION_SPEC.getPatch();
    }

    public static String getVersionQualifier() {
        return VERSION_SPEC.getQualifier();
    }


    public static boolean isVersionStableRelease() {
        return STABLE_RELEASE_QUALIFIER.equals(VERSION_SPEC.getQualifier());
    }



    private Thymeleaf() {
        super();
    }

}
