package com.example.ano.articledaily.Bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class MusicBean  implements Serializable
{
    public String author;
    String musicURL;
    public String imgURL;
    public String title;

    public MusicBean( String title, String author, String imgURL, String musicURL) {
        this.title = title;
        this.author = author;
        this.imgURL = imgURL;
        this.musicURL = musicURL;
    }


    public String getAuthor() {
        return author;
    }


    public String getImgURL() {
        return imgURL;
    }

    public String getMusicURL() {
        return musicURL;
    }

    public String getTitle() {
        return title;
    }

}
