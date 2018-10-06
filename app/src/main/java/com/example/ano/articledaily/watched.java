package com.example.ano.articledaily;

import android.os.Bundle;
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

import java.util.List;

public class watched extends AppCompatActivity {

    private List<Artical> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watched);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.articl_recycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ArticleAdapter articleAdapter=new ArticleAdapter(list);
        recyclerView.setAdapter(articleAdapter);

    }

}
