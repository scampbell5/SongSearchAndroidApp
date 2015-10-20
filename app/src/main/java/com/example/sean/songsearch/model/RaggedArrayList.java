package com.example.sean.songsearch.model;

import java.util.*;

/*
 * RaggedArrayList.java
 * Sean Campbell
 *
 * Initial starting code by Prof. Boothe Sep 2015
 *
 * The RaggedArrayList is a 2 level data structure that is an array of arrays.
 *
 * It keeps the items in sorted order according to the comparator.
 * Duplicates are allowed.
 * New items are added after any equivalent items.
 *
 * NOTE: normally fields, internal nested classes and non API methods should all be private,
 *       however they have been made public so that the tester code can set them
 */
public class RaggedArrayList<E> implements Iterable<E> {
    private static final int MINIMUM_SIZE = 4;    // must be even so when split get two equal pieces
    private int size;
    private Object[] l1Array;     // really is an array of L2Array, but compiler won't let me cast to that
    private int l1NumUsed;
    private final Comparator<E> comp;

    // create an empty list
    // always have at least 1 second level array even if empty, makes code easier
    // (DONE)
    public RaggedArrayList(Comparator<E> c) {
        size = 0;
        l1Array = new Object[MINIMUM_SIZE];                // you can't create an array of a generic type
        l1Array[0] = new L2Array(MINIMUM_SIZE);  // first 2nd level array
        l1NumUsed = 1;
        comp = c;
    }

    // nested class for 2nd level arrays
    // (DONE)
    public class L2Array {
        public E[] items;
        public int numUsed;

        @SuppressWarnings({"unchecked"})
        public L2Array(int capacity) {
            items = (E[]) new Object[capacity];  // you can't create an array of a generic type
            numUsed = 0;
        }
    }

    //total size (number of entries) in the entire data structure
    // (DONE)
    public int size() {
        return size;
    }

    // null out all references so garbage collector can grab them
    // but keep otherwise empty l1Array and 1st L2Array
    // (DONE)
    @SuppressWarnings({"unchecked"})
    public void clear() {
        size = 0;
        Arrays.fill(l1Array, 1, l1Array.length, null);  // clear all but first l2 array
        l1NumUsed = 1;
        L2Array l2Array = (L2Array) l1Array[0];
        Arrays.fill(l2Array.items, 0, l2Array.numUsed, null);  // clear out l2array
        l2Array.numUsed = 0;
    }

    // nested class for a list position
    // used only internally
    // 2 parts: level 1 index and level 2 index
    public class ListLoc {
        public int level1Index;
        public int level2Index;

        public ListLoc(int level1Index, int level2Index) {
            this.level1Index = level1Index;
            this.level2Index = level2Index;
        }

        // test if two ListLoc's are to the same location (done)
        @SuppressWarnings({"unchecked"})
        public boolean equals(Object otherObj) {
            if (getClass() != otherObj.getClass())  // not really needed since it will be ListLoc
                return false;
            ListLoc other = (ListLoc) otherObj;

            return level1Index == other.level1Index && level2Index == other.level2Index;
        }

        // move ListLoc to next entry
        // when it moves past the very last entry it will be 1 index past the last value in the used level 2 array
        // can be used internally to scan through the array for sublist
        // also can be used to implement the iterator
        @SuppressWarnings({"unchecked"})
        public void moveToNext() {
            //While loc.Level2Index < numOfUsed -1; Keep incrementing Level2Index.
            if (this.level2Index < ((L2Array) l1Array[this.level1Index]).numUsed - 1) {
                this.level2Index++;
                //Resets Level2Index to 0, increments L1Array to next position.
            } else {
                this.level2Index = 0;
                this.level1Index++;
            }
        }
    }

