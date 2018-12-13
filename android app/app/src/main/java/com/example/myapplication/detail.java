package com.example.myapplication;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;


import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.TableLayout;
import android.widget.*;
import android.view.ViewGroup.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import android.text.Html;
import android.text.method.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyStore;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;

public class detail extends AppCompatActivity implements OnMapReadyCallback{
    private String eventname="",venuename="",eventurl="",eventtime="",eventID="";
    public RequestQueue queue;
    public LinearLayout progressLoading;
    public MoviesAdapter mAdapter;
    public TextView noRecord;
    public boolean noEvent = false;
    public boolean noArtist = false;
    public boolean noVenue = false;
    public boolean half1 = false;
    public boolean half2 = false;
    public boolean noUpcoming = false;
    private String[] title = {"event", "artist(s)", "venue", "upcoming"};
    public boolean finishEvent = false,finishArtist=false,finishPhoto=false,finishVenue=false,finishUpcoming=false;
    public int previous = 0;
    public List<Upcoming> movieList,changeMovie=new ArrayList<>();
    public List<String> uri,changeUri,bianUri=new ArrayList<>();
    public List<String> upName=new ArrayList<>(),upArtist=new ArrayList<>(),upDate=new ArrayList<>(),upTime=new ArrayList<>(),upType=new ArrayList<>();
    public float lat=0,lon=0;
    public String artist1 = "",artist2 = "",segment = "";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        progressLoading = (LinearLayout) findViewById(R.id.progress_loading);
        progressLoading.setVisibility(View.VISIBLE);
        noRecord = (TextView) findViewById(R.id.noRecord);
        noRecord.setVisibility(View.INVISIBLE);
        final String[] category={"Default","Event Name","Time","Artist","Type"};
        String[] order = {"Ascending","Descending"};
        final Spinner spinner,spinner2;
        ArrayAdapter <String> adapter;
        spinner = (Spinner) findViewById(R.id.sortbycategory);
        spinner2 = (Spinner) findViewById(R.id.sortbyorder);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,category);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text= spinner.getItemAtPosition(position).toString();
                Log.e("hei","2");
                if (text.equals("Default"))
                {
                    spinner2.setEnabled(false);
                   // uri = changeUri;
                   if (changeMovie.size()!=0)
                   {
                       Log.e("11","lalal");

                       RecyclerView recyclerView = findViewById(R.id.upcoming);
                       mAdapter = new MoviesAdapter(changeMovie);
                       recyclerView.setAdapter(mAdapter);
                       uri = new ArrayList<>();
                       for (int i=0;i<changeUri.size();i++)
                       {
                           uri.add(changeUri.get(i));
                      }


                   }




                   // Log.e("11",changeMovie.toString());

                }
                else
                {
                    spinner2.setEnabled(true);
                    sort(spinner.getItemAtPosition(position).toString(),spinner2.getSelectedItem().toString());
                   RecyclerView recyclerView = findViewById(R.id.upcoming);
                   mAdapter = new MoviesAdapter(movieList);
                   recyclerView.setAdapter(mAdapter);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    sort(spinner.getSelectedItem().toString(),spinner2.getItemAtPosition(position).toString());


                    RecyclerView recyclerView = findViewById(R.id.upcoming);
                    mAdapter = new MoviesAdapter(movieList);
                    recyclerView.setAdapter(mAdapter);



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,order);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner2.setAdapter(adapter);
        spinner2.setEnabled(false);

        Log.e("h", "haole");
        try {
            //    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            //   mViewPager = (ViewPager) findViewById(R.id.container);
            // setupViewPager(mViewPager);
            //  TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            // tabLayout.setupWithViewPager(mViewPager);
        } catch (Exception e) {
            Log.e("cuo:", e.getMessage());
        }
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabPagerAdapter adapter1 = new TabPagerAdapter();
        viewPager.setAdapter(adapter1);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(R.drawable.event);
        tabs.getTabAt(1).setIcon(R.drawable.artist);
        tabs.getTabAt(2).setIcon(R.drawable.venue);
        tabs.getTabAt(3).setIcon(R.drawable.upcoming);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 3) {
                    noRecord.setVisibility(View.INVISIBLE);
                    if (noUpcoming)
                    {
                        noRecord.setVisibility(View.VISIBLE);
                    }
                    ScrollView s = (ScrollView) findViewById(R.id.scrollview_event);
                    s.setVisibility(View.INVISIBLE);
                    ScrollView s2 = (ScrollView) findViewById(R.id.scrollview_artist);
                    s2.setVisibility(View.INVISIBLE);
                    ScrollView s3 = (ScrollView) findViewById(R.id.scrollview_venue);
                    s3.setVisibility(View.INVISIBLE);
                    LinearLayout s4 = findViewById(R.id.linear_upcoming);
                    s4.setVisibility(View.VISIBLE);
                    TranslateAnimation alphaAnimation;
                    if (previous > position)
                    {
                        alphaAnimation=new TranslateAnimation(-800,0,0,0);
                        alphaAnimation.setDuration(50);
                    }
                    else
                    {
                        alphaAnimation=new TranslateAnimation(800,0,0,0);
                        alphaAnimation.setDuration(50);
                    }
                    previous = position;
                    s4.setAnimation(alphaAnimation);

                }
                if (position == 1) {

                    noRecord.setVisibility(View.INVISIBLE);
                    if (noArtist)
                    {
                        noRecord.setVisibility(View.VISIBLE);
                    }
                    ScrollView s = (ScrollView) findViewById(R.id.scrollview_event);
                    s.setVisibility(View.INVISIBLE);
                    ScrollView s2 = (ScrollView) findViewById(R.id.scrollview_artist);
                    s2.setVisibility(View.VISIBLE);
                    ScrollView s3 = (ScrollView) findViewById(R.id.scrollview_venue);
                    s3.setVisibility(View.INVISIBLE);
                    LinearLayout s4 = findViewById(R.id.linear_upcoming);
                    s4.setVisibility(View.INVISIBLE);
                    TranslateAnimation alphaAnimation;
                    if (previous > position)
                    {
                        alphaAnimation=new TranslateAnimation(-800,0,0,0);
                        alphaAnimation.setDuration(50);
                    }
                    else
                    {
                        alphaAnimation=new TranslateAnimation(800,0,0,0);
                        alphaAnimation.setDuration(50);
                    }
                    previous = position;
                    s2.setAnimation(alphaAnimation);
                }
                if (position == 0) {
                    Log.e("ff","zenmele");
                    noRecord.setVisibility(View.INVISIBLE);
                    if (noEvent)
                    {
                        noRecord.setVisibility(View.VISIBLE);
                    }
                    ScrollView s = (ScrollView) findViewById(R.id.scrollview_event);
                    s.setVisibility(View.VISIBLE);
                    ScrollView s2 = (ScrollView) findViewById(R.id.scrollview_artist);
                    s2.setVisibility(View.INVISIBLE);
                    ScrollView s3 = (ScrollView) findViewById(R.id.scrollview_venue);
                    s3.setVisibility(View.INVISIBLE);
                    LinearLayout s4 = findViewById(R.id.linear_upcoming);
                    s4.setVisibility(View.INVISIBLE);
                    TranslateAnimation alphaAnimation;
                    if (previous > position)
                    {
                        alphaAnimation=new TranslateAnimation(-800,0,0,0);
                        alphaAnimation.setDuration(50);
                    }
                    else
                    {
                        alphaAnimation=new TranslateAnimation(800,0,0,0);
                        alphaAnimation.setDuration(50);
                    }
                    previous = position;
                    s.setAnimation(alphaAnimation);
                }
                if (position == 2) {
                    noRecord.setVisibility(View.INVISIBLE);
                    if (noVenue)
                    {
                        noRecord.setVisibility(View.VISIBLE);
                    }
                    ScrollView s = (ScrollView) findViewById(R.id.scrollview_event);
                    s.setVisibility(View.INVISIBLE);
                    ScrollView s2 = (ScrollView) findViewById(R.id.scrollview_artist);
                    s2.setVisibility(View.INVISIBLE);
                    ScrollView s3 = (ScrollView) findViewById(R.id.scrollview_venue);
                    s3.setVisibility(View.VISIBLE);
                    LinearLayout s4 = findViewById(R.id.linear_upcoming);
                    s4.setVisibility(View.INVISIBLE);
                    TranslateAnimation alphaAnimation;
                    if (previous > position)
                    {
                        alphaAnimation=new TranslateAnimation(-800,0,0,0);
                        alphaAnimation.setDuration(50);
                    }
                    else
                    {
                        alphaAnimation=new TranslateAnimation(800,0,0,0);
                        alphaAnimation.setDuration(50);
                    }
                    previous = position;
                    s3.setAnimation(alphaAnimation);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });



        ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
        s.setVisibility(View.INVISIBLE);
        s = (ScrollView)findViewById(R.id.scrollview_artist);
        s.setVisibility(View.INVISIBLE);
        s = (ScrollView)findViewById(R.id.scrollview_venue);
        s.setVisibility(View.INVISIBLE);
        LinearLayout s4 = (LinearLayout)findViewById(R.id.linear_upcoming);
        s4.setVisibility(View.INVISIBLE);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        queue = Volley.newRequestQueue(this);
        String url = "";
        if (!MainActivity.chooseID.equals(""))
        {
            url ="http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/detail?id="+MainActivity.chooseID;
        }
        else
        {
            url ="http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/detail?id="+SearchResult.chooseID;
        }


        Log.e("xxx","yyy");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("11",response);
                        JSONObject event = new JSONObject();
                        String text;
                        noEvent = false;
                        noRecord.setVisibility(View.INVISIBLE);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            event = jsonObject.getJSONObject("_embedded").getJSONArray("events").getJSONObject(0);
                        }
                        catch (Exception e)
                        {
                            noEvent = true;
                            noRecord.setVisibility(View.VISIBLE);
                        }
                        try
                        {

                            eventID = event.getString("id");

                        }
                        catch (Exception e)
                        {

                            eventID = "";
                        }
                        try
                        {
                            setTitle(event.getString("name"));
                            eventname = event.getString("name");

                        }
                        catch (Exception e)
                        {
                            setTitle("");
                            eventname = "";
                        }
                        TableLayout t = (TableLayout)findViewById(R.id.tableEvent2);
                        try
                        {


                                TableRow tablerow = new TableRow(detail.this);
                                tablerow.setPadding(0,0,0,20);
                                TextView textView = new TextView(detail.this);
                                textView.setWidth(300);


                                textView.setText("Artist/Team(s)");
                                textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                tablerow.addView(textView);
                                JSONArray att = event.getJSONObject("_embedded").getJSONArray("attractions");
                                String temp = "";
                                for (int i=0;i<att.length();i++)
                                {

                                    temp += att.getJSONObject(i).getString("name");
                                    if (i==0)
                                    {
                                        artist1 = att.getJSONObject(i).getString("name");
                                    }
                                    if (i == 1)
                                    {
                                        artist2 = att.getJSONObject(i).getString("name");
                                    }
                                    temp += "|";
                                }


                                if (temp.substring(temp.length()-1).equals("|"))
                                {
                                    temp = temp.substring(0,temp.length()-1);
                                }
                                Log.e("11",temp);
                                TextView textView2 = new TextView(detail.this);
                                textView2.setWidth(720);
                                textView2.setText(temp);
                                tablerow.addView(textView2);
                                t.addView(tablerow);


                        }
                        catch (Exception e)
                        {
                            Log.e("cuo","youcuo");
                        }
                        try
                        {


                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);
                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);


                            textView.setText("Venue");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);
                            String temp = event.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
                            venuename = temp;
                            Log.e("11",temp);
                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(temp);
                            tablerow.addView(textView2);
                            t.addView(tablerow);


                        }
                        catch (Exception e)
                        {
                            Log.e("cuo",e.getMessage());
                            venuename = "";
                        }
                        searchUpcoming();
                        try {
                            Thread.sleep(1000);
                        }
                        catch (Exception e)
                        {

                        }
                        searchVenue();
                        String time = "";
                        try
                        {
                           // time += ;
                            //SimpleDateFormat CeshiFmt1=new SimpleDateFormat("mm dd,yyyy");
                            //time += CeshiFmt1.parse("2016-10-24");
                            String string = event.getJSONObject("dates").getJSONObject("start").getString("localDate");
                            eventtime = string;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date d = sdf.parse(string,new ParsePosition(0));
                            Log.e("1",d.toString());
                           SimpleDateFormat CeshiFmt0=new SimpleDateFormat("MMM dd, yyyy");
                           time += CeshiFmt0.format(d);
                        }
                        catch (Exception e)
                        {

                            Log.e("cuo",e.getMessage());

                        }
                        try
                        {
                            if (!time.equals(""))
                            {
                                time += " ";
                                eventtime += " ";
                            }
                            time += event.getJSONObject("dates").getJSONObject("start").getString("localTime");
                            eventtime += event.getJSONObject("dates").getJSONObject("start").getString("localTime");
                        }
                        catch (Exception e)
                        {
                            Log.e("cuo",e.getMessage());

                        }
                        Log.e("11",time);
                        if (!time.equals(""))
                        {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);
                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);


                            textView.setText("Time");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);



                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(time);
                            tablerow.addView(textView2);
                            t.addView(tablerow);
                        }


                        String genre = "";
                        try
                        {
                            genre += event.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                            segment = event.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                        }
                        catch (Exception e)
                        {

                            Log.e("cuo",e.getMessage());

                        }
                        if (!artist1.equals(""))
                        {
                            TextView textView = new TextView(detail.this);
                            textView.setGravity(Gravity.CENTER);
                            textView.setText(artist1);

                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear1);
                            linearLayout.addView(textView);
                        }
                        if (!artist2.equals(""))
                        {
                            TextView textView = new TextView(detail.this);
                            textView.setGravity(Gravity.CENTER);
                            textView.setText(artist2);

                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear3);
                            linearLayout.addView(textView);
                        }
                        searchArtist();
                        searchPhoto();
                        try
                        {
                            if (!genre.equals(""))
                            {
                                genre += " | ";
                            }
                            genre += event.getJSONArray("classifications").getJSONObject(0).getJSONObject("genre").getString("name");

                        }
                        catch (Exception e)
                        {
                            Log.e("cuo",e.getMessage());

                        }

                        if (!genre.equals(""))
                        {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);
                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);


                            textView.setText("Category");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);



                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(genre);
                            tablerow.addView(textView2);
                            t.addView(tablerow);
                        }
                        String price = "";
                        try
                        {
                            DecimalFormat df = new DecimalFormat("#.00");

                            price += "$"+df.format(Double.parseDouble(event.getJSONArray("priceRanges").getJSONObject(0).getString("min")));

                        }
                        catch (Exception e)
                        {

                            Log.e("cuo",e.getMessage());

                        }
                        try
                        {
                            if (!price.equals(""))
                            {
                                price += "~";
                            }

                            DecimalFormat df = new DecimalFormat("#.00");

                            price += "$"+df.format(Double.parseDouble(event.getJSONArray("priceRanges").getJSONObject(0).getString("max")));



                        }
                        catch (Exception e)
                        {
                            Log.e("cuo",e.getMessage());

                        }

                        if (!price.equals(""))
                        {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);
                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);


                            textView.setText("Price Range");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);



                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(price);
                            tablerow.addView(textView2);
                            t.addView(tablerow);
                        }
                        try
                        {


                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);
                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);


                            textView.setText("Ticket Stauts");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);
                            String temp = event.getJSONObject("dates").getJSONObject("status").getString("code");

                            Log.e("11",temp);
                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(temp);
                            tablerow.addView(textView2);
                            t.addView(tablerow);


                        }
                        catch (Exception e)
                        {
                            Log.e("cuo",e.getMessage());

                        }
                        try
                        {


                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);
                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);


                            textView.setText("Buy Ticket At");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);
                            String temp = event.getString("url");
                            eventurl = temp;
                            Log.e("11",temp);
                            TextView textView2 = new TextView(detail.this);
                           // textView2.setWidth(720);
                            CharSequence charSequence = Html.fromHtml("<a href='"+temp+"'>Ticketmaster</a>");
                            textView2.setText(charSequence);
                            textView2.setMovementMethod(LinkMovementMethod.getInstance());
                            tablerow.addView(textView2);
                            t.addView(tablerow);


                        }
                        catch (Exception e)
                        {
                            Log.e("cuo",e.getMessage());
                            eventurl = "";
                        }
                        try
                        {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);
                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);


                            textView.setText("Seat Map");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);
                            String temp = event.getJSONObject("seatmap").getString("staticUrl");

                            Log.e("11",temp);
                            TextView textView2 = new TextView(detail.this);
                          //  textView2.setWidth(720);
                            CharSequence charSequence = Html.fromHtml("<a href='"+temp+"'>View Here</a>");
                            textView2.setText(charSequence);
                            textView2.setMovementMethod(LinkMovementMethod.getInstance());
                            tablerow.addView(textView2);
                            t.addView(tablerow);


                        }
                        catch (Exception e)
                        {
                            Log.e("cuo",e.getMessage());

                        }
                        finishEvent = true;
                        if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
                        {
                            ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                            s.setVisibility(View.VISIBLE);
                            progressLoading.setVisibility(View.INVISIBLE);
                            if (noEvent)
                            {
                                noRecord.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                noRecord.setVisibility(View.INVISIBLE);
                            }
                        }

                    }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(detail.this,"Failed to get results",Toast.LENGTH_LONG).show();
                progressLoading.setVisibility(View.INVISIBLE);
                Log.e("11","222");
            }
        });
        queue.add(stringRequest);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ttt, menu);
        SharedPreferences s = getSharedPreferences("Favorite", Context.MODE_PRIVATE);

        int num = s.getInt("num",0);
        if (MainActivity.chooseID.equals(""))
        {
            for (int i=0;i<num;i++)
            {

                if (SearchResult.chooseID.equals(s.getString("id_"+i,"x")))
                {
                    menu.getItem(0).setIcon(R.drawable.heart_fill_red);
                }
            }
        }
       else
        {
            for (int i=0;i<num;i++)
            {

                if (MainActivity.chooseID.equals(s.getString("id_"+i,"x")))
                {
                    menu.getItem(0).setIcon(R.drawable.heart_fill_red);
                }
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            this.finish(); // back button
            return true;
        }
        if (item.getItemId() == R.id.favorite)
        {

            Log.e("choose:",eventname+" "+segment+" "+venuename+" "+eventtime+" "+eventID);
            //ActionMenuItemView actionMenuItemView = (ActionMenuItemView) findViewById(R.id.twitter);
            Log.e("1",item.getIcon().toString());
            if (item.getIcon().getConstantState().equals(getResources().getDrawable(R.drawable.heart_fill_white).getConstantState()))
            {
                item.setIcon(R.drawable.heart_fill_red);
                 SharedPreferences share = getSharedPreferences("Favorite", Context.MODE_PRIVATE);
                 int num= share.getInt("num",0);

                 SharedPreferences sharedPreferences = getSharedPreferences("Favorite", Context.MODE_PRIVATE);
                 SharedPreferences.Editor editor = sharedPreferences.edit();
                 editor.putString("event_"+num,eventname);
                if (segment.equals("Music")) {

                    editor.putInt("segment_"+num,R.drawable.music_icon);
                }

                if (segment.equals("Sports")) {
                    editor.putInt("segment_"+num,R.drawable.sport_icon);
                }
                if (segment.equals("Arts & Theatre")) {
                    editor.putInt("segment_"+num,R.drawable.art_icon);
                }
                if (segment.equals("Film")) {
                    editor.putInt("segment_"+num,R.drawable.film_icon);
                }
                if (segment.equals("Miscellaneous")) {
                    editor.putInt("segment_"+num,R.drawable.miscellaneous_icon);
                }
                if (segment.equals(""))
                {
                    editor.putInt("segment_"+num,R.drawable.common_google_signin_btn_icon_light_normal_background);
                }

                editor.putString("venue_"+num,venuename);
                editor.putString("time_"+num,eventtime);
                editor.putString("id_"+num,eventID);
                editor.putInt("num",num+1);
                 editor.commit();

                SharedPreferences s = getSharedPreferences("Favorite", Context.MODE_PRIVATE);
                Log.e("xx:",String.valueOf(s.getInt("num",-1))+",");
                num = s.getInt("num",0);
                Toast.makeText(getApplicationContext(),eventname+" was added to favorites",Toast.LENGTH_LONG).show();
                for (int i=0;i<num;i++)
                {
                    Log.e("xx:",s.getString("event_"+i,"x"));
                  //  Log.e("xx:",s.getString("segment_"+i,"x"));
                    Log.e("xx:",s.getString("venue_"+i,"x"));
                    Log.e("xx:",s.getString("time_"+i,"x"));
                    Log.e("xx:",s.getString("id_"+i,"x"));
                }
                Intent intent = new Intent("jason.broadcast.action");

                intent.putExtra("index", num);
                sendBroadcast(intent);
            }
            else
            {



                item.setIcon(R.drawable.heart_fill_white);
                SharedPreferences s = getSharedPreferences("Favorite", Context.MODE_PRIVATE);

                int num = s.getInt("num",0);
                List <String> tempID = new ArrayList<>();
                List <String> tempEvent= new ArrayList<>();
                List <Integer> tempSegment = new ArrayList<>();
                List <String> tempVenue = new ArrayList<>();
                List <String> tempTime = new ArrayList<>();
                for (int i=0;i<num;i++)
                {
                    tempID.add(s.getString("id_"+i,"x"));
                    tempEvent.add(s.getString("event_"+i,"x"));
//                    tempSegment.add(s.getString("segment_"+i,"x"));
                    tempSegment.add(s.getInt("segment_"+i,R.drawable.common_google_signin_btn_icon_light_normal_background));
                    tempVenue.add(s.getString("venue_"+i,"x"));
                    tempTime.add(s.getString("time_"+i,"x"));
                }
                SharedPreferences.Editor editor = s.edit();

                for (int i=0;i<tempID.size();i++)
                {
                    Log.e("bi",tempID.get(i)+"     "+eventID);
                    if (tempID.get(i).equals(eventID))
                    {
                        Toast.makeText(getApplicationContext(),eventname+" was removed from favorites",Toast.LENGTH_LONG).show();
                        tempID.remove(i);
                        tempEvent.remove(i);
                        tempSegment.remove(i);
                        tempTime.remove(i);
                        tempVenue.remove(i);
                        editor.clear();

                        editor.putInt("num",num-1);

                        for (int j=0;j<tempID.size();j++)
                        {
                            editor.putString("event_"+j,tempEvent.get(j));
                            //editor.putString("segment_"+j,tempSegment.get(j));
                            editor.putInt("segment_"+j,tempSegment.get(j));
                            editor.putString("venue_"+j,tempVenue.get(j));
                            editor.putString("time_"+j,tempTime.get(j));
                            editor.putString("id_"+j,tempID.get(j));
                        }
                        editor.commit();
                        Intent intent = new Intent("jason.broadcast.action");

                        intent.putExtra("index", i);
                        sendBroadcast(intent);
                        break;
                    }
                }
                num = s.getInt("num",0);
                for (int i=0;i<num;i++)
                {
                    Log.e("xx:",s.getString("event_"+i,"x"));
                    //Log.e("xx:",s.getString("segment_"+i,"x"));
                    Log.e("xx:",s.getString("venue_"+i,"x"));
                    Log.e("xx:",s.getString("time_"+i,"x"));
                    Log.e("xx:",s.getString("id_"+i,"x"));
                }
            }
           // Log.e("inon:",actionMenuItemView.getPointerIcon());
            //actionMenuItemView.setIcon(getDrawable(R.drawable.art_icon));
            return true;
        }
        if (item.getItemId() == R.id.twitter)
        {
            //Log.e("choose:","twitter");

            try
            {
                String twittertext = "";
                twittertext = "https://twitter.com/intent/tweet?text="+escape("Check out "+eventname+" located at "+venuename+". Website: "+eventurl+" #CSCI571EventSearch");

                Log.e("twi:",twittertext);
                Intent intent = new Intent();
                intent.setData(Uri.parse(twittertext));
                intent.setAction(Intent.ACTION_VIEW);
                this.startActivity(intent);
            }
            catch (Exception e)
            {
                Log.e("cuo",e.getMessage());
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }
    public void searchArtist()
    {

        if (segment.equals("Music"))
        {
            if (!artist1.equals(""))
            {
                String url ="http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/artist?artistming="+artist1;
                Log.e("11",url);


                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                int art1 = -1;
                                JSONObject jsonObject = new JSONObject();
                                JSONArray item = new JSONArray();
                                try {
                                    jsonObject = new JSONObject(response);
                                    item = jsonObject.getJSONObject("artists").getJSONArray("items");

                                    for (int i=0;i<item.length();i++)
                                    {
                                        if (item.getJSONObject(i).getString("name").equals(artist1))
                                        {
                                            art1 = i;
                                            break;
                                        }
                                    }

                                }
                                catch (Exception e)
                                {

                                }
                                if (art1 != -1)
                                {
                                    //TableLayout t = (TableLayout) findViewById(R.id.tableArtist);
                                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear1);
                                    TableLayout t = new TableLayout(detail.this);
                                    try {
                                        /*

                                        TextView textView = new TextView(detail.this);
                                        textView.setText(item.getJSONObject(art1).getString("name"));

                                        textView.setGravity(Gravity.CENTER);
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        linearLayout.addView(textView);*/
                                        /*
                                        l.addView(textView);
                                           // Log.e("1",item.getJSONObject(art1).getString("name"));
                                        TableRow tablerow = new TableRow(detail.this);
                                        tablerow.setPadding(0,0,0,20);
                                        TextView textView = new TextView(detail.this);

                                        textView.setText(item.getJSONObject(art1).getString("name"));
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        tablerow.addView(textView);
                                        tablerow.setGravity(Gravity.CENTER);
                                        t.addView(tablerow);*/

                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    try {

                                        TableRow tablerow = new TableRow(detail.this);
                                        tablerow.setPadding(0,0,0,20);
                                        TextView textView = new TextView(detail.this);
                                        textView.setWidth(300);


                                        textView.setText("Name");
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        tablerow.addView(textView);
                                        String temp = item.getJSONObject(art1).getString("name");


                                        TextView textView2 = new TextView(detail.this);
                                        textView2.setWidth(720);
                                        textView2.setText(temp);
                                        tablerow.addView(textView2);
                                        t.addView(tablerow);
                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    try {
                                        TableRow tablerow = new TableRow(detail.this);
                                        tablerow.setPadding(0,0,0,20);
                                        TextView textView = new TextView(detail.this);
                                        textView.setWidth(300);


                                        textView.setText("Followers");
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        tablerow.addView(textView);
                                        DecimalFormat df = new DecimalFormat("#,###");
                                        String temp = df.format(Integer.parseInt(item.getJSONObject(art1).getJSONObject("followers").getString("total")));


                                        TextView textView2 = new TextView(detail.this);
                                        textView2.setWidth(720);
                                        textView2.setText(temp);
                                        tablerow.addView(textView2);
                                        t.addView(tablerow);

                                            Log.e("1",item.getJSONObject(art1).getJSONObject("followers").getString("total"));

                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    try {
                                        TableRow tablerow = new TableRow(detail.this);
                                        tablerow.setPadding(0,0,0,20);
                                        TextView textView = new TextView(detail.this);
                                        textView.setWidth(300);


                                        textView.setText("Popularity");
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        tablerow.addView(textView);

                                        String temp = item.getJSONObject(art1).getString("popularity");


                                        TextView textView2 = new TextView(detail.this);
                                        textView2.setWidth(720);
                                        textView2.setText(temp);
                                        tablerow.addView(textView2);
                                        t.addView(tablerow);


                                            Log.e("1",item.getJSONObject(art1).getString("popularity"));

                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    try {

                                        TableRow tablerow = new TableRow(detail.this);
                                        tablerow.setPadding(0,0,0,20);
                                        TextView textView = new TextView(detail.this);
                                        textView.setWidth(300);


                                        textView.setText("Check At");
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        tablerow.addView(textView);

                                        String temp = item.getJSONObject(art1).getJSONObject("external_urls").getString("spotify");
                                        CharSequence charSequence = Html.fromHtml("<a href='"+temp+"'>Spotify</a>");

                                        TextView textView2 = new TextView(detail.this);
                                        textView2.setWidth(720);
                                        textView2.setText(charSequence);
                                        textView2.setMovementMethod(LinkMovementMethod.getInstance());
                                        tablerow.addView(textView2);
                                        t.addView(tablerow);

                                        Log.e("1",item.getJSONObject(art1).getJSONObject("external_urls").getString("spotify"));

                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    linearLayout.addView(t);
                                }
                                finishArtist = true;
                                if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
                                {
                                    ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                                    s.setVisibility(View.VISIBLE);
                                    progressLoading.setVisibility(View.INVISIBLE);
                                    if (noEvent)
                                    {
                                        noRecord.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        noRecord.setVisibility(View.INVISIBLE);
                                    }
                                }

                            }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(detail.this,"Failed to get results",Toast.LENGTH_LONG).show();
                        progressLoading.setVisibility(View.INVISIBLE);
                        Log.e("11","222");
                    }
                });
                queue.add(stringRequest);

            }
            else
            {
                finishArtist = true;
                if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
                {
                    ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                    s.setVisibility(View.VISIBLE);
                    progressLoading.setVisibility(View.INVISIBLE);
                    if (noEvent)
                    {
                        noRecord.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        noRecord.setVisibility(View.INVISIBLE);
                    }
                }
            }
            if (!artist2.equals(""))
            {
                String url ="http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/artist?artistming="+artist2;
                Log.e("11",url);


                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                int art2 = -1;
                                Log.e("url2",response);
                                JSONObject jsonObject = new JSONObject();
                                JSONArray item = new JSONArray();
                                try {
                                    jsonObject = new JSONObject(response);
                                    item = jsonObject.getJSONObject("artists").getJSONArray("items");

                                    for (int i=0;i<item.length();i++)
                                    {
                                        if (item.getJSONObject(i).getString("name").equals(artist2))
                                        {
                                            art2 = i;
                                            break;
                                        }
                                    }

                                }
                                catch (Exception e)
                                {

                                }

                                if (art2 != -1)
                                {
                                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear3);
                                    TableLayout t = new TableLayout(detail.this);


                                    try {
                                        /*
                                        TextView textView = new TextView(detail.this);
                                        textView.setText(item.getJSONObject(art2).getString("name"));
                                        textView.setGravity(Gravity.CENTER);
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        linearLayout.addView(textView);
                                        */
                                        // Log.e("1",item.getJSONObject(art1).getString("name"));
                                        /*           TableLayout t = (TableLayout) findViewById(R.id.tableArtist);
                                        TableRow tablerow = new TableRow(detail.this);
                                        tablerow.setPadding(0,0,0,20);
                                        TextView textView = new TextView(detail.this);

                                        textView.setText(item.getJSONObject(art2).getString("name"));
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        tablerow.addView(textView);
                                        tablerow.setGravity(Gravity.CENTER);
                                        t.addView(tablerow);*/
                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    try {

                                        TableRow tablerow = new TableRow(detail.this);
                                        tablerow.setPadding(0,0,0,20);
                                        TextView textView = new TextView(detail.this);
                                        textView.setWidth(300);


                                        textView.setText("Name");
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        tablerow.addView(textView);
                                        String temp = item.getJSONObject(art2).getString("name");


                                        TextView textView2 = new TextView(detail.this);
                                        textView2.setWidth(720);
                                        textView2.setText(temp);
                                        tablerow.addView(textView2);
                                        t.addView(tablerow);
                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    try {
                                        TableRow tablerow = new TableRow(detail.this);
                                        tablerow.setPadding(0,0,0,20);
                                        TextView textView = new TextView(detail.this);
                                        textView.setWidth(300);


                                        textView.setText("Followers");
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        tablerow.addView(textView);
                                        DecimalFormat df = new DecimalFormat("#,###");
                                        String temp = df.format(Integer.parseInt(item.getJSONObject(art2).getJSONObject("followers").getString("total")));


                                        TextView textView2 = new TextView(detail.this);
                                        textView2.setWidth(720);
                                        textView2.setText(temp);
                                        tablerow.addView(textView2);
                                        t.addView(tablerow);

                                        Log.e("1",item.getJSONObject(art2).getJSONObject("followers").getString("total"));

                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    try {
                                        TableRow tablerow = new TableRow(detail.this);
                                        tablerow.setPadding(0,0,0,20);
                                        TextView textView = new TextView(detail.this);
                                        textView.setWidth(300);


                                        textView.setText("Popularity");
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        tablerow.addView(textView);

                                        String temp = item.getJSONObject(art2).getString("popularity");


                                        TextView textView2 = new TextView(detail.this);
                                        textView2.setWidth(720);
                                        textView2.setText(temp);
                                        tablerow.addView(textView2);
                                        t.addView(tablerow);


                                        Log.e("1",item.getJSONObject(art2).getString("popularity"));

                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    try {

                                        TableRow tablerow = new TableRow(detail.this);
                                        tablerow.setPadding(0,0,0,20);
                                        TextView textView = new TextView(detail.this);
                                        textView.setWidth(300);


                                        textView.setText("Check At");
                                        textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                        tablerow.addView(textView);

                                        String temp = item.getJSONObject(art2).getJSONObject("external_urls").getString("spotify");
                                        CharSequence charSequence = Html.fromHtml("<a href='"+temp+"'>Spotify</a>");

                                        TextView textView2 = new TextView(detail.this);
                                        textView2.setWidth(720);
                                        textView2.setText(charSequence);
                                        textView2.setMovementMethod(LinkMovementMethod.getInstance());
                                        tablerow.addView(textView2);
                                        t.addView(tablerow);

                                        Log.e("1",item.getJSONObject(art2).getJSONObject("external_urls").getString("spotify"));

                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    linearLayout.addView(t);

                                }
                                finishArtist = true;
                                if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
                                {
                                    ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                                    s.setVisibility(View.VISIBLE);
                                    progressLoading.setVisibility(View.INVISIBLE);
                                    if (noEvent)
                                    {
                                        noRecord.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        noRecord.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(detail.this,"Failed to get results",Toast.LENGTH_LONG).show();
                        progressLoading.setVisibility(View.INVISIBLE);
                        Log.e("11","222");
                    }
                });
                queue.add(stringRequest);
            }
            else
            {
                finishArtist = true;
                if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
                {
                    ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                    s.setVisibility(View.VISIBLE);
                    progressLoading.setVisibility(View.INVISIBLE);
                    if (noEvent)
                    {
                        noRecord.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        noRecord.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
        else
        {
            finishArtist = true;
            if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
            {
                ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                s.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.INVISIBLE);
                if (noEvent)
                {
                    noRecord.setVisibility(View.VISIBLE);
                }
                else
                {
                    noRecord.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
    public void searchPhoto()
    {
        noArtist = false;
        half1 = false;
        half2 = false;

        noRecord.setVisibility(View.INVISIBLE);
        if (artist1.equals("") && artist2.equals(""))
        {
            noArtist = true;
          //  noRecord.setVisibility(View.VISIBLE);
            finishPhoto = true;
            if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
            {
                ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                s.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.INVISIBLE);
                if (noEvent)
                {
                    noRecord.setVisibility(View.VISIBLE);
                }
                else
                {
                    noRecord.setVisibility(View.INVISIBLE);
                }
            }
        }
        final ImageLoader imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
            }

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
        });


        if (!artist1.equals(""))
        {

            String url ="http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/photo?id="+artist1;
            Log.e("11",url);


            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                           // Log.e("goole",response);
                            JSONObject jsonObject = new JSONObject();
                            JSONArray item = new JSONArray();
                            try {
                                    jsonObject = new JSONObject(response);
                                    item = jsonObject.getJSONArray("items");
                            }
                            catch (Exception e)
                            {

                            }
                            try {
                                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear2);
                                Log.e("1","aaaa");
                                if (item.length() == 0)
                                {
                                    half1 = true;
                                }
                                else
                                {
                                 /*   TextView textView = new TextView(detail.this);
                                    textView.setGravity(Gravity.CENTER);
                                    textView.setText(artist1);

                                    textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                    linearLayout.addView(textView);*/
                                }

                                for (int i=0;i<Math.min(item.length(),8);i++)
                                {
                                    //Log.e("pic",item.getJSONObject(i).getString("link"));
                                    ImageView imageView = new ImageView(detail.this);
                                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,R.drawable.common_google_signin_btn_icon_dark_normal_background,R.drawable.common_google_signin_btn_icon_dark_normal_background);
                                    imageLoader.get(item.getJSONObject(i).getString("link"), listener);
                                    imageView.setMaxWidth(1000);
                                    imageView.setAdjustViewBounds(true);

                                    //imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                    linearLayout.addView(imageView);
                                    TextView t = new TextView(detail.this);
                                    t.setText("\n");
                                    linearLayout.addView(t);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e("1","bbbb");
                            }
                            finishPhoto = true;
                            if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
                            {
                                ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                                s.setVisibility(View.VISIBLE);
                                progressLoading.setVisibility(View.INVISIBLE);
                                if (noEvent)
                                {
                                    noRecord.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    noRecord.setVisibility(View.INVISIBLE);
                                }
                            }
                            if (half1 && half2)
                            {
                                noArtist = true;
                            }
                        }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(detail.this,"Failed to get results",Toast.LENGTH_LONG).show();
                    progressLoading.setVisibility(View.INVISIBLE);
                    Log.e("11","222");
                }
            });
            queue.add(stringRequest);
        }
        else
        {
            half1 = true;
            finishPhoto = true;
            if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
            {
                ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                s.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.INVISIBLE);
                if (noEvent)
                {
                    noRecord.setVisibility(View.VISIBLE);
                }
                else
                {
                    noRecord.setVisibility(View.INVISIBLE);
                }
            }
            if (half1 && half2)
            {
                noArtist = true;
            }
        }
        if (!artist2.equals(""))
        {
            String url ="http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/photo?id="+artist2;
            Log.e("11",url);


            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Log.e("goole",response);
                            JSONObject jsonObject = new JSONObject();
                            JSONArray item = new JSONArray();
                            try {
                                jsonObject = new JSONObject(response);
                                item = jsonObject.getJSONArray("items");
                            }
                            catch (Exception e)
                            {

                            }
                            try {
                                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear4);
                                if (item.length() == 0)
                                {
                                    half2 = true;
                                }
                                else
                                {
                                 /*   TextView textView = new TextView(detail.this);
                                    textView.setGravity(Gravity.CENTER);

                                    textView.setText(artist2);
                                    textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                                    linearLayout.addView(textView);*/
                                }

                                for (int i=0;i<Math.min(item.length(),8);i++)
                                {
                                    //Log.e("pic",item.getJSONObject(i).getString("link"));
                                    ImageView imageView = new ImageView(detail.this);
                                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,R.drawable.common_google_signin_btn_icon_dark_normal_background,R.drawable.common_google_signin_btn_icon_dark_normal_background
                                    );
                                    imageLoader.get(item.getJSONObject(i).getString("link"), listener);
                                    imageView.setMaxWidth(1000);
                                    imageView.setAdjustViewBounds(true);

                                    //  imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                    linearLayout.addView(imageView);
                                    TextView t = new TextView(detail.this);
                                    t.setText("\n");
                                    linearLayout.addView(t);
                                }
                            }
                            catch (Exception e)
                            {

                            }
                            finishPhoto = true;
                            if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
                            {
                                ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                                s.setVisibility(View.VISIBLE);
                                progressLoading.setVisibility(View.INVISIBLE);
                                if (noEvent)
                                {
                                    noRecord.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    noRecord.setVisibility(View.INVISIBLE);
                                }
                            }
                            if (half1 && half2)
                            {
                                noArtist = true;
                            }
                        }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(detail.this,"Failed to get results",Toast.LENGTH_LONG).show();
                    progressLoading.setVisibility(View.INVISIBLE);
                    Log.e("11","222");
                }
            });
            queue.add(stringRequest);
        }
        else
        {
            half2 = true;
            finishPhoto = true;
            if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
            {
                ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                s.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.INVISIBLE);
                if (noEvent)
                {
                    noRecord.setVisibility(View.VISIBLE);
                }
                else
                {
                    noRecord.setVisibility(View.INVISIBLE);
                }
            }
            if (half1 && half2)
            {
                noArtist = true;
            }
        }


    }
    public void searchVenue()
    {
        noVenue = false;
        noRecord.setVisibility(View.INVISIBLE);
        String url = "http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/venue?venue="+venuename;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = new JSONObject();
                        JSONObject venue = new JSONObject();
                        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear_venue1) ;
                        TableLayout tableLayout = new TableLayout(detail.this);

                        try {
                            jsonObject = new JSONObject(response);
                            venue = jsonObject.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
                        }
                        catch (Exception e)
                        {
                            noVenue = true;
                        //    noRecord.setVisibility(View.VISIBLE);
                        }
                        try {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);

                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);


                            textView.setText("Name");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);

                            String temp = venue.getString("name");


                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(temp);

                            tablerow.addView(textView2);
                            tableLayout.addView(tablerow);



                        }
                        catch (Exception e)
                        {

                        }
                        try {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);
                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);
                            textView.setText("Address");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);

                            String temp = venue.getJSONObject("address").getString("line1");
                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(temp);

                            tablerow.addView(textView2);
                            tableLayout.addView(tablerow);
                        }
                        catch (Exception e)
                        {

                        }
                        String city = "";
                        try {
                            city += venue.getJSONObject("city").getString("name");
                        }
                        catch (Exception e)
                        {

                        }
                        try {
                            if (!city.equals(""))
                            {
                                city += ", ";
                            }
                            city += venue.getJSONObject("state").getString("name");
                        }
                        catch (Exception e)
                        {

                        }
                        if (!city.equals(""))
                        {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);
                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);
                            textView.setText("City");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);


                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(city);

                            tablerow.addView(textView2);
                            tableLayout.addView(tablerow);
                        }
                        try {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);
                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);
                            textView.setText("Phone Number");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);

                            String temp = venue.getJSONObject("boxOfficeInfo").getString("phoneNumberDetail");
                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(temp);

                            tablerow.addView(textView2);
                            tableLayout.addView(tablerow);
                        }
                        catch (Exception e)
                        {

                        }
                        try {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);

                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);
                            textView.setText("Open Hours");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);

                            String temp = venue.getJSONObject("boxOfficeInfo").getString("openHoursDetail");
                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(temp);

                            tablerow.addView(textView2);
                            tableLayout.addView(tablerow);
                        }
                        catch (Exception e)
                        {

                        }
                        try {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);

                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);
                            textView.setText("General Rule");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);

                            String temp = venue.getJSONObject("generalInfo").getString("generalRule");
                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(temp);

                            tablerow.addView(textView2);
                            tableLayout.addView(tablerow);
                        }
                        catch (Exception e)
                        {

                        }
                        try {
                            TableRow tablerow = new TableRow(detail.this);
                            tablerow.setPadding(0,0,0,20);

                            TextView textView = new TextView(detail.this);
                            textView.setWidth(300);
                            textView.setText("Child Rule");
                            textView.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
                            tablerow.addView(textView);

                            String temp = venue.getJSONObject("generalInfo").getString("childRule");
                            TextView textView2 = new TextView(detail.this);
                            textView2.setWidth(720);
                            textView2.setText(temp);

                            tablerow.addView(textView2);
                            tableLayout.addView(tablerow);
                        }
                        catch (Exception e)
                        {

                        }
                        try {

                            lat = Float.parseFloat(venue.getJSONObject("location").getString("latitude"));
                            lon = Float.parseFloat(venue.getJSONObject("location").getString("longitude"));
                            Log.e("ll",lat+","+lon);
                            MapFragment mMapFragment = MapFragment.newInstance();
                            android.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.add(R.id.linear_venue2, mMapFragment);
                            fragmentTransaction.commit();
                            mMapFragment.getMapAsync(detail.this);
                        }
                        catch (Exception e)
                        {

                        }
                        linearLayout.addView(tableLayout);
                        finishVenue = true;
                        if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
                        {
                            ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                            s.setVisibility(View.VISIBLE);
                            progressLoading.setVisibility(View.INVISIBLE);
                            if (noEvent)
                            {
                                noRecord.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                noRecord.setVisibility(View.INVISIBLE);
                            }
                        }
                    }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(detail.this,"Failed to get results",Toast.LENGTH_LONG).show();
                progressLoading.setVisibility(View.INVISIBLE);
                Log.e("11","222");
            }
        });
        queue.add(stringRequest);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(lat, lon);

        mMap.addMarker(new MarkerOptions().position(sydney));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
    }
    public void searchUpcoming()
    {
        noUpcoming = false;
        noRecord.setVisibility(View.INVISIBLE);
        if (venuename != "")
        {
            String url ="http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/upcoming?venue="+venuename;
            Log.e("11",url);


            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                             Log.e("goole",response);
                            JSONObject jsonObject = new JSONObject();
                            JSONArray event = new JSONArray();
                            try {
                                jsonObject = new JSONObject(response);
                                event = jsonObject.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("event");
                            }
                            catch (Exception e)
                            {
                                noUpcoming = true;
                                Spinner spinner = (Spinner) findViewById(R.id.sortbycategory);
                                spinner.setEnabled(false);
                         //       noRecord.setVisibility(View.VISIBLE);
                            }
                            RecyclerView recyclerView = findViewById(R.id.upcoming);

                            movieList = new ArrayList<>();
                            uri = new ArrayList<>();
                            changeMovie = new ArrayList<>();
                            changeUri = new ArrayList<>();
                            mAdapter = new MoviesAdapter(movieList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);




                            for (int i=0;i<Math.min(5,event.length());i++)
                            {
                                String name="",artist="",time="",type="",website = "";
                                try
                                {
                                    website = event.getJSONObject(i).getString("uri");
                                }
                                catch (Exception e)
                                {

                                }
                                try {
                                    name = event.getJSONObject(i).getString("displayName");
                                }
                                catch (Exception e)
                                {

                                }
                                try {
                                    artist = event.getJSONObject(i).getJSONArray("performance").getJSONObject(0).getString("displayName");
                                }
                                catch (Exception e)
                                {

                                }

                                try {
                                    String string = event.getJSONObject(i).getJSONObject("start").getString("date");
                                    upDate.add(string);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date d = sdf.parse(string,new ParsePosition(0));
                                    Log.e("1",d.toString());
                                    SimpleDateFormat CeshiFmt0=new SimpleDateFormat("MMM dd, yyyy");
                                    time += CeshiFmt0.format(d);
                                }
                                catch (Exception e)
                                {
                                    upDate.add("");
                                }
                                try {
                                    if (!time.equals(""))
                                    {
                                        time += " ";


                                    }
                                    if (!event.getJSONObject(i).getJSONObject("start").getString("time").equals("null"))
                                    {
                                        time += event.getJSONObject(i).getJSONObject("start").getString("time");
                                        upTime.add(event.getJSONObject(i).getJSONObject("start").getString("time"));
                                    }
                                    else
                                    {
                                        upTime.add("");
                                    }
                                }
                                catch (Exception e)
                                {
                                    upTime.add("");
                                }
                                try {
                                    type = "Type: "+ event.getJSONObject(i).getString("type");
                                }
                                catch (Exception e)
                                {

                                }
                                Upcoming upcoming = new Upcoming(name, artist, time,type);
                                upName.add(name);
                                upArtist.add(artist);
                                upType.add(type);
                                movieList.add(upcoming);
                                uri.add(website);
                                changeMovie.add(upcoming);
                                changeUri.add(website);
                                bianUri.add(website);
                            }

                            recyclerView.setAdapter(mAdapter);

                            //changeMovie = new ArrayList<>(movieList);

                            //changeMovie = movieList;
                            //changeUri = uri;
                            Log.e("hei","1");
                            recyclerView.addOnItemTouchListener(
                                    new RecyclerItemClickListener(detail.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override public void onItemClick(View view, int position) {
                                            try {
                                                Intent intent = new Intent();
                                                intent.setData(Uri.parse(uri.get(position)));
                                                intent.setAction(Intent.ACTION_VIEW);
                                                detail.this.startActivity(intent);

                                            }
                                            catch (Exception e)
                                            {

                                            }
                                        }

                                        @Override public void onLongItemClick(View view, int position) {
                                            // do whatever
                                        }
                                    })
                            );
                            finishUpcoming = true;
                            if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
                            {
                                ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                                s.setVisibility(View.VISIBLE);
                                progressLoading.setVisibility(View.INVISIBLE);
                                if (noEvent)
                                {
                                    noRecord.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    noRecord.setVisibility(View.INVISIBLE);
                                }
                            }
                        }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(detail.this,"Failed to get results",Toast.LENGTH_LONG).show();
                    progressLoading.setVisibility(View.INVISIBLE);
                    Log.e("11","222");
                    Spinner spinner = (Spinner) findViewById(R.id.sortbycategory);
                    spinner.setEnabled(false);
                }
            });
            queue.add(stringRequest);
        }
        else
        {
            noUpcoming = true;
            Spinner spinner = (Spinner) findViewById(R.id.sortbycategory);
            spinner.setEnabled(false);
         //   noRecord.setVisibility(View.VISIBLE);
            finishUpcoming = true;
            if (finishEvent && finishArtist && finishPhoto && finishVenue && finishUpcoming)
            {
                ScrollView s = (ScrollView)findViewById(R.id.scrollview_event);
                s.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.INVISIBLE);
                if (noEvent)
                {
                    noRecord.setVisibility(View.VISIBLE);
                }
                else
                {
                    noRecord.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
    public void sort(String category,String order)
    {
        //Log.e("1",category+order);
        if (order == "Ascending")
        {
            if (category == "Event Name")
            {
                sortAscend(upName);
            }
            if (category == "Time")
            {
                sortAscendTime();
            }
            if (category == "Artist")
            {
                sortAscend(upArtist);
            }
            if (category == "Type")
            {
                sortAscend(upType);
            }
        }
        else
        {
            if (category == "Event Name")
            {
                sortDescend(upName);
            }
            if (category == "Time")
            {
                sortDescendTime();
            }
            if (category == "Artist")
            {
                sortDescend(upArtist);
            }
            if (category == "Type")
            {
                sortDescend(upType);
            }
        }
    }
    public void sortAscend(List a)
    {
        uri = new ArrayList<>();
        for (int i=0;i<bianUri.size();i++)
        {
            uri.add(bianUri.get(i));
        }
        for (int i=0;i<a.size()-1;i++)
        {
            for (int j=i+1;j<a.size();j++)
            {
                if (a.get(i).toString().compareTo(a.get(j).toString()) > 0)
                {
                    String t;
                    t = upName.get(i);
                    upName.set(i,upName.get(j));
                    upName.set(j,t);
                    t = upArtist.get(i);
                    upArtist.set(i,upArtist.get(j));
                    upArtist.set(j,t);
                    t = upDate.get(i);
                    upDate.set(i,upDate.get(j));
                    upDate.set(j,t);
                    t = upTime.get(i);
                    upTime.set(i,upTime.get(j));
                    upTime.set(j,t);
                    t = upType.get(i);
                    upType.set(i,upType.get(j));
                    upType.set(j,t);
                    t = uri.get(i);
                    uri.set(i,uri.get(j));
                    uri.set(j,t);
                    Upcoming tt = movieList.get(i);
                    movieList.set(i,movieList.get(j));
                    movieList.set(j,tt);
                }
            }
        }
        for (int i=0;i<uri.size();i++)
        {
            Log.e("11",uri.get(i));
        }
        bianUri = new ArrayList<>();
        for (int i=0;i<uri.size();i++)
        {
            bianUri.add(uri.get(i));
        }
    }
    public void sortDescend(List a)
    {
        uri = new ArrayList<>();
        for (int i=0;i<bianUri.size();i++)
        {
            uri.add(bianUri.get(i));
        }
        for (int i=0;i<a.size()-1;i++)
        {
            for (int j=i+1;j<a.size();j++)
            {
                if (a.get(i).toString().compareTo(a.get(j).toString()) < 0)
                {
                    String t;
                    t = upName.get(i);
                    upName.set(i,upName.get(j));
                    upName.set(j,t);
                    t = upArtist.get(i);
                    upArtist.set(i,upArtist.get(j));
                    upArtist.set(j,t);
                    t = upDate.get(i);
                    upDate.set(i,upDate.get(j));
                    upDate.set(j,t);
                    t = upTime.get(i);
                    upTime.set(i,upTime.get(j));
                    upTime.set(j,t);
                    t = upType.get(i);
                    upType.set(i,upType.get(j));
                    upType.set(j,t);
                    t = uri.get(i);
                    uri.set(i,uri.get(j));
                    uri.set(j,t);
                    Upcoming tt = movieList.get(i);
                    movieList.set(i,movieList.get(j));
                    movieList.set(j,tt);
                }
            }
        }
        for (int i=0;i<uri.size();i++)
        {
            Log.e("22",uri.get(i));
        }
        bianUri = new ArrayList<>();
        for (int i=0;i<uri.size();i++)
        {
            bianUri.add(uri.get(i));
        }
    }
    public void sortAscendTime()
    {
        uri = new ArrayList<>();
        for (int i=0;i<bianUri.size();i++)
        {
            uri.add(bianUri.get(i));
        }
        for (int i=0;i<upDate.size()-1;i++)
        {
            for (int j=i+1;j<upDate.size();j++)
            {
                if ((upDate.get(i).compareTo(upDate.get(j)) > 0) || (upDate.get(i).equals(upDate.get(j)) && upTime.get(i).compareTo(upTime.get(j)) > 0))
                {
                    String t;
                    t = upName.get(i);
                    upName.set(i,upName.get(j));
                    upName.set(j,t);
                    t = upArtist.get(i);
                    upArtist.set(i,upArtist.get(j));
                    upArtist.set(j,t);
                    t = upDate.get(i);
                    upDate.set(i,upDate.get(j));
                    upDate.set(j,t);
                    t = upTime.get(i);
                    upTime.set(i,upTime.get(j));
                    upTime.set(j,t);
                    t = upType.get(i);
                    upType.set(i,upType.get(j));
                    upType.set(j,t);
                    t = uri.get(i);
                    uri.set(i,uri.get(j));
                    uri.set(j,t);
                    Upcoming tt = movieList.get(i);
                    movieList.set(i,movieList.get(j));
                    movieList.set(j,tt);
                }
            }
        }
        for (int i=0;i<uri.size();i++)
        {
            Log.e("11",uri.get(i));
        }
        bianUri = new ArrayList<>();
        for (int i=0;i<uri.size();i++)
        {
            bianUri.add(uri.get(i));
        }
    }
    public void sortDescendTime()
    {
        uri = new ArrayList<>();
        for (int i=0;i<bianUri.size();i++)
        {
            uri.add(bianUri.get(i));
        }
        for (int i=0;i<upDate.size()-1;i++)
        {
            for (int j=i+1;j<upDate.size();j++)
            {
                if ((upDate.get(i).compareTo(upDate.get(j)) < 0) || (upDate.get(i).equals(upDate.get(j)) && upTime.get(i).compareTo(upTime.get(j)) < 0))
                {
                    String t;
                    t = upName.get(i);
                    upName.set(i,upName.get(j));
                    upName.set(j,t);
                    t = upArtist.get(i);
                    upArtist.set(i,upArtist.get(j));
                    upArtist.set(j,t);
                    t = upDate.get(i);
                    upDate.set(i,upDate.get(j));
                    upDate.set(j,t);
                    t = upTime.get(i);
                    upTime.set(i,upTime.get(j));
                    upTime.set(j,t);
                    t = upType.get(i);
                    upType.set(i,upType.get(j));
                    upType.set(j,t);
                    t = uri.get(i);
                    uri.set(i,uri.get(j));
                    uri.set(j,t);
                    Upcoming tt = movieList.get(i);
                    movieList.set(i,movieList.get(j));
                    movieList.set(j,tt);
                }
            }
        }
        for (int i=0;i<uri.size();i++)
        {
            Log.e("11",uri.get(i));
        }
        bianUri = new ArrayList<>();
        for (int i=0;i<uri.size();i++)
        {
            bianUri.add(uri.get(i));
        }

    }
    public class TabPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView tv = new TextView(detail.this);
            // tv.setText(mTitleArray[position]);
            tv.setGravity(Gravity.CENTER);
            container.addView(tv, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
            return tv;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }
}


