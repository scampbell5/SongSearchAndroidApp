package com.example.sean.songsearch.controller;

import com.example.sean.songsearch.model.Song;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/*
 * SongCollection.java
 * Read the specified data file and build an array of songs.
 * 
 * Starting code by Prof. Boothe 2015
 */
public class SongCollection {

    private Song[] songs;

    //Song collection takes a filename, reads in the contents of file, parses 'Artist', 'Title', 'Lyrics' into a new object<Song>.
    //Songs are stored in a temp ArrayList. After all contents of file have been read, sorts the temp ArrayList, initializes songs[] to size
    //of temp ArrayList, and assigns sorts contents to songs[].
    public SongCollection(BufferedReader songReader) {
        ArrayList<Song> tmpSongs = new ArrayList<>();

        //Test if file exists.

        try {
            String tmpString;
            while ((tmpString = songReader.readLine()) != null){
                char tmpDelim;
                int tmpStringLength;
                tmpStringLength = tmpString.length()-1;
                String tmpArtist = "";
                String tmpTitle = "";
                StringBuilder tmpLyrics = new StringBuilder();


                //This portion retrieves the artist. Assumes artist is always on one line.
                if (tmpString.contains("ARTIST=")){
                    tmpArtist = tmpString.substring(8,tmpStringLength);
                    if ((tmpString = songReader.readLine()) != null){
                        tmpStringLength = tmpString.length()-1;
                    }
                }

                //This portion retrieves the song title. Assumes the title is always on one line.
                if (tmpString.contains("TITLE=")){
                    tmpTitle = tmpString.substring(7,tmpStringLength);
                    tmpString = songReader.readLine();
                }

                //This portion retrieves the lyrics portion, lyrics can be on multiple lines and must handle as such.
                //It detects the last character to see if it is a '"', this is end of lyrics
                if (tmpString.contains("LYRICS=")){
                    tmpString = tmpString.substring(8, tmpString.length());
                    tmpStringLength = tmpString.length()-1;
                    tmpDelim = tmpString.charAt(tmpStringLength);

                    //If Lyrics are on one line, append the string.
                    if (tmpDelim == '"') {
                        tmpLyrics.append(tmpString.substring(0,tmpStringLength + 1));
                        //Lyrics are not on one line, must parse until we reach the end of lyrics.
                        //Assumes delimiter == '"' at end of line.
                    }else
                        tmpLyrics.append(tmpString);
                    while (tmpDelim != '"'){
                        if ((tmpString = songReader.readLine()) != null){
                            tmpStringLength = tmpString.length()-1;

                            //If tmpString does not contain anything, sets tmpDelim = ' ' which will pass the test and continue.
                            //Issue was noticed when a line was just a "\n" character. Implemented this solution to rectify issue.
                            if (tmpStringLength + 1 > 0){
                                tmpDelim = tmpString.charAt(tmpStringLength);
                            }else{
                                tmpDelim = ' ';
                            }
                            if (tmpDelim == '"'){
                                break;
                            }else{
                                tmpLyrics.append("\n");
                                tmpLyrics.append(tmpString);
                            }
                        }else{
                            break;
                        }

                    }
                }
                //Creates new song and adds to list based on all information.
                tmpSongs.add(new Song(tmpArtist,tmpTitle,tmpLyrics.toString()));
            }

            //Sorts temp ArrayList with songs, initializes size of songs[] and assigns temp ArrayList to song[] already sorted.
            Collections.sort(tmpSongs);
            songs = new Song[tmpSongs.size()];
            tmpSongs.toArray(songs);
        }
        //If file does not exist, print error message and initialize songs[] to empty array.
        catch (FileNotFoundException e){
            System.out.println("No file found, please check the filename and try running again.");
            songs = new Song[0];
        }
        catch (IOException e){
            System.out.println("Issue reading from file. Please make sure file is not open.");
            songs = new Song[0];
        }
        // read in the song file and build the songs array

        // sort the songs array
    }

    // return the songs array
    // this is used as the data source for building other data structures
    public Song[] getAllSongs() {
        return songs;
    }



    // testing method
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: prog songfile");
            return;
        }
        /*
        SongCollection sc = new SongCollection(args[0]);
        int songsLength = sc.getAllSongs().length;

        // todo: show song count and first 10 songs (name & title only, 1 per line)
        System.out.println("Total songs = " + songsLength + ", first songs:");
        //Prints out up to 10 songs, or all songs if songs[] contains less than 10 songs
        for (int i = 0; i < songsLength && i < 10; i++){
            System.out.print(sc.getAllSongs()[i] + "\n");
        }*/
    }
}