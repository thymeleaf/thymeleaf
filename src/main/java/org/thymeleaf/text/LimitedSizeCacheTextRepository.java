/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.text;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.thymeleaf.util.TextUtils;

/**
 * <p>
 *     Repository of text (<tt>String</tt>) instances created by the parser or document model.
 * </p>
 * <p>
 *     This repository works in a similar way to {@link String#intern()}, except that it allows the
 *     <em>interning</em> of texts without the need to previously have a <tt>String</tt> instance,
 *     and that it allows setting a maximum size for the entire repository in chars (and therefore
 *     in bytes too).
 * </p>
 * <p>
 *     When full, objects of this class will be evicted following a first-in, first-out policy. Oldest texts
 *     added to the repository will be removed to make room for new ones no matter how many times they have been
 *     retrieved.
 * </p>
 * <p>
 *     Also, this implementation allows the specification (through a constructor argument) of a series of
 *     texts that should never be removed from the repository.
 * </p>
 * <p>
 *     Instances of this class are <strong>thread-safe</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class LimitedSizeCacheTextRepository implements ITextRepository {

    /*
     * This class's LRU will be based on storage order, not the real amount of times a text is actually used.
     * This is so in order to avoid that EVERY read operation needs a write lock in order to increment the LRU counter
     * for the text being returned.
     */

    // We will initially make room for 1000 different texts, and we will grow in increments of 500
    private static final int CONTENTS_INITIAL_LEN = 1500;
    private static final int CONTENTS_LEN_INC = 500;

    // HashCode-based indexes for the text map will be distributed using a 'modulo' function, so we need a prime number
    private static final int TEXT_MAP_LEN = 3181;

    private final int maxSizeInChars;
    private int currentSizeInChars;

    private int textsLen;
    private String[] texts;
    private int textsSize;
    private final int textsUnremovableSetSize;

    private final int[][] textMap;


    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Lock readLock = this.lock.readLock();
    private final Lock writeLock = this.lock.writeLock();



    public LimitedSizeCacheTextRepository(final int maxSizeInChars, final String[] unremovableTexts) {

        super();

        this.maxSizeInChars = maxSizeInChars;
        this.currentSizeInChars = 0;

        this.textsLen = CONTENTS_INITIAL_LEN;
        this.texts = new String[this.textsLen];
        this.textsSize = 0;

        this.textMap = new int[TEXT_MAP_LEN][];
        for (int i = 0; i < TEXT_MAP_LEN; i++) {
            this.textMap[i] = null;
        }

        for (final String unremovableText : unremovableTexts) {
            storeText(unremovableText);
        }
        this.textsUnremovableSetSize = unremovableTexts.length;

    }






    public String getText(final char[] text, final int offset, final int len) {

        if (text == null) {
            return null;
        }

        final int hashCode = TextUtils.hashCode(text, offset, len);

        this.readLock.lock();

        try {

            final int[] ids = this.textMap[Math.abs(hashCode) % TEXT_MAP_LEN];

            if (ids != null) {

                // Now we need to iterate the array of ids looking for the target text
                for (int i = 0; i < ids.length; i++) {
                    final String candidate = this.texts[ids[i]];
                    if (checkResult(text, offset, len, candidate)) {
                        return candidate;
                    }
                }

            }

        } finally {
            this.readLock.unlock();
        }


        /*
         * NOT FOUND. We need to obtain a write lock and store the text
         */
        this.writeLock.lock();
        try {
            return storeText(text,offset,len);
        } finally {
            this.writeLock.unlock();
        }

    }


    public String getText(final CharSequence text) {

        if (text == null) {
            return null;
        }

        final int hashCode = TextUtils.hashCode(text);

        this.readLock.lock();

        try {

            final int[] ids = this.textMap[Math.abs(hashCode) % TEXT_MAP_LEN];

            if (ids != null) {

                // Now we need to iterate the array of ids looking for the target text
                for (int i = 0; i < ids.length; i++) {
                    final String candidate = this.texts[ids[i]];
                    if (checkResult(text, candidate)) {
                        // We will return the stored instance, maybe allowing the 'text' arg to be eaten by the GC
                        return candidate;
                    }
                }

            }

        } finally {
            this.readLock.unlock();
        }

        /*
         * NOT FOUND. We need to obtain a write lock and store the text
         */
        this.writeLock.lock();
        try {
            return storeText(text.toString());
        } finally {
            this.writeLock.unlock();
        }

    }


    public String getText(final CharSequence text, final int beginIndex, final int endIndex) {

        if (text == null) {
            return null;
        }

        final int hashCode = TextUtils.hashCode(text, beginIndex, endIndex);

        this.readLock.lock();

        try {

            final int[] ids = this.textMap[Math.abs(hashCode) % TEXT_MAP_LEN];

            if (ids != null) {

                // Now we need to iterate the array of ids looking for the target text
                for (int i = 0; i < ids.length; i++) {
                    final String candidate = this.texts[ids[i]];
                    if (checkResult(text, beginIndex, endIndex, candidate)) {
                        // We will return the stored instance, maybe allowing the 'text' arg to be eaten by the GC
                        return candidate;
                    }
                }

            }

        } finally {
            this.readLock.unlock();
        }

        /*
         * NOT FOUND. We need to obtain a write lock and store the text
         */
        this.writeLock.lock();
        try {
            return storeText(text.subSequence(beginIndex, endIndex).toString());
        } finally {
            this.writeLock.unlock();
        }

    }


    public String getText(final CharSequence text0, final CharSequence text1) {

        if (text0 == null) {
            return getText(text1);
        }
        if (text1 == null) {
            return getText(text0);
        }

        final int hashCode = TextUtils.hashCode(text0, text1);

        this.readLock.lock();

        try {

            final int[] ids = this.textMap[Math.abs(hashCode) % TEXT_MAP_LEN];

            if (ids != null) {

                // Now we need to iterate the array of ids looking for the target text
                for (int i = 0; i < ids.length; i++) {
                    final String candidate = this.texts[ids[i]];
                    if (checkResult(text0, text1, candidate)) {
                        // We will return the stored instance, maybe allowing the 'text' arg to be eaten by the GC
                        return candidate;
                    }
                }

            }

        } finally {
            this.readLock.unlock();
        }

        /*
         * NOT FOUND. We need to obtain a write lock and store the text
         */
        this.writeLock.lock();
        try {
            return storeText(text0.toString() + text1.toString());
        } finally {
            this.writeLock.unlock();
        }

    }


    public String getText(final CharSequence text0, final CharSequence text1, final CharSequence text2) {

        if (text0 == null) {
            return getText(text1, text2);
        }
        if (text1 == null) {
            return getText(text0, text2);
        }
        if (text2 == null) {
            return getText(text0, text1);
        }

        final int hashCode = TextUtils.hashCode(text0, text1, text2);

        this.readLock.lock();

        try {

            final int[] ids = this.textMap[Math.abs(hashCode) % TEXT_MAP_LEN];

            if (ids != null) {

                // Now we need to iterate the array of ids looking for the target text
                for (int i = 0; i < ids.length; i++) {
                    final String candidate = this.texts[ids[i]];
                    if (checkResult(text0, text1, text2, candidate)) {
                        // We will return the stored instance, maybe allowing the 'text' arg to be eaten by the GC
                        return candidate;
                    }
                }

            }

        } finally {
            this.readLock.unlock();
        }

        /*
         * NOT FOUND. We need to obtain a write lock and store the text
         */
        this.writeLock.lock();
        try {
            return storeText(text0.toString() + text1.toString() + text2.toString());
        } finally {
            this.writeLock.unlock();
        }

    }


    public String getText(final CharSequence text0, final CharSequence text1, final CharSequence text2, final CharSequence text3) {

        if (text0 == null) {
            return getText(text1, text2, text3);
        }
        if (text1 == null) {
            return getText(text0, text2, text3);
        }
        if (text2 == null) {
            return getText(text0, text1, text3);
        }
        if (text3 == null) {
            return getText(text0, text1, text2);
        }

        final int hashCode = TextUtils.hashCode(text0, text1, text2, text3);

        this.readLock.lock();

        try {

            final int[] ids = this.textMap[Math.abs(hashCode) % TEXT_MAP_LEN];

            if (ids != null) {

                // Now we need to iterate the array of ids looking for the target text
                for (int i = 0; i < ids.length; i++) {
                    final String candidate = this.texts[ids[i]];
                    if (checkResult(text0, text1, text2, text3, candidate)) {
                        // We will return the stored instance, maybe allowing the 'text' arg to be eaten by the GC
                        return candidate;
                    }
                }

            }

        } finally {
            this.readLock.unlock();
        }

        /*
         * NOT FOUND. We need to obtain a write lock and store the text
         */
        this.writeLock.lock();
        try {
            return storeText(text0.toString() + text1.toString() + text2.toString() + text3.toString());
        } finally {
            this.writeLock.unlock();
        }

    }


    public String getText(final CharSequence text0, final CharSequence text1, final CharSequence text2, final CharSequence text3, final CharSequence text4) {

        if (text0 == null) {
            return getText(text1, text2, text3, text4);
        }
        if (text1 == null) {
            return getText(text0, text2, text3, text4);
        }
        if (text2 == null) {
            return getText(text0, text1, text3, text4);
        }
        if (text3 == null) {
            return getText(text0, text1, text2, text4);
        }
        if (text4 == null) {
            return getText(text0, text1, text2, text3);
        }

        final int hashCode = TextUtils.hashCode(text0, text1, text2, text3, text4);

        this.readLock.lock();

        try {

            final int[] ids = this.textMap[Math.abs(hashCode) % TEXT_MAP_LEN];

            if (ids != null) {

                // Now we need to iterate the array of ids looking for the target text
                for (int i = 0; i < ids.length; i++) {
                    final String candidate = this.texts[ids[i]];
                    if (checkResult(text0, text1, text2, text3, text4, candidate)) {
                        // We will return the stored instance, maybe allowing the 'text' arg to be eaten by the GC
                        return candidate;
                    }
                }

            }

        } finally {
            this.readLock.unlock();
        }

        /*
         * NOT FOUND. We need to obtain a write lock and store the text
         */
        this.writeLock.lock();
        try {
            return storeText(text0.toString() + text1.toString() + text2.toString() + text3.toString() + text4.toString());
        } finally {
            this.writeLock.unlock();
        }

    }






    private static boolean checkResult(final char[] input, final int offset, final int len, final String result) {
        if (result.length() != len) {
            return false;
        }
        int j = 0;
        for (; j < len; j++) {
            if (result.charAt(j) != input[offset+j]) {
                break;
            }
        }
        return(j >= len);
    }


    private static boolean checkResult(final CharSequence input, final String result) {

        if (input == result) {
            return true;
        }
        if (input instanceof String) {
            return input.equals(result);
        }
        int n = input.length();
        if (n == result.length()) {
            int i = 0;
            while (n-- != 0) {
                if (input.charAt(i) != result.charAt(i))
                    return false;
                i++;
            }
            return true;
        }
        return false;
    }


    private static boolean checkResult(final CharSequence input, final int beginIndex, final int endIndex, final String result) {

        if (beginIndex == 0 && endIndex == result.length()) {
            if (input == result) {
                return true;
            }
            if (input instanceof String) {
                return input.equals(result);
            }
        }
        int n = endIndex - beginIndex;
        if (n == result.length()) {
            int i = 0;
            while (n-- != 0) {
                if (input.charAt(beginIndex + i) != result.charAt(i))
                    return false;
                i++;
            }
            return true;
        }
        return false;
    }


    private static boolean checkResult(final CharSequence input0, final CharSequence input1, final String result) {

        if (input0 == null) {
            return checkResult(input1, result);
        }
        if (input1 == null) {
            return checkResult(input0, result);
        }

        return checkResultPart(checkResultPart(0, input0, result), input1, result) != -1;

    }


    private static boolean checkResult(final CharSequence input0, final CharSequence input1, final CharSequence input2, final String result) {

        if (input0 == null) {
            return checkResult(input1, input2, result);
        }
        if (input1 == null) {
            return checkResult(input0, input2, result);
        }
        if (input2 == null) {
            return checkResult(input0, input1, result);
        }

        return checkResultPart(checkResultPart(checkResultPart(0, input0, result), input1, result), input2, result) != -1;

    }


    private static boolean checkResult(final CharSequence input0, final CharSequence input1, final CharSequence input2, final CharSequence input3, final String result) {

        if (input0 == null) {
            return checkResult(input1, input2, input3, result);
        }
        if (input1 == null) {
            return checkResult(input0, input2, input3, result);
        }
        if (input2 == null) {
            return checkResult(input0, input1, input3, result);
        }
        if (input3 == null) {
            return checkResult(input0, input1, input2, result);
        }

        return checkResultPart(checkResultPart(checkResultPart(checkResultPart(0, input0, result), input1, result), input2, result), input3, result) != -1;

    }


    private static boolean checkResult(final CharSequence input0, final CharSequence input1, final CharSequence input2, final CharSequence input3, final CharSequence input4, final String result) {

        if (input0 == null) {
            return checkResult(input1, input2, input3, input4, result);
        }
        if (input1 == null) {
            return checkResult(input0, input2, input3, input4, result);
        }
        if (input2 == null) {
            return checkResult(input0, input1, input3, input4, result);
        }
        if (input3 == null) {
            return checkResult(input0, input1, input2, input4, result);
        }
        if (input4 == null) {
            return checkResult(input0, input1, input2, input3, result);
        }

        return checkResultPart(checkResultPart(checkResultPart(checkResultPart(checkResultPart(0, input0, result), input1, result), input2, result), input3, result), input4, result) != -1;

    }




    private static int checkResultPart(final int i, final CharSequence input, final String result) {

        if (i == -1) {
            return i;
        }

        final int inputLen = input.length();

        if (input instanceof String) {
            if (result.startsWith((String)input, i)) {
                return i + inputLen;
            }
            return -1; // doesn't match!
        }

        if (i + inputLen > result.length()) {
            return -1; // wouldn't fit :(
        }

        int x = i;
        int n = inputLen;
        while (n-- != 0) {
            if (input.charAt(x-i) != result.charAt(x)) {
                return -1;
            }
            x++;
        }

        return i + inputLen;

    }







    private String storeText(final char[] text, final int offset, final int len) {
        return storeText(new String(text,offset,len));
    }


    private String storeText(final String text) {

        /*
         * Compute the hash code of the new text
         */
        final int hashCode = text.hashCode();


        /*
         * Check if this text already exists - in such case, simply return the already-inserted one
         * (this might happen if two processes were waiting for the same write lock to store the same String)
         */
        final int mapIndex = Math.abs(hashCode) % TEXT_MAP_LEN;
        final int[] ids = this.textMap[mapIndex];

        if (ids != null) {

            for (int i = 0; i < ids.length; i++) {
                final String candidate = this.texts[ids[i]];
                if (checkResult(text, candidate)) {
                    return candidate;
                }
            }

        }


        /*
         * -------------------------
         * Not found -> Do store it!
         * -------------------------
         */


        /*
         * First, compute the size in chars of the text to be added
         */
        final int textLen = text.length();

        /*
         * We might need to make some room for it
         */
        while (this.currentSizeInChars + textLen > this.maxSizeInChars && removeOldest());
        if (this.currentSizeInChars + textLen > this.maxSizeInChars) {
            // We weren't able to make enough room. Text simply doesn't fit. Return it WITHOUT STORING
            return text;
        }


        if (this.textsSize == this.textsLen) { // texts array it will be at least as large as the text map
            growContents();
        }

        /*
         * This is the new index of the text
         */
        final int textIndex = this.textsSize;

        /*
         * Store the text itself into the 'texts' array
         */
        this.texts[textIndex] = text;
        this.textsSize++;

        /*
         * Add the text to the text map
         */
        final int newIdsLen = (ids == null? 1 : ids.length + 1);
        final int[] newIds = new int[newIdsLen];
        if (ids != null) {
            System.arraycopy(ids, 0, newIds, 0, ids.length);
        }
        newIds[newIdsLen - 1] = textIndex;
        this.textMap[mapIndex] = newIds;

        this.currentSizeInChars += textLen;

        return text;

    }






    private void growContents() {

        /*
         * Grow texts array
         */
        final int newTextsLen = this.textsLen + CONTENTS_LEN_INC;

        final String[] newTexts = new String[newTextsLen];
        System.arraycopy(this.texts,0,newTexts,0,this.textsSize);
        this.texts = newTexts;

        this.textsLen = newTextsLen;

    }




    public boolean removeOldest() {

        if (this.textsUnremovableSetSize == this.textsSize) {
            // We weren't able to remove anything
            return false;
        }

        /*
         * The oldest entry will always be in the 'texts' array at the index immediately after the unremovable set
         */

        final int removedTextIndex = this.textsUnremovableSetSize;
        final String removedText = this.texts[removedTextIndex];
        final int removedTextHashCode = removedText.hashCode();

        /*
         * Remove text from the 'texts' array
         */

        if (this.textsSize > removedTextIndex + 1) {
            System.arraycopy(this.texts, removedTextIndex + 1, this.texts, removedTextIndex, this.textsSize - (removedTextIndex + 1));
        }
        this.textsSize--;

        /*
         * Remove the text from the text map
         */

        final int i = Math.abs(removedTextHashCode) % TEXT_MAP_LEN;

        // Given the text exists in the repository, we must have found the exact hash code
        final int[] ids = this.textMap[i];

        if (ids.length == 1) {

            // Only one value, so we simply remove the complete array
            this.textMap[i] = null;

        } else {

            // We need to modify the array, first looking for the correct index

            int j = 0;
            while (j < ids.length && ids[j] != removedTextIndex) {
                j++;
            }

            final int[] newIds = new int[ids.length - 1];
            if (j > 0) {
                System.arraycopy(ids, 0, newIds, 0, j);
            }
            if (j + 1 < ids.length) {
                System.arraycopy(ids, j + 1, newIds, j, (ids.length - (j + 1)));
            }

            this.textMap[i] = newIds;

        }


        /*
         * Now, given we have removed a value from the 'texts' array, we need to decrease all index values
         * greater than the removed one which are stored in the text map
         */

        int kv; int[] kids;
        for (int k = 0; k < TEXT_MAP_LEN; k++) {
            kids = this.textMap[k];
            if (kids != null) {
                for (kv = 0; kv < kids.length; kv++) {
                    if (kids[kv] > removedTextIndex) {
                        kids[kv]--;
                    }
                }
            }
        }


        /*
         * Last step: simply decrease the amount of currently stored chars and return
         */

        this.currentSizeInChars -= removedText.length();

        return true;

    }



}
