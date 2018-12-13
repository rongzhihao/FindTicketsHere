package com.example.myapplication;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Debug;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.text.TextUtils;
import android.widget.Toast;
import android.content.pm.PackageManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.MapFragment;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String[] zhonglei={"All","Music","Sports","Arts & Theatre","Film","Miscellaneous"};
    private static final String[] yingli = {"Miles","Kilometers"};
    private String[] tabArray = {"search","favorite"};
    public static String chooseID = "";
    public ListView listView;
    private RequestQueue queue;
    public TextView noFavorite;
    public boolean nofav = true;
    private LocationManager locationManager;
    private Location location;
    private String provider;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    public static String keyword,category,distance,miles,where;
    public static boolean fromHere;
    private AutoCompleteTextView autoCompleteTextView;
    private AutoSuggestAdapter autoSuggestAdapter;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.keyword);
        autoSuggestAdapter = new AutoSuggestAdapter(this,android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        queue = Volley.newRequestQueue(this);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // selectedText.setText(autoSuggestAdapter.getObject(position));
                    }
                });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(100);
                handler.sendEmptyMessageDelayed(100,
                        300);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 100) {
                    if (!TextUtils.isEmpty( autoCompleteTextView.getText())) {
                        Log.e("hou","youle");
                        // makeApiCall(autoCompleteTextView.getText().toString());


                        String url = "http://cs571hw8-env.3z4cx2xfbs.us-east-2.elasticbeanstalk.com/autocomplete?keyword="+autoCompleteTextView.getText();
                        Log.e("url: ",url);
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        List <String> list = new ArrayList<>();
                                        JSONObject jsonObject = new JSONObject();
                                        JSONArray attractions = new JSONArray();
                                        try {
                                            jsonObject = new JSONObject(response);
                                            attractions = jsonObject.getJSONObject("_embedded").getJSONArray("attractions");
                                        }
                                        catch (Exception e)
                                        {

                                        }
                                        try {
                                            for (int i=0;i<Math.min(5,attractions.length());i++)
                                            {
                                                list.add(attractions.getJSONObject(i).getString("name"));
                                            }
                                        }
                                        catch (Exception e)
                                        {

                                        }

                                        autoSuggestAdapter.setData(list);
                                        autoSuggestAdapter.notifyDataSetChanged();
                                        }
                                    }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.e("11","222");
                            }
                        });
                        queue.add(stringRequest);




                    }
                }
                return false;
            }
        });
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        final ListView scrollView = (ListView) findViewById(R.id.favorite);




        noFavorite = (TextView) findViewById(R.id.noFavorite);
        IntentFilter filter = new IntentFilter("jason.broadcast.action");
        registerReceiver(broadcastReceiver, filter);
        Button clear = (Button)findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.wrongkeyword);
                textView.setVisibility(View.INVISIBLE);
                textView = (TextView) findViewById(R.id.keyword);
                textView.setText("");
                Spinner spinner = (Spinner) findViewById(R.id.category);
                spinner.setSelection(0);
                textView = (TextView) findViewById(R.id.distance);
                textView.setText("");
                spinner = (Spinner) findViewById(R.id.miles);
                spinner.setSelection(0);
                RadioButton radioButton = (RadioButton) findViewById(R.id.here);
                radioButton.setChecked(true);
                textView = (TextView) findViewById(R.id.wronglocation);
                textView.setVisibility(View.INVISIBLE);
                textView = (TextView) findViewById(R.id.location);
                textView.setText("");
                textView.setEnabled(false);
            }
        });
        ListView listView = (ListView) findViewById(R.id.favorite);
        listView.setVisibility(View.INVISIBLE);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.seafav);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter();
        viewPager.setAdapter(tabPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0)
                {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.searchList);
                    linearLayout.setVisibility(View.VISIBLE);
                    ListView listView = (ListView) findViewById(R.id.favorite);
                    TranslateAnimation alphaAnimation=new TranslateAnimation(-800,0,0,0);
                    alphaAnimation.setDuration(50);
                    linearLayout.setAnimation(alphaAnimation);
                    listView.setVisibility(View.INVISIBLE);
                    noFavorite.setVisibility(View.INVISIBLE);
                    Log.e("ff","zenmele");
                }
                if (position == 1)
                {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.searchList);
                    linearLayout.setVisibility(View.INVISIBLE);
                    ListView listView = (ListView) findViewById(R.id.favorite);

                    TranslateAnimation alphaAnimation=new TranslateAnimation(800,0,0,0);
                    alphaAnimation.setDuration(50);

                    listView.setVisibility(View.VISIBLE);

                    if (nofav)
                    {
                        noFavorite.setVisibility(View.VISIBLE);
                        noFavorite.setAnimation(alphaAnimation);
                    }
                    else
                    {
                        listView.setAnimation(alphaAnimation);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
      //  SharedPreferences sharedPreferences = getSharedPreferences("Favorite", Context.MODE_PRIVATE);

      //  SharedPreferences.Editor editor = sharedPreferences.edit();
      //  editor.clear();
      //  editor.commit();
      //  editor.clear();
       // editor.putString("1","rzh");
      //  editor.commit();

        searchFavorite();
        noFavorite.setVisibility(View.INVISIBLE);

        spinner = (Spinner) findViewById(R.id.category);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,zhonglei);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
        spinner = (Spinner) findViewById(R.id.miles);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,yingli);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
        Button bt1_2;
        bt1_2 = (Button) findViewById(R.id.search);
        bt1_2.setOnClickListener(listener);

    }
   // private Intent intent;
    private boolean isNull(String str)
    {
        return str.trim().isEmpty();
    }
    Button.OnClickListener listener = new Button.OnClickListener()
    {
        public void onClick(View v)
        {
            TextView t = (TextView)findViewById(R.id.keyword);

            boolean f = true;
            if (isNull(t.getText().toString()))
            {
                TextView tv = (TextView)findViewById(R.id.wrongkeyword);
                tv.setVisibility(View.VISIBLE);
                f = false;
            }
            else
            {
                TextView tv = (TextView)findViewById(R.id.wrongkeyword);
                tv.setVisibility(View.INVISIBLE);
            }
            RadioButton r = (RadioButton)findViewById(R.id.elseplace);
            if (r.isChecked())
            {
                fromHere = false;
                TextView tt = (TextView)findViewById(R.id.location);
                if (isNull(tt.getText().toString()))
                {
                    TextView tv = (TextView)findViewById(R.id.wronglocation);
                    tv.setVisibility(View.VISIBLE);
                    f = false;
                }
                else
                {
                    TextView tv = (TextView)findViewById(R.id.wronglocation);
                    tv.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                fromHere = true;
                TextView tv = (TextView)findViewById(R.id.wronglocation);
                tv.setVisibility(View.INVISIBLE);
            }
            if (f) {
                //TextView ProvinceTxt = (TextView) sp_privince.getSelectedView().findViewById(R.id.category); // 得到选中的选项Id
                //String codeString = ProvinceTxt.getText().toString();
                TextView tv = (TextView)findViewById(R.id.keyword);
                keyword = tv.getText().toString();
                spinner = (Spinner) findViewById(R.id.category);
                String temp = spinner.getSelectedItem().toString();

                if (temp == "All")
                    category = "";
                if (temp == "Music")
                    category = "KZFzniwnSyZfZ7v7nJ";
                if (temp == "Sports")
                    category = "KZFzniwnSyZfZ7v7nE";
                if (temp == "Arts & Theatre")
                    category = "KZFzniwnSyZfZ7v7na";
                if (temp == "Film")
                    category = "KZFzniwnSyZfZ7v7nn";
                if (temp == "Miscellaneous")
                    category = "KZFzniwnSyZfZ7v7n1";
                spinner = (Spinner) findViewById(R.id.miles);
                temp = spinner.getSelectedItem().toString();
                if (temp == "Miles") {
                    miles = "miles";
                }
                else
                {
                    miles = "km";
                }
                tv = (TextView)findViewById(R.id.distance);
                if (isNull(tv.getText().toString()))
                {
                    distance = "10";
                }
                else
                {
                    distance = tv.getText().toString();
                }
                tv = (TextView)findViewById(R.id.location);
                where = tv.getText().toString();
                Intent intent = new Intent(MainActivity.this, SearchResult.class);
                startActivity(intent);
               // MainActivity.this.finish();

            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Please fix all fields with errors",Toast.LENGTH_LONG);

                toast.show();
            }

        }
    };
    public void chooseElse(View view)
    {
       TextView t = (TextView)findViewById(R.id.location);
       t.setEnabled(true);

    }
    public void chooseHere(View view)
    {
        TextView t = (TextView)findViewById(R.id.location);
        t.setEnabled(false);

    }
    public void searchFavorite()
    {
        nofav = false;
        noFavorite.setVisibility(View.INVISIBLE);
        SharedPreferences share = getSharedPreferences("Favorite", Context.MODE_PRIVATE);
        int num = share.getInt("num",0);
        if (num == 0)
        {
            nofav = true;
            noFavorite.setVisibility(View.VISIBLE);
        }
       // List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        List <String> name = new ArrayList<>();
        List <Integer> category = new ArrayList<>();
        List <String> venue = new ArrayList<>();
        List <String> time = new ArrayList<>();

        final List<String> event_id = new ArrayList<>();
        for (int i=0;i<num;i++)
        {
         //   Map<String, Object> map = new HashMap<String, Object>();
            /*
            if (share.getString("segment_"+i,"").equals("Music")) {

                //map.put("category", R.drawable.music_icon);
                category.add(R.drawable.music_icon);
            }
            if (share.getString("segment_"+i,"").equals("Sports")) {
               // map.put("category", R.drawable.sport_icon);
                category.add(R.drawable.sport_icon);
            }
            if (share.getString("segment_"+i,"").equals("Arts & Theatre")) {
               // map.put("category", R.drawable.art_icon);
                category.add(R.drawable.art_icon);
            }
            if (share.getString("segment_"+i,"").equals("Film")) {
               // map.put("category", R.drawable.film_icon);
                category.add( R.drawable.film_icon);
            }
            if (share.getString("segment_"+i,"").equals("Miscellaneous")) {
              //  map.put("category", R.drawable.miscellaneous_icon);
                category.add( R.drawable.miscellaneous_icon);
            }*/
            category.add(share.getInt("segment_"+i,R.drawable.common_google_signin_btn_icon_light_normal_background));
            name.add(share.getString("event_"+i,""));
            venue.add(share.getString("venue_"+i,""));
            time.add(share.getString("time_"+i,""));
           // map.put("name", share.getString("event_"+i,""));
           // map.put("venue", share.getString("venue_"+i,""));
           // map.put("datetime", share.getString("time_"+i,""));
            event_id.add(share.getString("id_"+i,""));
           // listitem.add(map);
        }

/*
        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext()
                , listitem
                , R.layout.fragment_one_item
                , new String[]{"name", "category", "venue", "datetime"}
                , new int[]{R.id.event_name, R.id.category_image, R.id.venue_name, R.id.time});
        Log.e("1", adapter.toString());*/
        MyAdapter adapter = new MyAdapter(event_id,name,category,venue,time);
        listView = (ListView) findViewById(R.id.favorite);
        listView.setAdapter(adapter);
        Log.e("id:",event_id.toString());
       // ListView listView1 = (ListView) findViewById(R.id.)

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("111",event_id.get(position));
                chooseID = event_id.get(position);
                SearchResult.chooseID = "";
                Intent intent = new Intent(MainActivity.this,detail.class);
                startActivity(intent);
                //   SearchResult.this.finish();
            }
        });

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
          //  Log.e("wolaile",String.valueOf(intent.getExtras().getInt("index")));
            searchFavorite();
        }
    };
    public class TabPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return tabArray.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView tv = new TextView(MainActivity.this);
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
            return tabArray[position];
        }
    }

}
