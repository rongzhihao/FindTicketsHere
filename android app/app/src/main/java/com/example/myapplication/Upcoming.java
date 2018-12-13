package com.example.myapplication;

public class Upcoming {
    private String name, artist, time,type;

    public Upcoming() {
    }

    public Upcoming(String name, String artist, String time, String type) {
        this.name = name;
        this.artist = artist;
        this.time = time;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