    /*
    //Old code before implementing Binary Search
    // find 1st matching entry
    // returns ListLoc of 1st matching item
    // or of 1st item greater than the item if no match
    // this might be an unused slot at the end of a level 2 array
    public ListLoc findFront1(E item) {
        L2Array currentArray;
        ListLoc tempLocation = new ListLoc(0, 0);
        for (int i = 0; i < l1NumUsed; i++) {
            currentArray = (L2Array) l1Array[i];
            //Before we enter into the next array, check to see if it's empty first.
            //If it's empty, location will be at i, 0;
            if (currentArray.numUsed == 0) {
                return new ListLoc(i, 0);
            }

            int compare = comp.compare(item, currentArray.items[currentArray.numUsed - 1]);
            if (compare <= 0 || i == l1NumUsed - 1) {
                for (int j = 0; j < currentArray.numUsed; j++) {
                    int compare2 = comp.compare(item, currentArray.items[j]);
                    if (compare2 <= 0) {
                        return new ListLoc(i, j);
                    }
                    tempLocation.level1Index = i;
                    tempLocation.level2Index = j;
                }
            }

        }
        //If we scan all arrays and still did not find position, increment level2Index by 1 indicating next position in that array.
        tempLocation.level2Index++;
        return tempLocation;            // when finished should return: new ListLoc(l1,l2);
    }

*/

    //Find the position of the very first item.
    //Returns the position where item should be if item is not found.
    //Uses binary search to search through the outer (L1Arrays) to find the inner L2Array where item should be
    //Uses binary search on the L2Array, after locating location, returns a ListLocation for the item.
    @SuppressWarnings({"unchecked"})
    private ListLoc findFront(E item) {
        ListLoc insertLocation = new ListLoc(0, 0);
        int l1Low = 0;
        int l1Mid = 0;
        int l1High = l1NumUsed - 1;
        int l2Low = 0;
        int l2Mid = 0;
        int l2High;
        L2Array currentArray = (L2Array) l1Array[l1Mid];

        //Edge case to check to see if L1 is empty. Returns the first spot.
        if (currentArray.numUsed == 0) {
            return insertLocation;
        }

        //First search on outer array to find the inner array.
        int compare = 0;
        while (l1Low <= l1High) {
            l1Mid = (l1High + l1Low) / 2;
            currentArray = (L2Array) l1Array[l1Mid];
            compare = comp.compare(currentArray.items[currentArray.numUsed - 1], item);
            if (compare < 0) {
                l1Low = l1Mid + 1;
            } else {
                l1High = l1Mid - 1;
            }
        }

        if (compare < 0 && l1Mid + 1 != l1NumUsed) {
            l1Mid++;
        }

        //Update the location to the new level 1 index just found.
        insertLocation.level1Index = l1Mid;
        currentArray = (L2Array) l1Array[l1Mid];
        l2High = currentArray.numUsed - 1;


        //Start searching on the inner array to find insertion point.
        while (l2Low <= l2High) {
            l2Mid = (l2Low + l2High) / 2;
            compare = comp.compare(currentArray.items[l2Mid], item);
            if (compare == 0) {
                break;
            }
            if (compare < 0) {
                l2Low = l2Mid + 1;
            } else {
                l2High = l2Mid - 1;
            }
        }
        //If compare < 0, that means item was not found and should be inserted in the next possible slow.
        if (compare < 0) {
            l2Mid++;
        }

        for (int i = l2Mid; i > 0; i--) {
            compare = comp.compare(item, currentArray.items[i - 1]);
            if (compare != 0) {
                break;
            }
            l2Mid--;
        }
        insertLocation.level2Index = l2Mid;
        return insertLocation;
    }


    //Old search using LinearSearch before implementing Binary Search.
    /*
    // find location after the last matching entry
    // or if no match, it finds the index of the next larger item
    // this is the position to add a new entry
    // this might be an unused slot at the end of a level 2 array
    public ListLoc findEnd1(E item) {

        //currentArray will be the currentArray we're scanning.
        L2Array currentArray;

        //Iterate over the outside array.
        for (int i = l1NumUsed - 1; i >= 0; i--) {
            //Assign currentArray to the internal array we're checking if the item is present.
            currentArray = (L2Array) l1Array[i];
            //Before we enter into the next array, check to see if it's empty first.
            //If it's empty, location will be at i, 0;
            if (currentArray.numUsed == 0) {
                return new ListLoc(i, 0);
            }

            int compare = comp.compare(item, currentArray.items[0]);
            if (compare >= 0 || i == 0) {
                //Iterate over the currentArray
                for (int j = currentArray.numUsed - 1; j >= 0; j--) {
                    //If compare is < 0, we continue. If it's >=0 we know we found the position. Return position i = l1Array, j = L2Array.
                    int compare2 = comp.compare(item, currentArray.items[j]);
                    if (compare2 >= 0) {
                        j++;
                        return new ListLoc(i, j);
                    }
                }
            }
        }
        // If we don't find the item in any array, we return Loc(0,0)
        return new ListLoc(0, 0);            // when finished should return: new ListLoc(l1,l2);
    }

*/

