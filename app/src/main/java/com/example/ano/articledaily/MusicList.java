package com.example.ano.articledaily;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
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
import android.widget.RemoteViews;
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


public class MusicList extends AppCompatActivity implements View.OnClickListener {
    private MusicBean musicBean;
    public static Notification notification;
    private static NotificationManager notificationManager;
    private static int duration;//音频总长度
    private static int currentPosition;//当前进度
    private static int status;
    ImageView imageView;
    TextView name,author;
    FloatingActionButton play;
    MediaPlayer mediaPlayer=new MediaPlayer();
    long downloadID;
    public static String downloadPath,filename;
    Boolean exits;
    private static SeekBar Progress;
    private boolean isplaying =true;
    private Context context=this;

    public static  MusicService.MyBinder myBinder;//中间人对象
    private  MyConn myConn;
    public static Button button;
    private String PLAYER_TAG;

    public static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    duration=msg.arg1;
                    Progress.setMax(duration);
                    break;
                case 2:
                    status=3;
                    Progress.setProgress(0);
                    break;
                case 3:
                    if(status==3) return;
                    currentPosition=msg.arg2;
                    Progress.setProgress(currentPosition);
                    break;
            }
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
        initNotification();
        File file=new File(downloadPath+filename);
        exits=file.exists();

        PLAYER_TAG=getPackageName();
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

    public class MyConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取中间人对象
            myBinder = (MusicService.MyBinder) service;

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
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

    public void initView() {
        imageView = (ImageView) findViewById(R.id.image);
        name = (TextView) findViewById(R.id.name);
        author = (TextView) findViewById(R.id.author);
        button = (Button) findViewById(R.id.playorpause);
        button.setOnClickListener(this);

        Progress = (SeekBar) findViewById(R.id.skbProgress);
        Progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myBinder.seekToPosition(seekBar.getProgress());
            }
        });

        Glide.with(context)
                .load(musicBean.getImgURL())
                .apply(new RequestOptions()
                        .fitCenter())
                .into(imageView);
    }


    //初始化通知栏
    public void initNotification()
    {
        notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this,"voice");
        notification=new Notification();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel("voice","通知栏",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.notification);
        remoteViews.setTextViewText(R.id.ntfTitle,musicBean.title);
        remoteViews.setTextViewText(R.id.ntfAuthor,musicBean.author);
        remoteViews.setImageViewResource(R.id.playOrPause,R.drawable.pause);
        remoteViews.setImageViewResource(R.id.close,R.drawable.close);

        Intent intentPause=new Intent(PLAYER_TAG);
        intentPause.putExtra("STATUS","pause");
        PendingIntent pIntentPause=PendingIntent.getBroadcast(this,2,intentPause,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.playOrPause,pIntentPause);

        Intent notificationIntent=new Intent(this,MusicList.class);
        PendingIntent intent=PendingIntent.getActivity(this,0,notificationIntent,0);
        mBuilder.setContent(remoteViews)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent);

        notification=mBuilder.build();
        notification.flags= Notification.FLAG_NO_CLEAR;//滑动或点击时不被清除
        notificationManager.notify(PLAYER_TAG,111,notification);
    }

    public  void onClick(View v){
        if(myConn!=null){
            switch (status){
                case 0://初始状态
                    //播放
                    myBinder.play();
                    status=2;
                    button.setText("暂停");
                    break;

                case 1://暂停
                    //继续播放
                    myBinder.replay();
                    status=2;
                    button.setText("暂停");
                    break;

                case 2://播放中
                    //暂停
                    myBinder.pause();
                    status=1;
                    button.setText("继续播放");

                    break;

                case 3://播放完成
                    //重新开始
                    myBinder.replay();
                    status=2;
                    button.setText("暂停");
                    break;
            }
        }else {
            switch (status) {
                case 0://初始状态
                    //播
                    mediaPlayer.start();
                    status = 2;
                    button.setText("暂停");
                    break;

                case 1://暂停
                    //继续播放
                    mediaPlayer.start();
                    status = 2;
                    button.setText("暂停");
                    break;

                case 2://播放中
                    //暂停
                    mediaPlayer.pause();
                    status = 1;
                    button.setText("继续播放");

                    break;

                case 3://播放完成
                    //重新开始
                    mediaPlayer.start();
                    status = 2;
                    button.setText("暂停");
                    break;
            }
        }
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
                button.setEnabled(false);
                Toast.makeText(context,"音频正在准备中",Toast.LENGTH_LONG).show();
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        button.setEnabled(true);
                    }
                });

            }catch (IOException e){
                e.printStackTrace();
            }

        }else {
            myConn=new MyConn();
            Intent intent=new Intent(this,MusicService.class);
            bindService(intent,myConn,BIND_AUTO_CREATE);
                startService(intent);
        }
    }


    protected  void onDestroy(){
        super.onDestroy();
        unbindService(myConn);
        myConn=null;
        if(mediaPlayer!=null)
        {
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

}
