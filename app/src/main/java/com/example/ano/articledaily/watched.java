package com.example.ano.articledaily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.ano.articledaily.Adapter.ArticleAdapter;
import com.example.ano.articledaily.Bean.Artical;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class watched extends AppCompatActivity {

    private List<Artical> list;
    List<Artical>bookedlist=new ArrayList<>();
    boolean booked;
    ArticleAdapter articleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watched);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent=getIntent();
        booked=(boolean)intent.getBooleanExtra("booked",false);
        list=LitePal.findAll(Artical.class);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.articl_recycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(booked){
            for (Artical artical:list)
            {
                if(artical.booked){
                    bookedlist.add(artical);
                }
            }
            articleAdapter=new ArticleAdapter(this,bookedlist);
        }
        else {
            articleAdapter=new ArticleAdapter(this,list);
        }

        recyclerView.setAdapter(articleAdapter);

    }

}
