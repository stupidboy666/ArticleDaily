package com.example.ano.articledaily.Bean;


import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.lang.invoke.VolatileCallSite;
import java.util.List;

public class Artical extends LitePalSupport{
    public String title;
    public String authour;
    public String content;
    public boolean booked=false;

    public Artical(){
    }

    public Artical(String title,String authour,String content)
    {
        this.authour=authour;
        this.content=content;
        this.title=title;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthour() {
        return authour;
    }

    public void setAuthour(String authour) {
        this.authour = authour;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
