package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<String> id,name,venue,time;
    private List<Integer> category;
    public MyAdapter(List<String> id,List<String> name,List<Integer> category,List<String> venue,List<String> time){
        this.id = id;
        this.name = name;
        this.venue = venue;
        this.time = time;
        this.category = category;
        Log.e("id:",id.toString());
        Log.e("name:",name.toString());
        Log.e("venue:",venue.toString());
        Log.e("time:",time.toString());
        Log.e("category:",category.toString());
    }
    @Override
    public int getCount() {
        return id == null ? 0 : id.size();
    }
    @Override
    public String getItem(int i) {
        return id.get(i);
    }

    public int getPic(int i) {
        return category.get(i);
    }
    public String  getName(int i) {
        return name.get(i);
    }
    public String  getTime(int i) {
        return time.get(i);
    }
    public String  getVenue(int i) {
        return venue.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      //  Log.e("ii:",String.valueOf(i));
        ViewHolder viewHolder = null;
        if(context == null)
            context = viewGroup.getContext();
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_one_item,null);
            viewHolder = new ViewHolder();
          //  viewHolder.mTv = (TextView)view.findViewById(R.id.mTv);
            viewHolder.mBtn = (Button)view.findViewById(R.id.heart);
            view.setTag(viewHolder);
            viewHolder.Pic = (ImageView) view.findViewById(R.id.category_image);
            viewHolder.Name = (TextView) view.findViewById(R.id.event_name);
            viewHolder.Venue = (TextView) view.findViewById(R.id.venue_name);
            viewHolder.Time = (TextView) view.findViewById(R.id.time);
        }
        viewHolder = (ViewHolder)view.getTag();
        //设置tag标记
        Boolean flag = false;
        SharedPreferences s = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);

        int num = s.getInt("num",0);

            for (int j=0;j<num;j++)
            {

                if (this.getItem(i).equals(s.getString("id_"+j,"x")))
                {
                    //menu.getItem(0).setIcon(R.drawable.heart_fill_red);
                    flag = true;
                }
            }
        if (flag) {
            viewHolder.mBtn.setBackgroundResource(R.drawable.heart_fill_red);
        }
        else {
            viewHolder.mBtn.setBackgroundResource(R.drawable.heart_outline_black);
        }
        viewHolder.mBtn.setTag(R.id.heart,i);//添加此代码
        viewHolder.mBtn.setOnClickListener(this);
       viewHolder.Name.setText(this.getName(i));
        viewHolder.Venue.setText(this.getVenue(i));
        viewHolder.Time.setText(this.getTime(i));
        viewHolder.Pic.setBackgroundResource(this.getPic(i));
        return view;
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.heart)
        {
            int b = (int) view.getTag(R.id.heart);
            //Toast.makeText(context,b+"  "+id.get(b),Toast.LENGTH_SHORT).show();
            Log.e("back ",view.getBackground().toString());

            if (view.getBackground().getConstantState().equals( context.getResources().getDrawable(R.drawable.heart_outline_black).getConstantState()))
            {
                view.setBackgroundResource(R.drawable.heart_fill_red);
                SharedPreferences share = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);
                int num= share.getInt("num",0);

                SharedPreferences sharedPreferences = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("event_"+num,this.getName(b));
                editor.putInt("segment_"+num,this.getPic(b));
                editor.putString("venue_"+num,this.getVenue(b));
                editor.putString("time_"+num,this.getTime(b));
                editor.putString("id_"+num,this.getItem(b));
                editor.putInt("num",num+1);
                editor.commit();

                SharedPreferences s = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);
                Log.e("xx:",String.valueOf(s.getInt("num",-1))+",");
                num = s.getInt("num",0);
                Toast.makeText(context,this.getName(b)+" was added to favorites",Toast.LENGTH_LONG).show();

                Intent intent = new Intent("jason.broadcast.action");

                intent.putExtra("index", num);
                context.sendBroadcast(intent);
            }
            else
            {
                view.setBackgroundResource(R.drawable.heart_outline_black);
                SharedPreferences s = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);

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
                    tempSegment.add(s.getInt("segment_"+i,R.drawable.common_google_signin_btn_icon_light_normal_background));
                    tempVenue.add(s.getString("venue_"+i,"x"));
                    tempTime.add(s.getString("time_"+i,"x"));
                }
                SharedPreferences.Editor editor = s.edit();

                for (int i=0;i<tempID.size();i++)
                {
                    Log.e("bi",tempID.get(i)+"     "+this.getItem(b));
                    if (tempID.get(i).equals(this.getItem(b)))
                    {
                        Toast.makeText(context,this.getName(b)+" was removed from favorites",Toast.LENGTH_LONG).show();
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
                            editor.putInt("segment_"+j,tempSegment.get(j));
                            editor.putString("venue_"+j,tempVenue.get(j));
                            editor.putString("time_"+j,tempTime.get(j));
                            editor.putString("id_"+j,tempID.get(j));
                        }
                        editor.commit();
                        Intent intent = new Intent("jason.broadcast.action");

                        intent.putExtra("index", i);
                        context.sendBroadcast(intent);
                        break;
                    }
                }
                num = s.getInt("num",0);
                for (int i=0;i<num;i++)
                {
                    Log.e("xx:",s.getString("event_"+i,"x"));
                    //Log.e("xx:",String.valueOf(s.getInt("segment_"+i,0)));
                    Log.e("xx:",s.getString("venue_"+i,"x"));
                    Log.e("xx:",s.getString("time_"+i,"x"));
                    Log.e("xx:",s.getString("id_"+i,"x"));
                }
            }
            }

        }


    static class ViewHolder{
        //TextView mTv;
        ImageView Pic;
        Button mBtn;
        TextView Name,Venue,Time;
    }

    }


