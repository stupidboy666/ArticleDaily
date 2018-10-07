package com.example.ano.articledaily;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MusicList extends AppCompatActivity implements View.OnClickListener {
    public static MusicBean musicBean;
    private DownloadManager downloadManager;
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
    public static String downloadPath,filename,downloadPath2,filename2;
    Boolean exits;
    private static SeekBar Progress;
    private boolean isplaying =true;
    private Context context=this;

    public static  MusicService.MyBinder myBinder;//中间人对象
    private  MyConn myConn;
    public static Button button;
    private String PLAYER_TAG;
    private Timer timer=new Timer();


    private TimerTask timertask = new TimerTask() {
        @Override
        public void run() {
            try {
                    handler.sendEmptyMessage(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Handler handler=new Handler()
   {
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what) {
               case 1:
                   try {
                       if(myBinder!=null&&myConn!=null){
                           Progress.setProgress(myBinder.getProgress());
                       }
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                   //handler.sendEmptyMessageDelayed(UPDATE);
                   handler.sendEmptyMessageDelayed(1,500);
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
            Progress.setMax(myBinder.getDuration());
            timer.schedule(timertask,0,500);

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
        name.setText(musicBean.title);
        author.setText(musicBean.author);

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




    public  void onClick(View v){
        if(myConn==null){
          Toast.makeText(this,"Downloading,please wait a moment",Toast.LENGTH_SHORT).show();
          checkStatus();
        }else {
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
        }

    }

    public void download() {

        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(musicBean.getMusicURL()));
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager.Request req2 = new DownloadManager.Request(Uri.parse(musicBean.getImgURL()));
        req2.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadPath = Environment.getExternalStorageDirectory().getPath() + "/aticledaily/Assets/";
        downloadPath2=Environment.getDownloadCacheDirectory().getPath()+"/aticledaily/Music/";
        filename = musicBean.title + ".mp3";
        filename2=musicBean.title+".png";

        File file=new File(downloadPath+filename);
        exits=file.exists();
        if (!exits) {
            req.setDestinationInExternalPublicDir("/aticledaily/Assets/", musicBean.title + ".mp3");
            req2.setDestinationInExternalPublicDir("/aticledaily/Musics/",musicBean.title+".png");
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(req);
            downloadManager.enqueue(req2);
        }else {
            myConn=new MyConn();
            Intent intent=new Intent(this,MusicService.class);
            bindService(intent,myConn,BIND_AUTO_CREATE);
        }
    }

    private void checkStatus()
    {
        File file=new File(downloadPath+filename);
        exits=file.exists();
        if(exits)
        {
            myConn=new MyConn();
            Intent intent=new Intent(this,MusicService.class);
            bindService(intent,myConn,BIND_AUTO_CREATE);
            musicBean.imgURL=filename2;
            musicBean.musicURL=filename;
            musicBean.save();
        }
    }


    protected  void onDestroy(){
        super.onDestroy();
        if(myConn!=null){
            unbindService(myConn);
            myConn=null;
        }
        if (timer!=null){
            timer.cancel();
            timer.purge();
            timer=null;
        }

        if(mediaPlayer!=null)
        {
            mediaPlayer.release();
            mediaPlayer=null;
        }

    }

}
