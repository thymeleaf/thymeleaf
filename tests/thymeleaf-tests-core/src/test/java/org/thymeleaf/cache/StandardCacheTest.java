/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;


public class StandardCacheTest {

    public StandardCacheTest() {
        super();
    }


    @Test
    public void testSoftReferenceHandling () {

        final int maxSize = 10;
        final int iters = 50;

        final StandardCache<String, String> cache =
                new StandardCache<String, String>("testSoftReferences", true, 2, maxSize, NOPLogger.NOP_LOGGER);

        int keyCount = 0;
        int cacheSize = 0;

        for (int i = 0; i < iters; i++) {

            String key = "key" + keyCount;
            cache.get(key);
            cache.put(key, "value" + keyCount);

            cacheSize = cache.size();
            keyCount = (keyCount + 1) % maxSize;

            if (i == maxSize + 1) {
                //force soft references to be cleared by gc
                try {
                    Object[] ignored = new Object[(int)Runtime.getRuntime().maxMemory()];
                } catch (Throwable ignored2) {
                    // Ignore OME
                }
            }

        }

        Assertions.assertEquals(maxSize, cacheSize);

    }

    
}
