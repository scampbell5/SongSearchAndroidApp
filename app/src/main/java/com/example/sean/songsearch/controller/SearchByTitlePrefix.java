package com.example.sean.songsearch.controller;

import com.example.sean.songsearch.model.Song;

import java.util.Comparator;

/**
 * Created by Sean on 10/12/15.
 */
public class SearchByTitlePrefix {

    Comparator<Song> cmpTitle = new Song.CmpTitle();
    RaggedArrayList<Song> songs = new RaggedArrayList<>(cmpTitle);

    public SearchByTitlePrefix(SongCollection sc) {
        //Iterates through and populates the RaggedArrayList with all songs.
        for (int i = 0; i < sc.getAllSongs().length; i++) {
            songs.add(sc.getAllSongs()[i]);
        }
    }


    //Method used to search for a song based on the title prefix.
    public Song[] search(String titlePrefix) {
        RaggedArrayList<Song> tempSongs;
        Song toSong = new Song("", titlePrefix, "");
        char endChar = titlePrefix.charAt(titlePrefix.length() - 1);
        endChar++;
        //fromTitle is the exact same title prefix, except last character is incremented by 1.
        //This gives us the end position.
        String fromTitle = titlePrefix.substring(0, titlePrefix.length() - 1) + endChar;
        Song fromSong = new Song("", fromTitle, "");
        tempSongs = songs.subList(toSong, fromSong);

        //Checks to see if there are no matching searches.
        //Returns an empty array.
        if (tempSongs.size() == 0){
            return new Song[0];
        }

        Song[] foundSongs = new Song[tempSongs.size()];
        return tempSongs.toArray(foundSongs);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: prog songfile [search string]");
            return;
        }

        /*
        SongCollection sc = new SongCollection(args[0]);
        SearchByTitlePrefix sbtp = new SearchByTitlePrefix(sc);
        System.out.println("Number of compares: " + ((CmpCnt) sbtp.cmpTitle).getCmpCnt());

        if (args.length > 1) {
            System.out.println("searching for: " + args[1]);
            Song[] byTitleResult = sbtp.search(args[1]);

            //Shows first <= 10 matches.
            System.out.println("Total number of songs: " + sc.getAllSongs().length);
            System.out.println("Total matches: " + byTitleResult.length);
            for (int i = 0; i < byTitleResult.length && i < 10; i++) {
                System.out.println(byTitleResult[i]);
            }
        }
        */
    }

}
