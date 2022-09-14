package org.thymeleaf.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionUtilsTest {



    @Test
    public void testVersionMatches() {

        testVersionMatches("1.0.0.RELEASE", null, false, "1.0.0", "1.0.0.RELEASE", "1.0.0.RELEASE", 1, 0, 0, "RELEASE", null);
        testVersionMatches("1.0.1.RELEASE", null, false, "1.0.1", "1.0.1.RELEASE", "1.0.1.RELEASE", 1, 0, 1, "RELEASE", null);
        testVersionMatches("3.2.1.RELEASE", "2020-01-01T12:30", false, "3.2.1", "3.2.1.RELEASE", "3.2.1.RELEASE (2020-01-01T12:30)", 3, 2, 1, "RELEASE", "2020-01-01T12:30");
        testVersionMatches("3.2.1-RELEASE", "2020-01-01T12:30", false, "3.2.1", "3.2.1-RELEASE", "3.2.1-RELEASE (2020-01-01T12:30)", 3, 2, 1, "RELEASE", "2020-01-01T12:30");
        testVersionMatches("3.2.1-SNAPSHOT", "2020-01-01T12:30", false, "3.2.1", "3.2.1-SNAPSHOT", "3.2.1-SNAPSHOT (2020-01-01T12:30)", 3, 2, 1, "SNAPSHOT", "2020-01-01T12:30");
        testVersionMatches("3.2.1_RELEASE", "2020-01-01T12:30", false, "3.2.1", "3.2.1_RELEASE", "3.2.1_RELEASE (2020-01-01T12:30)", 3, 2, 1, "RELEASE", "2020-01-01T12:30");
        testVersionMatches("3.2.1RELEASE", "2020-01-01T12:30", false, "3.2.1", "3.2.1RELEASE", "3.2.1RELEASE (2020-01-01T12:30)", 3, 2, 1, "RELEASE", "2020-01-01T12:30");
        testVersionMatches("3.2.1R", "2020-01-01T12:30", false, "3.2.1", "3.2.1R", "3.2.1R (2020-01-01T12:30)", 3, 2, 1, "R", "2020-01-01T12:30");
        testVersionMatches("3.2.1 RELEASE", "2020-01-01T12:30", false, "3.2.1", "3.2.1 RELEASE", "3.2.1 RELEASE (2020-01-01T12:30)", 3, 2, 1, "RELEASE", "2020-01-01T12:30");
        testVersionMatches("3.2.1 R", "2020-01-01T12:30", false, "3.2.1", "3.2.1 R", "3.2.1 R (2020-01-01T12:30)", 3, 2, 1, "R", "2020-01-01T12:30");
        testVersionMatches("3.2.1.BUILD-RELEASE", "2020-01-01T12:30", false, "3.2.1", "3.2.1.BUILD-RELEASE", "3.2.1.BUILD-RELEASE (2020-01-01T12:30)", 3, 2, 1, "BUILD-RELEASE", "2020-01-01T12:30");
        testVersionMatches("3.2.1 BUILD-RELEASE", "2020-01-01T12:30", false, "3.2.1", "3.2.1 BUILD-RELEASE", "3.2.1 BUILD-RELEASE (2020-01-01T12:30)", 3, 2, 1, "BUILD-RELEASE", "2020-01-01T12:30");
        testVersionMatches("3.2.1-Final", "2020-01-01T12:30", false, "3.2.1", "3.2.1-Final", "3.2.1-Final (2020-01-01T12:30)", 3, 2, 1, "Final", "2020-01-01T12:30");
        testVersionMatches("3.2.1rc2", "2020-01-01T12:30", false, "3.2.1", "3.2.1rc2", "3.2.1rc2 (2020-01-01T12:30)", 3, 2, 1, "rc2", "2020-01-01T12:30");
        testVersionMatches("33.22.11rc2", "2020-01-01T12:30", false, "33.22.11", "33.22.11rc2", "33.22.11rc2 (2020-01-01T12:30)", 33, 22, 11, "rc2", "2020-01-01T12:30");
        testVersionMatches("3.2.1", "2020-01-01T12:30", false, "3.2.1", "3.2.1", "3.2.1 (2020-01-01T12:30)", 3, 2, 1, null, "2020-01-01T12:30");
        testVersionMatches("3.2.RELEASE", "2020-01-01T12:30", false, "3.2", "3.2.RELEASE", "3.2.RELEASE (2020-01-01T12:30)", 3, 2, 0, "RELEASE", "2020-01-01T12:30");
        testVersionMatches("3.2.BUILD.RELEASE", "2020-01-01T12:30", false, "3.2", "3.2.BUILD.RELEASE", "3.2.BUILD.RELEASE (2020-01-01T12:30)", 3, 2, 0, "BUILD.RELEASE", "2020-01-01T12:30");
        testVersionMatches("3.2-RELEASE", "2020-01-01T12:30", false, "3.2", "3.2-RELEASE", "3.2-RELEASE (2020-01-01T12:30)", 3, 2, 0, "RELEASE", "2020-01-01T12:30");
        testVersionMatches("3.2", "2020-01-01T12:30", false, "3.2", "3.2", "3.2 (2020-01-01T12:30)", 3, 2, 0, null, "2020-01-01T12:30");
        testVersionMatches("3.RELEASE", "2020-01-01T12:30", false, "3", "3.RELEASE", "3.RELEASE (2020-01-01T12:30)", 3, 0, 0, "RELEASE", "2020-01-01T12:30");
        testVersionMatches("3-RELEASE", "2020-01-01T12:30", false, "3", "3-RELEASE", "3-RELEASE (2020-01-01T12:30)", 3, 0, 0, "RELEASE", "2020-01-01T12:30");
        testVersionMatches("3", "2020-01-01T12:30", false, "3", "3", "3 (2020-01-01T12:30)", 3, 0, 0, null, "2020-01-01T12:30");
        testVersionMatches("", null, true, "UNKNOWN", "UNKNOWN", "UNKNOWN", 0, 0, 0, null, null);
        testVersionMatches("", "2020-01-01T12:30", true, "UNKNOWN", "UNKNOWN", "UNKNOWN (2020-01-01T12:30)", 0, 0, 0, null, "2020-01-01T12:30");
        testVersionMatches(null, null, true, "UNKNOWN", "UNKNOWN", "UNKNOWN", 0, 0, 0, null, null);
        testVersionMatches(null, "2020-01-01T12:30", true, "UNKNOWN", "UNKNOWN", "UNKNOWN (2020-01-01T12:30)", 0, 0, 0, null, "2020-01-01T12:30");
        testVersionMatches("a", null, true, "UNKNOWN", "UNKNOWN", "UNKNOWN", 0, 0, 0, null, null);
        testVersionMatches("a", "2020-01-01T12:30", true, "UNKNOWN", "UNKNOWN", "UNKNOWN (2020-01-01T12:30)", 0, 0, 0, null, "2020-01-01T12:30");

    }


    private void testVersionMatches(final String v, final String bt,
                                    final boolean unknown,
                                    final String versionCore, final String version, final String fullVersion,
                                    final int major, final int minor, final int patch,
                                    final String qualifier, final String buildTimestamp)  {

        final VersionUtils.VersionSpec vs =
                (bt != null) ? VersionUtils.parseVersion(v, bt) : VersionUtils.parseVersion(v);

        Assertions.assertEquals(Boolean.valueOf(unknown), Boolean.valueOf(vs.isUnknown()));
        Assertions.assertEquals(versionCore, vs.getVersionCore());
        Assertions.assertEquals(version, vs.getVersion());
        Assertions.assertEquals(fullVersion, vs.getFullVersion());
        Assertions.assertEquals(major, vs.getMajor());
        Assertions.assertEquals(minor, vs.getMinor());
        Assertions.assertEquals(patch, vs.getPatch());
        Assertions.assertEquals(qualifier, vs.getQualifier());
        Assertions.assertEquals(buildTimestamp, vs.getBuildTimestamp());

    }



    @Test
    public void testIsAtLeast() {
        testIsAtLeast(true, "1.0.0", 1, 0, 0);
        testIsAtLeast(false, "1.0.0", 1, 0, 1);
        testIsAtLeast(false, "1.0.0", 1, 1, 0);
        testIsAtLeast(true, "1.0.0", 0, 0, 1);
        testIsAtLeast(true, "2.0.0", 1, 4, 2);
        testIsAtLeast(true, "2.0.0", 1, 4, 2);
        testIsAtLeast(false, "2.0.0", 2, 4, 2);
        testIsAtLeast(false, "2.0.0", 2, 0, 1);
        testIsAtLeast(false, "2.0", 2, 0, 1);
        testIsAtLeast(true, "2.0", 2, 0, 0);
        testIsAtLeast(false, "2.0", 2, 1, 0);
        testIsAtLeast(true, "2", 2, 0, 0);
        testIsAtLeast(false, "2", 2, 0, 1);
        testIsAtLeast(false, "2", 2, 1, 0);
        testIsAtLeast(true, "2.3.4", 2, 2, 9);
        testIsAtLeast(true, "2.3.4", 2, 3, 4);
        testIsAtLeast(false, "2.3.4", 3, 3, 4);
        testIsAtLeast(false, "2.3.4", 3, 0, 0);
        testIsAtLeast(true, "2.3.4", 2, 0, 0);
        testIsAtLeast(true, "2.3.4", 2, 3, 0);
        testIsAtLeast(false, "2.3.4", 2, 4, 4);
        testIsAtLeast(false, "2.3.4", 2, 4, 0);
        testIsAtLeast(false, "2.3.4", 2, 3, 5);
        testIsAtLeast(true, "2.3.4", 2, 3, 3);
        testIsAtLeast(true, "2.3.4", 2, 2, 9);
        testIsAtLeast(true, "2.3.4", 1, 8, 0);
        testIsAtLeast(true, "2.3.4", 1, 8, 9);
        testIsAtLeast(true, "1.0.0.RELEASE", 1, 0, 0);
        testIsAtLeast(false, "1.0.0.RELEASE", 1, 0, 1);
        testIsAtLeast(false, "1.0.0.RELEASE", 1, 1, 0);
        testIsAtLeast(true, "1.0.0.RELEASE", 0, 0, 1);
        testIsAtLeast(true, "2.0.0.RELEASE", 1, 4, 2);
        testIsAtLeast(true, "2.0.0.RELEASE", 1, 4, 2);
        testIsAtLeast(false, "2.0.0.RELEASE", 2, 4, 2);
        testIsAtLeast(false, "2.0.0.RELEASE", 2, 0, 1);
        testIsAtLeast(false, "2.0.RELEASE", 2, 0, 1);
        testIsAtLeast(true, "2.0.RELEASE", 2, 0, 0);
        testIsAtLeast(false, "2.0.RELEASE", 2, 1, 0);
        testIsAtLeast(true, "2.RELEASE", 2, 0, 0);
        testIsAtLeast(false, "2.RELEASE", 2, 0, 1);
        testIsAtLeast(false, "2.RELEASE", 2, 1, 0);
        testIsAtLeast(true, "2.3.4.RELEASE", 2, 2, 9);
        testIsAtLeast(true, "2.3.4.RELEASE", 2, 3, 4);
        testIsAtLeast(false, "2.3.4.RELEASE", 3, 3, 4);
        testIsAtLeast(false, "2.3.4.RELEASE", 3, 0, 0);
        testIsAtLeast(true, "2.3.4.RELEASE", 2, 0, 0);
        testIsAtLeast(true, "2.3.4.RELEASE", 2, 3, 0);
        testIsAtLeast(false, "2.3.4.RELEASE", 2, 4, 4);
        testIsAtLeast(false, "2.3.4.RELEASE", 2, 4, 0);
        testIsAtLeast(false, "2.3.4.RELEASE", 2, 3, 5);
        testIsAtLeast(true, "2.3.4.RELEASE", 2, 3, 3);
        testIsAtLeast(true, "2.3.4.RELEASE", 2, 2, 9);
        testIsAtLeast(true, "2.3.4.RELEASE", 1, 8, 0);
        testIsAtLeast(true, "2.3.4.RELEASE", 1, 8, 9);
        testIsAtLeast(true, "1.0.0-SNAPSHOT", 1, 0, 0);
        testIsAtLeast(false, "1.0.0-SNAPSHOT", 1, 0, 1);
        testIsAtLeast(false, "1.0.0-SNAPSHOT", 1, 1, 0);
        testIsAtLeast(true, "1.0.0-SNAPSHOT", 0, 0, 1);
        testIsAtLeast(true, "2.0.0-SNAPSHOT", 1, 4, 2);
        testIsAtLeast(true, "2.0.0-SNAPSHOT", 1, 4, 2);
        testIsAtLeast(false, "2.0.0-SNAPSHOT", 2, 4, 2);
        testIsAtLeast(false, "2.0.0-SNAPSHOT", 2, 0, 1);
        testIsAtLeast(false, "2.0-SNAPSHOT", 2, 0, 1);
        testIsAtLeast(true, "2.0-SNAPSHOT", 2, 0, 0);
        testIsAtLeast(false, "2.0-SNAPSHOT", 2, 1, 0);
        testIsAtLeast(true, "2-SNAPSHOT", 2, 0, 0);
        testIsAtLeast(false, "2-SNAPSHOT", 2, 0, 1);
        testIsAtLeast(false, "2-SNAPSHOT", 2, 1, 0);
        testIsAtLeast(true, "2.3.4-SNAPSHOT", 2, 2, 9);
        testIsAtLeast(true, "2.3.4-SNAPSHOT", 2, 3, 4);
        testIsAtLeast(false, "2.3.4-SNAPSHOT", 3, 3, 4);
        testIsAtLeast(false, "2.3.4-SNAPSHOT", 3, 0, 0);
        testIsAtLeast(true, "2.3.4-SNAPSHOT", 2, 0, 0);
        testIsAtLeast(true, "2.3.4-SNAPSHOT", 2, 3, 0);
        testIsAtLeast(false, "2.3.4-SNAPSHOT", 2, 4, 4);
        testIsAtLeast(false, "2.3.4-SNAPSHOT", 2, 4, 0);
        testIsAtLeast(false, "2.3.4-SNAPSHOT", 2, 3, 5);
        testIsAtLeast(true, "2.3.4-SNAPSHOT", 2, 3, 3);
        testIsAtLeast(true, "2.3.4-SNAPSHOT", 2, 2, 9);
        testIsAtLeast(true, "2.3.4-SNAPSHOT", 1, 8, 0);
        testIsAtLeast(true, "2.3.4-SNAPSHOT", 1, 8, 9);
        testIsAtLeast(true, "un", 0, 0, 0);
        testIsAtLeast(false, "un", 0, 0, 1);

        final VersionUtils.VersionSpec vs = VersionUtils.parseVersion("2.3.4.RELEASE");
        Assertions.assertTrue(vs.isAtLeast(2));
        Assertions.assertTrue(vs.isAtLeast(2, 3));
        Assertions.assertFalse(vs.isAtLeast(3));
        Assertions.assertFalse(vs.isAtLeast(2, 4));
    }



    private void testIsAtLeast(final boolean result, final String v, final int major, final int minor, final int patch)  {
        final VersionUtils.VersionSpec vs = VersionUtils.parseVersion(v);
        Assertions.assertTrue(result == vs.isAtLeast(major, minor, patch));
    }

}
