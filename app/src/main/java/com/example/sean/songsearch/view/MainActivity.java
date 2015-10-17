package com.example.sean.songsearch.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sean.songsearch.R;
import com.example.sean.songsearch.controller.SearchByArtistPrefix;
import com.example.sean.songsearch.controller.SearchByTitlePrefix;
import com.example.sean.songsearch.controller.SongCollection;
import com.example.sean.songsearch.model.Song;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    SongCollection sc;

    public void searchForTitle(View view){
        EditText searchedText = (EditText) findViewById(R.id.editText);
        String searchText = searchedText.getText().toString();
        ListView displayResults = (ListView) findViewById(R.id.displaySongs);
        TextView searchedTime = (TextView) findViewById(R.id.searchTime);

        if (!searchText.isEmpty()){
            long startTime = System.nanoTime();
            SearchByTitlePrefix sbtp = new SearchByTitlePrefix(sc);
            Song[] byTitleResult = sbtp.search(searchText);
            if (byTitleResult.length == 0){
                displayResults.setAdapter(null);
                searchedTime.setText("");
                Toast.makeText(getApplicationContext(), "No search results found.", Toast.LENGTH_SHORT).show();
            }else{
                long stopTime = System.nanoTime();
                long timeTook = stopTime - startTime;
                timeTook = TimeUnit.MILLISECONDS.convert(timeTook, TimeUnit.NANOSECONDS);
                Adapter temp = new ArrayAdapter<Song>(this,android.R.layout.simple_list_item_1,byTitleResult);
                searchedTime.setText(String.valueOf(timeTook) + " MS");
                displayResults.setAdapter((ListAdapter) temp);
            }

        }else{
            Toast.makeText(getApplicationContext(), "Please enter a valid Title to search for.", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchForArtist(View view){
        EditText searchedText = (EditText) findViewById(R.id.editText);
        ListView displayResults = (ListView) findViewById(R.id.displaySongs);
        String searchText = searchedText.getText().toString();
        TextView searchedTime = (TextView) findViewById(R.id.searchTime);

        if (!searchText.isEmpty()){
            long startTime = System.nanoTime();
            SearchByArtistPrefix sbap = new SearchByArtistPrefix(sc);
            Song[] byTitleResult = sbap.search(searchText);
            if (byTitleResult.length == 0){
                displayResults.setAdapter(null);
                searchedTime.setText("");
                Toast.makeText(getApplicationContext(), "No search results found.", Toast.LENGTH_SHORT).show();
            }else{
                long stopTime = System.nanoTime();
                long timeTook = stopTime - startTime;
                timeTook = TimeUnit.MILLISECONDS.convert(timeTook, TimeUnit.NANOSECONDS);
                Adapter temp = new ArrayAdapter<Song>(this,android.R.layout.simple_list_item_1,byTitleResult);
                searchedTime.setText(String.valueOf(timeTook) + " MS");
                displayResults.setAdapter((ListAdapter) temp);
            }
        }else{
            Toast.makeText(getApplicationContext(), "Please enter a valid Title to search for.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void buildSongCollection(String siteAddress){
        try {
            // Create a URL for the desired page
            URL url = new URL(siteAddress);
            // Read all the text returned by the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            sc = new SongCollection(reader);
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String urlToPullFrom = "http://cs.usm.maine.edu/class/cos285/prog1/allSongs.txt";
        new Thread(new Runnable() {
            @Override
            public void run() {
            buildSongCollection(urlToPullFrom);
            }
        }).start();
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
