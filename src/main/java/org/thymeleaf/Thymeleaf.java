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

    public static final String VERSION;
    public static final String BUILD_TIMESTAMP;

    public static final int VERSION_MAJOR;
    public static final int VERSION_MINOR;
    public static final int VERSION_BUILD;
    public static final String VERSION_TYPE;


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

        VERSION = version;
        BUILD_TIMESTAMP = buildTimestamp;

        if (VERSION == null || VERSION.trim().length() == 0) {

            VERSION_MAJOR = 0;
            VERSION_MINOR = 0;
            VERSION_BUILD = 0;
            VERSION_TYPE = "UNKNOWN";

        } else {

            try {

                String versionRemainder = VERSION;

                int separatorIdx = versionRemainder.indexOf('.');
                VERSION_MAJOR = Integer.parseInt(versionRemainder.substring(0,separatorIdx));
                versionRemainder = versionRemainder.substring(separatorIdx + 1);

                separatorIdx = versionRemainder.indexOf('.');
                VERSION_MINOR = Integer.parseInt(versionRemainder.substring(0, separatorIdx));
                versionRemainder = versionRemainder.substring(separatorIdx + 1);

                separatorIdx = versionRemainder.indexOf('.');
                if (separatorIdx < 0) {
                    separatorIdx = versionRemainder.indexOf('-');
                }
                VERSION_BUILD = Integer.parseInt(versionRemainder.substring(0, separatorIdx));
                VERSION_TYPE = versionRemainder.substring(separatorIdx + 1);

            } catch (final Exception e) {
                throw new ExceptionInInitializerError(
                        "Exception during initialization of Thymeleaf versioning utilities. Identified Thymeleaf " +
                        "version is '" + VERSION + "', which does not follow the {major}.{minor}.{build}[.|-]{type} " +
                        "scheme");
            }

        }

    }


    public static boolean isVersionStableRelease() {
        return "RELEASE".equals(VERSION_TYPE);
    }



    private Thymeleaf() {
        super();
    }

}
