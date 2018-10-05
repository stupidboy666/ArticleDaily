package com.example.ano.articledaily;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CursorTreeAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ano.articledaily.Bean.MusicBean;

import com.example.ano.articledaily.Player.DownloadAsync;
import com.example.ano.articledaily.Player.MusicPlayer;
import com.yhd.hdmediaplayer.MediaPlayerHelper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MusicList extends AppCompatActivity {
    private MusicBean musicBean;
    ImageView imageView;
    TextView name,author;
    FloatingActionButton play;
    MediaPlayer mediaPlayer;
    long downloadID;
    String downloadPath,filename;
    Boolean exits=false;
    String Url;
    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);

        Intent intent=getIntent();
        musicBean=(MusicBean)intent.getSerializableExtra("music");

        initView();
        getPermission();
        initUrl();
        initMediaPlayer();


        //new thread to download
        new Thread(new Runnable() {
            @Override
            public void run() {
                download();
            }
        }).start();


    }


    public void download() {

        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(musicBean.getMusicURL()));
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadPath = Environment.getExternalStorageDirectory().getPath() + "/aticledaily/Assets/";
        filename = musicBean.title + ".mp3";

        File file = new File(downloadPath + filename);
        exits = file.exists();
        if (!exits) {
            req.setDestinationInExternalPublicDir("/aticledaily/Assets/", musicBean.title + ".mp3");
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(req);
        }
    }
    public void getPermission()
    {
        //getting permission
        if (ContextCompat.checkSelfPermission(MusicList.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MusicList.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
        if(ContextCompat.checkSelfPermission(MusicList.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MusicList.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }
    }

    public void initView()
    {
        imageView=(ImageView) findViewById(R.id.image);
        name=(TextView)findViewById(R.id.name);
        author=(TextView)findViewById(R.id.author);
        play=(FloatingActionButton)findViewById(R.id.fab_play);
        play.setImageResource(R.drawable.play);
        play.setEnabled(false);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.pause();
                }else{
                    mediaPlayer.start();
                }
            }
        });


        //init view
        Glide.with(this)
                .load(musicBean.getImgURL())
                .apply(new RequestOptions().fitCenter()).into(imageView);
        name.setText(musicBean.title);
        author.setText(musicBean.author);


        //init the seekbar
       /* Progress=(SeekBar)findViewById(R.id.skbProgress);
        Progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress()*1000);
            }
        });*/
    }

    public void initUrl()
    {
        File file=new File(downloadPath+filename);
        exits=file.exists();
        Url=downloadPath+filename;
    }

    public  void initMediaPlayer()
    {
        mediaPlayer=new MediaPlayer();
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(Url);
            mediaPlayer.prepare();
            Toast.makeText(this,"MediaPlayer is prepareing",Toast.LENGTH_SHORT).show();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
               play.setEnabled(true);
            }
        });
    }


}
