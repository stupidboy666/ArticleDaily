package com.example.ano.articledaily;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ano.articledaily.Bean.Artical;

public class bookedArticle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookedarticle);

        Intent intent=getIntent();
        Artical artical=(Artical) intent.getSerializableExtra("article");

        TextView title=(TextView)findViewById(R.id.booked_title);
        TextView author=(TextView)findViewById(R.id.booked_author);
        TextView content=(TextView)findViewById(R.id.booked_content);

        title.setText(artical.getTitle());
        content.setText(artical.getContent());
        author.setText(artical.getAuthour()+"\n");
    }
}