    //Find the position of the very last item.
    //Returns the position where item should be if item is not found.
    //Uses binary search to search through the outer (L1Arrays) to find the inner L2Array where item should be
    //Uses binary search on the L2Array, after locating location, returns a ListLocation for the item.
    @SuppressWarnings({"unchecked"})
    private ListLoc findEnd(E item) {
        int l1Low = 0;
        int l1High = l1NumUsed - 1;
        int l1Mid = 0;
        int compare;
        int l2Mid;
        int l2Low = 0;
        int l2High;
        ListLoc insertLocation = new ListLoc(0, 0);

        L2Array currentArray = (L2Array) l1Array[l1Mid];

        //Checks to see if array is empty.
        if (currentArray.numUsed == 0) {
            return insertLocation;
        }

        l1Mid = (l1High + l1Low) / 2;
        while (l1Low <= l1High) {
            currentArray = (L2Array) l1Array[l1Mid];
            compare = comp.compare(currentArray.items[0], item);
            if (compare <= 0) {
                l1Low = l1Mid + 1;
            } else {
                l1High = l1Mid - 1;
            }
            l1Mid = (l1High + l1Low) / 2;
        }

        insertLocation.level1Index = l1Mid;
        currentArray = (L2Array) l1Array[l1Mid];

        l2High = currentArray.numUsed - 1;

        l2Mid = (l2Low + l2High) / 2;
        while (l2Low <= l2High) {
            compare = comp.compare(currentArray.items[l2Mid], item);
            if (compare == 0) {
                break;
            }
            if (compare < 0) {
                l2Low = l2Mid + 1;
            } else {
                l2High = l2Mid - 1;
            }
            l2Mid = (l2Low + l2High) / 2;
        }
        compare = comp.compare(currentArray.items[l2Mid], item);
        if (compare <= 0) {
            for (int i = l2Mid + 1; i < currentArray.numUsed; i++) {
                compare = comp.compare(item, currentArray.items[i]);
                if (compare >= 0) {
                    l2Mid++;
                } else {
                    break;
                }
            }
            l2Mid++;
        }

        insertLocation.level2Index = l2Mid;
        return insertLocation;            // when finished should return: new ListLoc(l1,l2);
    }

    /**
     * add object after any other matching values
     * findEnd will give the insertion position
     */
    @SuppressWarnings({"unchecked"})
    public boolean add(E item) {

        //Location used to find insertion point of new item.
        ListLoc locToAdd = findEnd(item);

        //Pointer to the level2 array where item needs to be inserted.
        L2Array l2ArrayToAddItem = (L2Array) l1Array[locToAdd.level1Index];


        //Loops through level2 array, adding item and shifting items down.
        for (int i = locToAdd.level2Index; i < l2ArrayToAddItem.numUsed; i++) {
            E tmpItem = l2ArrayToAddItem.items[i];
            l2ArrayToAddItem.items[i] = item;
            item = tmpItem;
        }

        //Assigns last item into array, increments numOfUsed to account for new item added.
        l2ArrayToAddItem.items[l2ArrayToAddItem.numUsed] = item;
        l2ArrayToAddItem.numUsed++;

        //We must grow the array if level 2 array is full after add.
        //We grow by 2 if the array length is less than level1 array length.
        //We split if level 2 array length is >= level 1 array length.
        //These specs were outlined in the assignment.
        if (l2ArrayToAddItem.items.length == l2ArrayToAddItem.numUsed) {
            //If l2Array is smaller than l1Array, we double.
            if (l2ArrayToAddItem.numUsed < l1NumUsed) {
                //Grows array by 2.
                l2ArrayToAddItem.items = Arrays.copyOf(l2ArrayToAddItem.items, l2ArrayToAddItem.numUsed * 2);

                //Else if it become larger, we must split the array into two new arrays.
            } else {
                //Create a new level2 array for second half of first level2 array we added to.
                L2Array l2ArrayToAddItem2 = new L2Array(l2ArrayToAddItem.items.length);
                //Copy the second half of the level2 array to the new array.
                System.arraycopy(l2ArrayToAddItem.items, l2ArrayToAddItem.numUsed / 2, l2ArrayToAddItem2.items, 0, l2ArrayToAddItem.numUsed / 2);
                //Fills out the original L2Array with blanks, second half was copied to a new array.
                Arrays.fill(l2ArrayToAddItem.items, l2ArrayToAddItem.numUsed / 2, l2ArrayToAddItem.numUsed, null);

                //Assigns the two new arrays the correct numOfUsed.
                l2ArrayToAddItem.numUsed = l2ArrayToAddItem2.numUsed = l2ArrayToAddItem.numUsed / 2;

                //Assign the original l1Index with the L2Array we just split.
                l1Array[locToAdd.level1Index] = l2ArrayToAddItem;

                //Adds new L2Array to L1Array, reassigns each L2Array.
                for (int i = locToAdd.level1Index + 1; i < l1NumUsed; i++) {
                    L2Array tmpArray = (L2Array) l1Array[i];
                    l1Array[i] = l2ArrayToAddItem2;
                    l2ArrayToAddItem2 = tmpArray;
                }
                //Assigns last L2Array to L1Array.
                l1Array[l1NumUsed] = l2ArrayToAddItem2;

                //Increments l1NumOfUsed to account for L2Array Split.
                l1NumUsed++;

                //Checks to see if l1NumOfUsed = Length, if so grows L1Array by 2.
                if (l1NumUsed == l1Array.length) {
                    l1Array = Arrays.copyOf(l1Array, l1NumUsed * 2);
                }
            }
        }

        size++;
        return true;
    }

