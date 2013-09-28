package org.thymeleaf.expression;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.util.ListUtils;

public class ListsTest {

    @Test
    public void testSortListOfT() {
        //Arrange
        final List<String> unorderedList = createCABList();

        //Act
        final List<String> orderedList = ListUtils.sort(unorderedList);

        //Assert
        Assert.assertEquals("a", orderedList.get(0));
        Assert.assertEquals("b", orderedList.get(1));
        Assert.assertEquals("c", orderedList.get(2));
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
        Assert.assertEquals("c", orderedList.get(0));
        Assert.assertEquals("b", orderedList.get(1));
        Assert.assertEquals("a", orderedList.get(2));
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
        Assert.assertEquals(unorderedList.getClass(), orderedList.getClass());
        Assert.assertEquals("c", unorderedList.get(0));
        Assert.assertEquals("a", unorderedList.get(1));
        Assert.assertEquals("b", unorderedList.get(2));
        Assert.assertFalse(orderedList == unorderedList);
    }



}
