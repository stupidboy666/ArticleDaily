package com.example.ano.articledaily;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
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
import android.widget.Button;
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
import com.example.ano.articledaily.Service.MusicService;
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
    MediaPlayer mediaPlayer=new MediaPlayer();
    long downloadID;
    public static String downloadPath,filename;
    Boolean exits;
    SeekBar Progress;
    private boolean isplaying =true;
    private Context context=this;

    private Button button;

    private Handler handler=new Handler();
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            Progress.setProgress(musicBinder.getProgress());
            handler.post(runnable);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);

        Intent intent=getIntent();
        musicBean=(MusicBean)intent.getSerializableExtra("music");

        getPermission();
        initView();

        File file=new File(downloadPath+filename);
        exits=file.exists();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //new thread to download
        new Thread(new Runnable() {
            @Override
            public void run() {
                download();
            }
        }).start();
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
        button=(Button)findViewById(R.id.playorpause);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(isplaying){
                   musicBinder.pause();
                   isplaying=false;
               }else {
                   musicBinder.play();
                   isplaying=true;
               }
            }
        });

        Progress=(SeekBar)findViewById(R.id.skbProgress);

        Glide.with(this)
                .load(musicBean.getImgURL())
                .apply(new RequestOptions().fitCenter()).into(imageView);
        name.setText(musicBean.title);
        author.setText(musicBean.author);
    }

    public void download() {

        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(musicBean.getMusicURL()));
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadPath = Environment.getExternalStorageDirectory().getPath() + "/aticledaily/Assets/";
        filename = musicBean.title + ".mp3";

        File file=new File(downloadPath+filename);
        exits=file.exists();
        if (!exits) {
            req.setDestinationInExternalPublicDir("/aticledaily/Assets/", musicBean.title + ".mp3");
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(req);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try{
                mediaPlayer.setDataSource(musicBean.getMusicURL());
                mediaPlayer.prepare();
                Toast.makeText(context,"正在准备中",Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                e.printStackTrace();
            }
            Toast.makeText(context,"准备完成",Toast.LENGTH_SHORT).show();
            mediaPlayer.start();
        }else {
            Intent intent=new Intent(this,MusicService.class);
            bindService(intent,musicServiceConnection,BIND_AUTO_CREATE);
            if(!(downloadPath+filename).equals(MusicService.src))
            {
                startService(intent);
            }
        }
    }

    private ServiceConnection musicServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder=(MusicService.MusicBinder)service;
            Progress.setMax(musicBinder.getDuration());

            Progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    musicBinder.seekToPosition(Progress.getProgress()*1000);
                }
            });
            handler.post(runnable);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    protected  void onDestroy(){
        super.onDestroy();
        if(musicServiceConnection!=null)
        {
            Intent intent=new Intent(this,MusicService.class);
            stopService(intent);
            unbindService(musicServiceConnection);
        }
        mediaPlayer.stop();
    }

}