    /**
     * check if list contains a match
     */
    @SuppressWarnings({"unchecked"})
    public boolean contains(E item) {
        //Finds the index of the item. findFront returns where it is, or where it should be in list.
        //Have to perform one compare to verify item == item in L2Array
        ListLoc indexFound = findFront(item);
        return item.equals(((L2Array) l1Array[indexFound.level1Index]).items[indexFound.level2Index]);
    }

    /**
     * copy the contents of the RaggedArrayList into the given array
     *
     * @param a - an array of the actual type and of the correct size
     * @return the filled in array
     */
    @SuppressWarnings({"unchecked"})
    public E[] toArray(E[] a) {

        //If size==0, return empty array.
        if (size() == 0) {
            return a;
        } else {
            if (a.length == size()) {
                Iterator tmpIter = iterator();
                int i = 0;
                while (tmpIter.hasNext()) {
                    a[i] = (E) tmpIter.next();
                    i++;
                }
                return a;
            }
            return a;
        }
    }

    /**
     * returns a new independent RaggedArrayList
     * whose elements range from fromElemnt, inclusive, to toElement, exclusive
     * the original list is unaffected
     * findStart and findEnd will be useful
     *
     * @param fromElement
     * @param toElement
     * @return the sublist
     */
    @SuppressWarnings({"unchecked"})
    public RaggedArrayList<E> subList(E fromElement, E toElement) {
        RaggedArrayList<E> result = new RaggedArrayList<>(comp);
        //Find beginning item location, endItem location.
        ListLoc locFront = findFront(fromElement);
        ListLoc locEnd = findFront(toElement);

        //While two locations do not equal eachother, iterate through adding each item to new array.
        while (!locFront.equals(locEnd) && locFront.level1Index < l1NumUsed) {
            result.add(((L2Array) l1Array[locFront.level1Index]).items[locFront.level2Index]);
            locFront.moveToNext();
        }

        return result;
    }

    /**
     * returns an iterator for this list
     * this method just creates an instance of the inner Itr() class
     * (DONE)
     */
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * Iterator is just a list loc
     * it starts at (0,0) and finishes with index2 1 past the last item in the last block
     */
    private class Itr implements Iterator<E> {
        private final ListLoc loc;

        /*
         * create iterator at start of list
         * (DONE)
         */
        Itr() {
            loc = new ListLoc(0, 0);
        }

        /**
         * check if more items
         */
        public boolean hasNext() {
            //If level1Index > l1NumOfUsed, out of bounds and will return false.
            return loc.level1Index < l1NumUsed;
        }

        /**
         * return item and move to next
         * throws NoSuchElementException if off end of list
         */
        public E next() {
            //Throw exception if out of bounds.
            if (!hasNext()) {
                throw new IndexOutOfBoundsException();
            } else {
                E itemToReturn;
                itemToReturn = ((L2Array) l1Array[loc.level1Index]).items[loc.level2Index];
                loc.moveToNext();
                return itemToReturn;
            }
        }

        /**
         * Remove is not implemented. Just use this code.
         * (DONE)
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}