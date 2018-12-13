package com.example.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {
    private List<Upcoming> moviesList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, artist, time, type;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            artist = (TextView) view.findViewById(R.id.artist);
            time = (TextView) view.findViewById(R.id.time);
            type = (TextView) view.findViewById(R.id.type);
        }
    }


    public MoviesAdapter(List<Upcoming> moviesList) {
        this.moviesList = moviesList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Upcoming movie = moviesList.get(position);
        holder.name.setText(movie.getName());
        holder.artist.setText(movie.getArtist());
        holder.time.setText(movie.getTime());
        holder.type.setText(movie.getType());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
