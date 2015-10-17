package com.example.sean.songsearch.controller;

import com.example.sean.songsearch.model.CmpCnt;
import com.example.sean.songsearch.model.Song;

import java.util.*;

/*
 * SearchByArtistPrefix.java
 * starting code
 * Boothe 2015
 */
public class SearchByArtistPrefix {

    private Song[] songs;  // keep a direct reference to the song array
    private int numberOfCompares = 0;

    public SearchByArtistPrefix(SongCollection sc) {
        songs = sc.getAllSongs();
    }

    /**
     * find all songs matching artist prefix
     * uses binary search
     * should operate in time log n + k (# matches)
     */
    public Song[] search(String artistPrefix) {
        LinkedList<Song> tmpSongs = new LinkedList<>();
        Song[] foundSongs;
        Song tmpSong = new Song(artistPrefix, "", "");
        Comparator<Song> compareArtist = new Song.CmpArtist();
        int initialSearchIndex = Arrays.binarySearch(songs, tmpSong, compareArtist);
        numberOfCompares = ((CmpCnt) compareArtist).getCmpCnt();

        //Search index will be < 0 if no exact matches are found.
        //Iterates through all songs at starting position == initialSearchIndex, until reaches an artist where artist does not start with prefix.
        if (initialSearchIndex < 0) {
            searchBySongNotFound(initialSearchIndex, artistPrefix, tmpSongs);

            //Search index will == 0 if an artist matches exactly.
            //Iterate each end of index, searching until artist != searched for artistPrefix.
        } else {
            searchByFoundSong(initialSearchIndex, artistPrefix, tmpSongs);
        }

        foundSongs = new Song[tmpSongs.size()];
        tmpSongs.toArray(foundSongs);
        return foundSongs;
    }

    //If search = 0, that means there is an exact artist name and we must search as such.
    //Logic is to add the index of found song, search from found song to first song and last song.
    //Since songs are already sorted, we just need to search until search - i, and search + i does not equal artist.
    private void searchByFoundSong(int initialSearchIndex, String artistPrefix, LinkedList<Song> tmpSongs) {
        tmpSongs.add(songs[initialSearchIndex]);
        boolean rightFound = false;
        boolean leftFound = false;
        int i = 1;
        int size = songs.length;
        int artistPrefixLength = artistPrefix.length();
        //rightFound and leftFound will both be true when we've exhausted the search.
        while (!(rightFound && leftFound)) {

            //Verify that we haven't already exhausted all the songs to the right.
            //Must also check that we haven't reached the end of the array.
            int rightIndex = initialSearchIndex + i;
            if ((rightIndex < size) && !rightFound) {
                numberOfCompares++;
                if ((songs[rightIndex].getArtist().length() >= artistPrefixLength) && artistPrefix.compareToIgnoreCase(songs[rightIndex].getArtist().substring(0,artistPrefixLength)) == 0) {
                    tmpSongs.addLast(songs[rightIndex]);
                    if (rightIndex == size -1){
                        rightFound = true;
                    }
                } else {
                    rightFound = true;
                }
            }

            //Must also check the first half of songs[] array.
            //If search -i < 0, we reached all the way to front of array.
            int leftIndex = initialSearchIndex - i;
            if (( leftIndex >= 0) && !leftFound) {
                numberOfCompares++;
                if (songs[leftIndex].getArtist().length() >= artistPrefixLength && artistPrefix.compareToIgnoreCase(songs[leftIndex].getArtist().substring(0,artistPrefixLength)) == 0) {
                    tmpSongs.addFirst(songs[leftIndex]);
                    if (leftIndex == 0){
                        leftFound = true;
                    }
                } else {
                    leftFound = true;
                }
            }
            i++;
        }
    }

    //If search returns an index other than 0, we have a starting position and should search based on prefix.
    //We add each song from starting position until a song does not start with the prefix.
    private void searchBySongNotFound(int initialSearchIndex, String artistPrefix, LinkedList<Song> tmpSongs) {
        int index = (initialSearchIndex * -1) - 1;
        int artistPrefixLength = artistPrefix.length();
        for (int i = index; i < songs.length; i++) {
            //Checks to verify artist does start with prefix.
            //Casts both to lowercase to ensure they will match.
            numberOfCompares++;
            if (songs[i].getArtist().length() >= artistPrefixLength && artistPrefix.compareToIgnoreCase(songs[i].getArtist().substring(0,artistPrefixLength)) == 0 ) {
                tmpSongs.add(songs[i]);
            } else {
                break;
            }
        }
    }

    public int getNumberOfCompares() {
        return numberOfCompares;
    }


    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: prog songfile [search string]");
            return;
        }

        /*
        SongCollection sc = new SongCollection(args[0]);
        SearchByArtistPrefix sbap = new SearchByArtistPrefix(sc);

        if (args.length > 1) {
            System.out.println("searching for: " + args[1]);
            Song[] byArtistResult = sbap.search(args[1]);

            // to do: show first 10 matches

            System.out.println("Total number of songs: " + sc.getAllSongs().length);
            System.out.println("Number of compares: " + sbap.numberOfCompares);
            System.out.println("Total matches: " + byArtistResult.length);
            for (int i = 0; i < byArtistResult.length && i < 10; i++) {
                System.out.println(byArtistResult[i]);
            }
        }*/
    }
}