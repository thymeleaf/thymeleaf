package org.thymeleaf.util;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ListUtilsTest {



    @Test
    public void testSortListOfT() {
        //Arrange
        final List<String> unorderedList = createCABList();

        //Act
        final List<String> orderedList = ListUtils.sort(unorderedList);

        //Assert
        Assertions.assertEquals("a", orderedList.get(0));
        Assertions.assertEquals("b", orderedList.get(1));
        Assertions.assertEquals("c", orderedList.get(2));
        assertUnorderedListIsUnchanged(unorderedList, orderedList);
    }



    @Test
    public void testSortListOfTComparatorOfQsuperT() {
        //Arrange
        final Comparator<String> c = new Comparator<String>() {

            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        };
        final List<String> unorderedList = createCABList();

        //Act
        final List<String> orderedList = ListUtils.sort(unorderedList, c);

        //Assert
        Assertions.assertEquals("c", orderedList.get(0));
        Assertions.assertEquals("b", orderedList.get(1));
        Assertions.assertEquals("a", orderedList.get(2));
        assertUnorderedListIsUnchanged(unorderedList, orderedList);
    }

    private List<String> createCABList() {
        final List<String> unorderedList = new LinkedList<String>();
        unorderedList.add("c");
        unorderedList.add("a");
        unorderedList.add("b");
        return unorderedList;
    }

    private void assertUnorderedListIsUnchanged(
            final List<String> unorderedList, final List<String> orderedList) {
        Assertions.assertEquals(unorderedList.getClass(), orderedList.getClass());
        Assertions.assertEquals("c", unorderedList.get(0));
        Assertions.assertEquals("a", unorderedList.get(1));
        Assertions.assertEquals("b", unorderedList.get(2));
        Assertions.assertFalse(orderedList == unorderedList);
    }



}
