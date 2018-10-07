package com.example.ano.articledaily.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.example.ano.articledaily.MusicList;
import com.example.ano.articledaily.R;

import java.io.IOException;

public class MusicService extends Service {

    private String url;
    private MediaPlayer mediaPlayer;
    boolean isStopThread;

    private NotificationManager notificationManager;
    private Notification notification;

    public void onCreate()
    {
        super.onCreate();
        MusicList.button.setEnabled(false);
        mediaPlayer=new MediaPlayer();

        notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent1=new Intent(this,MusicList.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        notification=new NotificationCompat.Builder(this,"music")
                .setContentTitle("the music is playing ")
                .setContentText(MusicList.musicBean.title)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .build();
        notificationManager.notify(1,notification);
    }


    public class  MyBinder extends Binder{

        public void play()
        {
            mediaPlayer.start();
        }

        public void pause(){
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.pause();
            }
        }

        public void replay()
        {
            mediaPlayer.start();
        }


        public int getProgress(){
            return mediaPlayer.getCurrentPosition()/1000;
        }

        public int getDuration(){
            return mediaPlayer.getDuration()/1000;
        }

        public boolean getStatus(){
            return mediaPlayer.isPlaying();
        }

        public void seekToPosition(int position)
        {
            mediaPlayer.seekTo(position);
        }
    }

    public IBinder onBind(Intent intent)
    {
        isStopThread=false;

        url=MusicList.downloadPath+MusicList.filename;
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);

            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    int duration=mp.getDuration();
                    Message message=Message.obtain();
                    message.what=1;
                    message.arg1=duration;
                    MusicList.button.setEnabled(true);
                }
            });
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        return new MyBinder();
    }

    public boolean onUnbind(Intent intent)
    {
        isStopThread=true;
        if(mediaPlayer.isPlaying())
            mediaPlayer.release();
        mediaPlayer=null;
        notificationManager.cancelAll();
        return  super.onUnbind(intent);
    }
}

