package com.bitsgoa.macclub.news.plus.news;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<String> majorHeadlines = new ArrayList<>();
    ArrayList<String> headLineLinks = new ArrayList<>();
    int articlesLoaded = 0;
    HashMap<String, String> headLinesMAPLinks = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Load up ListView's ArrayLists on App Startup
        majorHeadlines = new ArrayList<>();
        headLineLinks = new ArrayList<>();
        headLinesMAPLinks = new HashMap<>();
        //Custom AsyncTask extends the class
        AsyncTasker tasker = new AsyncTasker();
        try {
            majorHeadlines = tasker.execute(majorHeadlines, headLineLinks).get();
        } catch (InterruptedException e) {
            Log.e("TAG", "Interrupted!");
        } catch (ExecutionException e) {
            Log.e("TAG", "Execution Error!");
            e.printStackTrace();
        }

        //FAB at HomePage
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setRippleColor(Color.parseColor("#969696"));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTasker tasker = new AsyncTasker();
                try {
                    majorHeadlines = tasker.execute(majorHeadlines, headLineLinks).get();
                } catch (InterruptedException e) {
                    Log.e("TAG", "Interrupted!");

                } catch (ExecutionException e) {
                    Log.e("TAG", "Execution Error!");

                }
                Log.i("TAG final size", Integer.toString(majorHeadlines.size()));
                if (majorHeadlines.size() > 0) {
                    Snackbar.make(view, "Refreshed! Loaded " + Integer.toString(articlesLoaded) + " more articles.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    articlesLoaded = 0;
                }

            }
        });


        final ListView homePageListView = (ListView) findViewById(R.id.HomePageListView);
        CustomAdapter adapter = new CustomAdapter(this, majorHeadlines);
        homePageListView.setAdapter(adapter);
        homePageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.parse(headLineLinks.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

//bye
        //Drawer Stuff
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
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
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class AsyncTasker extends AsyncTask<ArrayList<String>, String, ArrayList<String>> {
        ArrayList asynclist;
        ArrayList asynclink;

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.i("TAG", values[0]);

        }

        @Override
        protected ArrayList doInBackground(ArrayList<String>... params) {
            asynclist = params[0];
            asynclink = params[1];
            try {
                URL url = new URL("http://www.pcworld.com/index.rss");
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = xmlPullParserFactory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF-8");
                boolean insideItem = false;
                int eventType = xpp.getEventType();
                articlesLoaded=0;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {

                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {

                                asynclist.add(xpp.nextText());
                                articlesLoaded++;
                            } //extract the headline
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem)
                                asynclink.add(xpp.nextText()); //extract the link of article
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }
                    eventType = xpp.next(); //move to next element
                }
            } catch (MalformedURLException e) {
                Log.e("TAG", "MalformedURLException!");

                e.printStackTrace();
            } catch (IOException e) {
                Log.e("TAG", "IOException!");

                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("TAG", "XMLPullParserException!");

                e.printStackTrace();
            }

            majorHeadlines = (asynclist);
            headLineLinks = asynclink;
            return asynclist;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
        }
    }

}
