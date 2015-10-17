# SongSearchAndroidApp
Song Search Android App is an app that allows you to currently search for a song title or by an artist prefix. App currently pulls from a list of 10,514 songs hosted on USM's site.


App uses a custom data structure known as a Ragged Array List that was built in the data structures class that I am currently taken. There are currently two different ways to search for a song, by Title which utilizes the Ragged Array List, and by Artist which uses a standard array. 

# How it Works
1. When app initializes, it pulls a predfined list of song files from USM's website
2. Song Collection parses and stores the songs from the website in an array
3. SearchByArtistPrefix and SearchByTitlePrefix both accept a SongCollection
4. SearchByArtistPrefix uses a standard Java Array, while SearchByTitlePrefix uses the Ragged Array List.
5. Both classes return an array of found songs that match the artist or title prefix.
6. System time is recorded when the search starts, and when the search ends. 
7. List of songs is displayed to a ListView
8. Search time is displayed to a TextView

Note: When SearchByTitlePrefix is called, the class has to build the RaggedArrayList, while SearchByArtistPrefix uses the existing array of songs. 

#

#To Do:
1. Display Lyrics of songs, when song is selected from ListView
2. Add additional sources to pull songs from
3. Customize look and feel of app using Material Design principals
4. Add additional features as class progresses 

#



