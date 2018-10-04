package com.example.ano.articledaily;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ano.articledaily.Bean.MusicBean;

import com.example.ano.articledaily.Player.MusicPlayer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicList extends AppCompatActivity {
    private MusicBean musicBean;
    ImageView imageView;
    TextView name,author;
    SeekBar Progress;
    FloatingActionButton play;
    FloatingActionButton pause;
    MusicPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);
        imageView=(ImageView) findViewById(R.id.image);
        name=(TextView)findViewById(R.id.name);
        author=(TextView)findViewById(R.id.author);
        play=(FloatingActionButton)findViewById(R.id.fab_play);
        pause=(FloatingActionButton)findViewById(R.id.fab_pause);

        //init view
        Intent intent=getIntent();
        musicBean=(MusicBean)intent.getSerializableExtra("music");
        Glide.with(this)
                .load(musicBean.getImgURL())
                .apply(new RequestOptions()
                        .fitCenter()).into(imageView);
        name.setText(musicBean.title);
        author.setText(musicBean.author);


        //init the seekbar
        Progress=(SeekBar)findViewById(R.id.skbProgress);
        Progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int prog;
            @Override

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.prog=progress* player.mediaPlayer.getDuration()/seekBar.getMax();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.mediaPlayer.seekTo(prog);
            }
        });

        player=new MusicPlayer(musicBean.getMusicURL(),Progress);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.play();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();
            }
        });

}
}
