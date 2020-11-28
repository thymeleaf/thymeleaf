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


/**
 * <p>
 *   Utility class for processing version numbers.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.12
 *
 */
public final class VersionUtils {


    public static VersionSpec parseVersion(final String version) {
        return parseVersion(version, null);
    }


    public static VersionSpec parseVersion(final String version, final String buildTimestamp) {

        if (version == null || version.trim().length() == 0) {

            // Build an UNKNOWN version
            return new VersionSpec(buildTimestamp);

        } else {

            try {

                final String ver = version.trim();

                final String numVer;
                final Character qualifierSeparator;
                final String qualifier;

                final int endOfNumericVersionIdx = findEndOfNumericVersion(ver);
                if (endOfNumericVersionIdx < 0) {
                    numVer = ver;
                    qualifierSeparator = null;
                    qualifier = null;
                } else {
                    numVer = ver.substring(0, endOfNumericVersionIdx);
                    final char c = ver.charAt(endOfNumericVersionIdx);
                    if (Character.isLetter(c)) {
                        qualifierSeparator = null;
                        qualifier = ver.substring(endOfNumericVersionIdx);
                    } else {
                        qualifierSeparator = Character.valueOf(ver.charAt(endOfNumericVersionIdx));
                        qualifier = ver.substring(endOfNumericVersionIdx + 1);
                        if (qualifier == null || qualifier.trim().length() == 0) {
                            // Build an UNKNOWN version
                            return new VersionSpec(buildTimestamp);
                        }
                    }
                }


                final int major;
                final Integer minor;
                final Integer patch;

                final int separator1Idx = numVer.indexOf('.');
                if (separator1Idx < 0) {
                    major = Integer.parseInt(numVer);
                    minor = null;
                    patch = null;
                } else {
                    major = Integer.parseInt(numVer.substring(0, separator1Idx));
                    final int separator2Idx = numVer.indexOf('.', separator1Idx + 1);
                    if (separator2Idx < 0) {
                        minor = Integer.valueOf(numVer.substring(separator1Idx + 1));
                        patch = null;
                    } else {
                        minor = Integer.valueOf(numVer.substring(separator1Idx + 1, separator2Idx));
                        patch = Integer.valueOf(numVer.substring(separator2Idx + 1));
                    }
                }

                return new VersionSpec(major, minor, patch, qualifierSeparator, qualifier, buildTimestamp);

            } catch (final Exception e) {
                // Build an UNKNOWN version
                return new VersionSpec(buildTimestamp);
            }

        }

    }


    private static int findEndOfNumericVersion(final CharSequence sequence) {
        final int seqLen = sequence.length();
        char c;
        for (int i = 0; i < seqLen; i++) {
            c = sequence.charAt(i);
            if (c != '.' && !Character.isDigit(c)) {
                if (i > 1 && sequence.charAt(i - 1) == '.') {
                    return i - 1;
                }
                return i;
            }
        }
        return -1;
    }




    private VersionUtils() {
        super();
    }



    public static final class VersionSpec {

        private static final String UNKNOWN_VERSION = "UNKNOWN";

        private boolean unknown;

        private int major;
        private int minor;
        private int patch;
        private String qualifier;
        private String buildTimestamp;

        private String versionCore;
        private String version;
        private String fullVersion;



        // UNKNOWN version
        private VersionSpec(final String buildTimestamp) {

            super();

            this.unknown = true;

            this.major = 0;
            this.minor = 0;
            this.patch = 0;
            this.qualifier = null;
            this.buildTimestamp = buildTimestamp;

            this.versionCore = UNKNOWN_VERSION;
            this.version = this.versionCore;

            // Version might be UNKNOWN but still full version specify a build timestamp
            this.fullVersion =
                    (this.buildTimestamp != null) ?
                            String.format("%s (%s)", this.version, this.buildTimestamp) : this.version;

        }


        private VersionSpec(
                final int major, final Integer minor, final Integer patch,
                final Character qualifierSeparator, final String qualifier,
                final String buildTimestamp) {

            super();

            Validate.isTrue(major >= 0, "Major version must be >= 0");
            Validate.isTrue(minor == null || minor.intValue() >= 0, "Minor version must be >= 0");
            Validate.isTrue(patch == null || patch.intValue() >= 0, "Patch version must be >= 0");
            Validate.isTrue(patch == null || minor != null, "Patch version present without minor");

            this.unknown = false;

            this.major = major;
            this.minor = (minor != null) ? minor.intValue() : 0;
            this.patch = (patch != null) ? patch.intValue() : 0;
            this.qualifier = qualifier;
            this.buildTimestamp = buildTimestamp;

            this.versionCore =
                    (patch != null) ?
                            String.format("%d.%d.%d", Integer.valueOf(major), minor, patch) :
                            (minor != null) ?
                                    String.format("%d.%d", Integer.valueOf(major), minor) : String.valueOf(major);

            this.version =
                    (qualifier == null) ?
                            this.versionCore :
                            (qualifierSeparator != null) ?
                                    String.format("%s%c%s", this.versionCore, qualifierSeparator, qualifier) :
                                    String.format("%s%s", this.versionCore, qualifier);

            // Version might be UNKNOWN but still full version specify a build timestamp
            this.fullVersion =
                    (buildTimestamp != null) ?
                            String.format("%s (%s)", this.version, this.buildTimestamp) : this.version;

        }

        public boolean isUnknown() {
            return this.unknown;
        }

        public int getMajor() {
            return this.major;
        }

        public int getMinor() {
            return this.minor;
        }

        public int getPatch() {
            return this.patch;
        }

        public boolean hasQualifier() {
            return this.qualifier != null;
        }

        public String getQualifier() {
            return this.qualifier;
        }

        public String getVersionCore() {
            return this.versionCore;
        }

        public String getVersion() {
            return this.version;
        }

        public boolean hasBuildTimestamp() {
            return this.buildTimestamp != null;
        }

        public String getBuildTimestamp() {
            return this.buildTimestamp;
        }

        public String getFullVersion() {
            return this.fullVersion;
        }

        public boolean isAtLeast(final int major) {
            return isAtLeast(major, 0);
        }

        public boolean isAtLeast(final int major, final int minor) {
            return isAtLeast(major, minor, 0);
        }

        public boolean isAtLeast(final int major, final int minor, final int patch) {
            return this.major > major ||
                    (this.major == major && this.minor > minor) ||
                    (this.major == major && this.minor == minor && this.patch >= patch);
        }

    }


}