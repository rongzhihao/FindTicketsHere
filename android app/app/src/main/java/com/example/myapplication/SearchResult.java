package com.example.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.*;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.ListView;
import java.util.Arrays;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.widget.TextView;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import java.util.*;

public class SearchResult extends AppCompatActivity {

    public static String chooseID = "";

    private LocationManager locationManager;
    private String lat,lon;
    public TextView noResult;
    public LinearLayout progressSearch;
    private String provider;
    @Override
    protected void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        progressSearch = (LinearLayout) findViewById(R.id.progress_search) ;

        progressSearch.setVisibility(View.VISIBLE);
        noResult = (TextView) findViewById(R.id.noResult);
        IntentFilter filter = new IntentFilter("jason.broadcast.action");
        registerReceiver(broadcastReceiver, filter);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        search();



    }
    public void search()
    {
        noResult.setVisibility(View.INVISIBLE);
        final List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        final List<String> event_id=new ArrayList<String >();
        Log.e("1",MainActivity.keyword);
        Log.e("2",MainActivity.category);
        Log.e("3",MainActivity.miles);
        Log.e("4",MainActivity.distance);
        Log.e("5",MainActivity.where);
        Log.e("6",String.valueOf(MainActivity.fromHere));
        final RequestQueue queue = Volley.newRequestQueue(this);

        if (MainActivity.fromHere)
        {

            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};


            getLngAndLat(SearchResult.this);

            //String url ="http://ip-api.com/json";



            Log.e("111",lat);
            Log.e("222",lon);

            String url2 ="http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/here?keyword="+MainActivity.keyword+"&category="+MainActivity.category+"&distance="+MainActivity.distance+"&miles="+MainActivity.miles+"&lat="+lat+"&lon="+lon;


            StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONArray event = new JSONArray();
                            try {

                                JSONObject jsonObject = new JSONObject(response);


                                // Log.e("111",jsonObject.getJSONObject("_embedded").getJSONArray("events").getJSONObject(0).getString("name"));
                                event = jsonObject.getJSONObject("_embedded").getJSONArray("events");
                            }
                            catch (Exception e)
                            {
                                Log.e("1","youcuo");
                                noResult.setVisibility(View.VISIBLE);
                            }
                            if (event.length()==0)
                            {
                                noResult.setVisibility(View.VISIBLE);
                            }
                            List <String> name1 = new ArrayList<>();
                            List <Integer> category1 = new ArrayList<>();
                            List <String> venue1 = new ArrayList<>();
                            List <String> time1 = new ArrayList<>();
                            for (int i = 0; i < event.length(); i++) {
                                String name,category,venue,date,time,datetime,id;
                                //Log.e("111",event.getJSONObject(i).getString("name"));
                                try
                                {
                                    id = event.getJSONObject(i).getString("id");
                                    event_id.add(id);
                                }
                                catch (Exception e)
                                {
                                    id = "";
                                }
                                try {
                                    name = event.getJSONObject(i).getString("name");

                                }
                                catch (Exception e)
                                {
                                    name = "";
                                }
                                try
                                {
                                    category = event.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                                }
                                catch (Exception e)
                                {
                                    category = "";
                                }
                                try
                                {
                                    venue = event.getJSONObject(i).getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");

                                }
                                catch (Exception e)
                                {
                                    venue = "";

                                }
                                try
                                {
                                    date = event.getJSONObject(i).getJSONObject("dates").getJSONObject("start").getString("localDate");
                                }
                                catch (Exception e)
                                {
                                    date = "";
                                }
                                try
                                {
                                    time = event.getJSONObject(i).getJSONObject("dates").getJSONObject("start").getString("localTime");
                                }
                                catch (Exception e)
                                {
                                    time = "";
                                }
                                datetime = date + " " + time;
                                Map<String, Object> map = new HashMap<String, Object>();
                                Log.e("ccc", String.valueOf(category.equals("Music")));
                                Log.e("111", category);
                                if (category.equals("Music")) {

                                    //  map.put("category", R.drawable.music_icon);
                                    category1.add(R.drawable.music_icon);
                                }
                                if (category.equals("Sports")) {
                                    // map.put("category", R.drawable.sport_icon);
                                    category1.add(R.drawable.sport_icon);
                                }
                                if (category.equals("Arts & Theatre")) {
                                    //map.put("category", R.drawable.art_icon);
                                    category1.add(R.drawable.art_icon);
                                }
                                if (category.equals("Film")) {
                                    //  map.put("category", R.drawable.film_icon);
                                    category1.add(R.drawable.film_icon);
                                }
                                if (category.equals("Miscellaneous")) {
                                    // map.put("category", R.drawable.miscellaneous_icon);
                                    category1.add(R.drawable.miscellaneous_icon);
                                }
                                if (category.equals(""))
                                {
                                    category1.add(R.drawable.common_google_signin_btn_icon_light_normal_background);
                                }
                                // map.put("name", name);
                                //  map.put("venue", venue);
                                //  map.put("datetime", datetime);

                                name1.add(name);
                                venue1.add(venue);
                                time1.add(datetime);
                            }
                            MyAdapter adapter = new MyAdapter(event_id,name1,category1,venue1,time1);

                            ListView listView = (ListView) findViewById(R.id.result);
                            listView.setAdapter(adapter);
                            Log.e("id:",event_id.toString());

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Log.e("111",event_id.get(position));
                                    chooseID = event_id.get(position);
                                    MainActivity.chooseID = "";
                                    Intent intent = new Intent(SearchResult.this,detail.class);
                                    startActivity(intent);
                                    //   SearchResult.this.finish();
                                }
                            });
                            progressSearch.setVisibility(View.INVISIBLE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SearchResult.this,"Failed to get results",Toast.LENGTH_LONG).show();
                    Log.e("11","222");
                    progressSearch.setVisibility(View.INVISIBLE);
                }
            });


            queue.add(stringRequest2);









        }

        else
        {
            //http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/else?keyword=1&category=&distance=10&miles=miles&where=new+york
            String url ="http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/else?keyword="+MainActivity.keyword+"&category="+MainActivity.category+"&distance="+MainActivity.distance+"&miles="+MainActivity.miles+"&where="+MainActivity.where;
            Log.e("11",url);

            StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONArray event = new JSONArray();
                            try {

                                JSONObject jsonObject = new JSONObject(response);


                                // Log.e("111",jsonObject.getJSONObject("_embedded").getJSONArray("events").getJSONObject(0).getString("name"));
                                event = jsonObject.getJSONObject("_embedded").getJSONArray("events");
                            }
                            catch (Exception e)
                            {
                                Log.e("1","youcuo");
                                noResult.setVisibility(View.VISIBLE);
                            }
                            if (event.length() == 0)
                            {
                                noResult.setVisibility(View.VISIBLE);
                            }
                            List <String> name1 = new ArrayList<>();
                            List <Integer> category1 = new ArrayList<>();
                            List <String> venue1 = new ArrayList<>();
                            List <String> time1 = new ArrayList<>();
                            for (int i = 0; i < event.length(); i++) {
                                String name,category,venue,date,time,datetime,id;
                                //Log.e("111",event.getJSONObject(i).getString("name"));
                                try
                                {
                                    id = event.getJSONObject(i).getString("id");
                                    event_id.add(id);
                                }
                                catch (Exception e)
                                {
                                    id = "";
                                }
                                try {
                                    name = event.getJSONObject(i).getString("name");
                                }
                                catch (Exception e)
                                {
                                    name = "";
                                }
                                try
                                {
                                    category = event.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                                }
                                catch (Exception e)
                                {
                                    category = "";
                                }
                                try
                                {
                                    venue = event.getJSONObject(i).getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
                                }
                                catch (Exception e)
                                {
                                    venue = "";

                                }
                                try
                                {
                                    date = event.getJSONObject(i).getJSONObject("dates").getJSONObject("start").getString("localDate");
                                }
                                catch (Exception e)
                                {
                                    date = "";
                                }
                                try
                                {
                                    time = event.getJSONObject(i).getJSONObject("dates").getJSONObject("start").getString("localTime");
                                }
                                catch (Exception e)
                                {
                                    time = "";
                                }

                                datetime = date + " " + time;
                                Map<String, Object> map = new HashMap<String, Object>();
                                Log.e("ccc", String.valueOf(category.equals("Music")));
                                Log.e("111", category);
                                if (category.equals("Music")) {

                                    //  map.put("category", R.drawable.music_icon);
                                    category1.add(R.drawable.music_icon);
                                }

                                if (category.equals("Sports")) {
                                        // map.put("category", R.drawable.sport_icon);
                                    category1.add(R.drawable.sport_icon);
                                }

                                if (category.equals("Arts & Theatre")) {
                                    //map.put("category", R.drawable.art_icon);
                                    category1.add(R.drawable.art_icon);
                                }
                                if (category.equals("Film")) {
                                    //  map.put("category", R.drawable.film_icon);
                                    category1.add(R.drawable.film_icon);
                                }
                                if (category.equals("Miscellaneous")) {
                                    // map.put("category", R.drawable.miscellaneous_icon);
                                    category1.add(R.drawable.miscellaneous_icon);
                                }
                                if (category.equals(""))
                                {
                                    category1.add(R.drawable.common_google_signin_btn_icon_light_normal_background);
                                }












                                // map.put("name", name);
                                //  map.put("venue", venue);
                                //  map.put("datetime", datetime);

                                name1.add(name);
                                venue1.add(venue);
                                time1.add(datetime);
                            }
                            MyAdapter adapter = new MyAdapter(event_id,name1,category1,venue1,time1);

                            ListView listView = (ListView) findViewById(R.id.result);
                            listView.setAdapter(adapter);
                            Log.e("id:",event_id.toString());

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Log.e("111",event_id.get(position));
                                    chooseID = event_id.get(position);
                                    MainActivity.chooseID = "";
                                    Intent intent = new Intent(SearchResult.this, detail.class);
                                    startActivity(intent);
                                    // SearchResult.this.finish();
                                }
                            });
                            progressSearch.setVisibility(View.INVISIBLE);
                        }


                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SearchResult.this,"Failed to get results",Toast.LENGTH_LONG).show();
                    Log.e("11","222");
                    progressSearch.setVisibility(View.INVISIBLE);
                }
            });


            queue.add(stringRequest2);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private String getLngAndLat(Context context) {
        double latitude = 0.0;
        double longitude = 0.0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(SearchResult.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            ActivityCompat.requestPermissions(SearchResult.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            try {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  //从gps获取经纬度
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    } else {//当GPS信号弱没获取到位置的时候又从网络获取
                        return getLngAndLatWithNetwork();
                    }
                } else {    //从网络获取经纬度
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("cuo,",e.getMessage());
            }

        }
        lat = String.valueOf(latitude);
        lon = String.valueOf(longitude);
        return longitude + "," + latitude;
    }

    //从网络获取经纬度
    public String getLngAndLatWithNetwork() {
        double latitude = 0.0;
        double longitude = 0.0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(SearchResult.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else

        {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            else
            {
               // Toast.makeText(SearchResult.this,"Failed ",Toast.LENGTH_LONG).show();
                latitude = 34.0266;
                longitude = -118.283;
            }

        }
        lat = String.valueOf(latitude);
        lon = String.valueOf(longitude);
        return longitude + "," + latitude;
    }


    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
        }
    };
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
              Log.e("wolaile",String.valueOf(intent.getExtras().getInt("index")));
            search();
        }
    };


}
