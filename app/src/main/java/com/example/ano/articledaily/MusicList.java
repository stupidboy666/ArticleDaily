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


public class MusicList extends AppCompatActivity {
    private MusicBean musicBean;
    ImageView imageView;
    TextView name,author;
    SeekBar Progress;
    FloatingActionButton play;
    FloatingActionButton pause;
    MediaPlayer mediaPlayer;
    long downloadID;
    String downloadPath,filename;
    Boolean exits=false;


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
                .apply(new RequestOptions().fitCenter()).into(imageView);
        name.setText(musicBean.title);
        author.setText(musicBean.author);


        //init the seekbar
        Progress=(SeekBar)findViewById(R.id.skbProgress);

        //getting permission
        if (ContextCompat.checkSelfPermission(MusicList.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MusicList.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
        if(ContextCompat.checkSelfPermission(MusicList.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MusicList.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }

        File file=new File(downloadPath+filename);
        exits=file.exists();

        //new thread to download
        new Thread(new Runnable() {
            @Override
            public void run() {
                download();
            }
        }).start();

        /**
         * 实现播放在线音频时出现问题，没有content provider
         */

        //根据是否缓存过进行播放方式选择
            if (exits) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(downloadPath + filename);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                MusicPlayer musicPlayer=new MusicPlayer(musicBean.getMusicURL(),Progress);
            }

}

    public void download()
    {

        DownloadManager.Request req=new DownloadManager.Request(Uri.parse(musicBean.getMusicURL()));
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadPath=Environment.getExternalStorageDirectory().getPath()+"/aticledaily/Assets/";
        filename=musicBean.title+".mp3";

        File file=new File(downloadPath+filename);
        exits=file.exists();
        if (!exits) {
            req.setDestinationInExternalPublicDir("/aticledaily/Assets/", musicBean.title + ".mp3");
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(req);
        }
    }

}
