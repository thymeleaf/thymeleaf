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
package org.thymeleaf.dom2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MarkupTextRepository implements IMarkupTextRepository {

    /*
     * This class's LRU will be based on storage order, not the real amount of times a text is actually used.
     * This is so in order to avoid that EVERY read operation needs a write lock in order to increment the LRU counter
     * for the text being returned.
     */

    // We will initially make room for 1000 different texts, and we will grow in increments of 500
    private static final int CONTENTS_INITIAL_SIZE = 1000;
    private static final int CONTENTS_SIZE_INC = 500;

    // We will use this constant to avoid creating too many 0-sized char arrays
    private static final int[] NO_IDS = new int[0];

    private final int maxSizeInChars;
    private int currentSizeInChars;

    private int textsLen;
    private String[] texts;
    private int textsSize;
    private int textsUnremovableSetSize;

    private int textMapLen;
    private int[] textMapKeys;
    private int[][] textMapValues;
    private int textMapSize;


    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Lock readLock = this.lock.readLock();
    private final Lock writeLock = this.lock.writeLock();



    public MarkupTextRepository(final int maxSizeInChars, final String[] unremovableTexts) {

        super();

        this.maxSizeInChars = maxSizeInChars;
        this.currentSizeInChars = 0;

        this.textsLen = CONTENTS_INITIAL_SIZE;
        this.texts = new String[this.textsLen];
        this.textsSize = 0;

        this.textsUnremovableSetSize = 0;

        this.textMapLen = CONTENTS_INITIAL_SIZE;
        this.textMapKeys = new int[this.textMapLen];
        this.textMapValues = new int[this.textMapLen][];
        this.textMapSize = 0;

    }






    public String getText(final String text) {

        if (text == null) {
            return null;
        }

        final int hashCode = text.hashCode();

        this.readLock.lock();

        try {

            final int[] ids = findTextIdsFromHashCode(hashCode);

            if (ids.length == 1) {

                // Found something for this hash code
                final String candidate = this.texts[ids[0]];
                if (checkResult(text, candidate)) {
                    return candidate;
                }

            } else if (ids.length > 1) {

                // The selected ID currently stores a collision. We will have to manually check for the right text instance
                for (int i = 0; i < ids.length; i++) {
                    final String candidate = this.texts[ids[i]];
                    if (checkResult(text, candidate)) {
                        // We will return the stored instance, maybe allowing 'text' to be eaten by the GC
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
            return storeText(text);
        } finally {
            this.writeLock.unlock();
        }

    }



    public String getText(final char[] text, final int offset, final int len) {

        if (text == null) {
            return null;
        }

        final int hashCode = computeHashCode(text, offset, len);

        this.readLock.lock();

        try {

            final int[] ids = findTextIdsFromHashCode(hashCode);

            if (ids.length == 1) {

                // Found something for this hash code
                final String candidate = this.texts[ids[0]];
                if (checkResult(text,offset,len,candidate)) {
                    return candidate;
                }

            } else if (ids.length > 1) {

                // The selected ID currently stores a collision. We will have to manually check for the right text instance
                for (int i = 0; i < ids.length; i++) {
                    final String candidate = this.texts[ids[i]];
                    if (checkResult(text, offset, len, candidate)) {
                        // We will return the stored instance, maybe allowing 'text' to be eaten by the GC
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




    private int[] findTextIdsFromHashCode(final int hashCode) {

        if (textMapSize == 0) {
            return NO_IDS;
        }

        /*
         * Find the map index for this hashCode, if it exists. We assume the map is ordered by key
         */
        int i = 0;
        while (i < this.textMapSize && this.textMapKeys[i] < hashCode) { i++; }
        if (i >= this.textMapSize) {
            // Not found, and all keys are < than this hashCode
            return NO_IDS;
        }
        if (this.textMapKeys[i] == hashCode) {
            // Found!
            return this.textMapValues[i];
        }
        // Not found, we stopped because we found a key > than our hashCode
        return NO_IDS;
    }




    private static boolean checkResult(final String input, final String result) {
        return input.equals(result);
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
        final int[] storedIds = findTextIdsFromHashCode(hashCode);

        if (storedIds.length == 1) {

            // Found something for this hash code
            final String candidate = this.texts[storedIds[0]];
            if (checkResult(text, candidate)) {
                return candidate;
            }

        } else if (storedIds.length > 1) {

            // The selected ID currently stores a collision. We will have to manually check for the right text instance
            for (int i = 0; i < storedIds.length; i++) {
                final String candidate = this.texts[storedIds[i]];
                if (checkResult(text, candidate)) {
                    // We will return the stored instance, maybe allowing 'text' to be eaten by the GC
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
         * Look for the correct position in the text map
         */
        int i = 0;
        while (i < this.textMapSize && this.textMapKeys[i] < hashCode) { i++; }

        if (i < this.textMapSize && this.textMapKeys[i] == hashCode) {
            // This is a collision! We need to grow the values array, but nothing else.

            final int[] ids = this.textMapValues[i];
            final int[] newIds = new int[ids.length + 1];
            System.arraycopy(ids,0,newIds,0,ids.length);
            newIds[ids.length] = textIndex;
            this.textMapValues[i] = newIds;

            this.currentSizeInChars += textLen;

            return text;

        }

        /*
         * No collisions, just add to the text map and return
         */

        if (i < this.textMapSize) {
            // We will add the new value somewhere in the middle of the map, so we need to make room for it
            System.arraycopy(this.textMapKeys, i, this.textMapKeys, i + 1, this.textMapSize - i);
            System.arraycopy(this.textMapValues, i, this.textMapValues, i + 1, this.textMapSize - i);
        }

        this.textMapKeys[i] = hashCode;
        this.textMapValues[i] = new int[] { textIndex };
        this.textMapSize++;

        this.currentSizeInChars += textLen;

        return text;

    }






    private void growContents() {

        // Even if, due to collisions, it might happen that the texts array reaches its maximum size
        // a bit before the texts map does, collisions should be so few this shouldn't really matter, so we
        // can confidently grow both structures at the same pace.

        /*
         * Grow texts array
         */
        final int newTextsLen = this.textsLen + CONTENTS_SIZE_INC;

        final String[] newTexts = new String[newTextsLen];
        System.arraycopy(this.texts,0,newTexts,0,this.textsSize);
        this.texts = newTexts;

        this.textsLen = newTextsLen;

        /*
         * Grow texts map
         */
        final int newTextMapLen = this.textMapLen + CONTENTS_SIZE_INC;

        final int[] newTextMapKeys = new int[newTextMapLen];
        System.arraycopy(this.textMapKeys,0,newTextMapKeys,0,this.textMapSize);
        this.textMapKeys = newTextMapKeys;

        final int[][] newTextMapValues = new int[newTextMapLen][];
        System.arraycopy(this.textMapValues,0,newTextMapValues,0,this.textMapSize);
        this.textMapValues = newTextMapValues;

        this.textMapLen = newTextMapLen;

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

        int i = 0;
        while (i < this.textMapSize && this.textMapKeys[i] < removedTextHashCode) { i++; }

        // Given the text exists in the repository, we must have found the exact hash code
        final int[] ids = this.textMapValues[i];

        if (ids.length == 1) {

            // Only one value, so we simply remove the complete entry

            if (this.textMapSize > i + 1) {
                System.arraycopy(this.textMapKeys, i + 1, this.textMapKeys, i, this.textMapSize - (i + 1));
                System.arraycopy(this.textMapValues, i + 1, this.textMapValues, i, this.textMapSize - (i + 1));
            }
            this.textMapSize--;

        } else {

            // This hash code had a collision, so removing it means modifying the ids array

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

            this.textMapValues[i] = newIds;

        }


        /*
         * Now, given we have removed a value from the 'texts' array, we need to decrease all index values
         * greater than the removed one which are stored in the text map
         */

        int kv; int[] kids;
        for (int k = 0; k < this.textMapSize; k++) {
            kids = this.textMapValues[k];
            for (kv = 0; kv < kids.length; kv++) {
                if (kids[kv] > removedTextIndex) {
                    kids[kv]--;
                }
            }
        }


        /*
         * Last step: simply decrease the amount of currently stored chars and return
         */

        this.currentSizeInChars -= removedText.length();

        return true;

    }




    private static int computeHashCode(final char[] text, final int offset, final int len) {
        // This basically mimics what the String.hashCode() method does, without the need to
        // convert the char[] into a new String object
        // If the text to compute was already a String, it would be better to directly call
        // its 'hashCode()' method, because Strings cache their hash codes.
        int h = 0;
        int off = offset;
        for (int i = 0; i < len; i++) {
            h = 31*h + text[off++];
        }
        return h;
    }




}
