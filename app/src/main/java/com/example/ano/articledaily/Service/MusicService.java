package com.example.ano.articledaily.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;

import com.example.ano.articledaily.MainActivity;
import com.example.ano.articledaily.MusicList;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class MusicService extends Service {

    /*public static String src;
    private MediaPlayer mediaPlayer;
    private MusicBinder musicBinder;

    public void onCreate()
    {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
    }

    public int onStartCommand(Intent intent,int flags,int startId)
    {
        try{
            mediaPlayer.reset();
            src=MusicList.downloadPath+MusicList.filename;
            mediaPlayer.setDataSource(src);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy()
    {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer=null;
    }


    public IBinder onBind(Intent intent)
    {
        return musicBinder;
    }


    public class MusicBinder extends Binder {
        public void play()
        {
            if (!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }

        public void pause(){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }

        public int getProgress(){
            return mediaPlayer.getCurrentPosition()/1000;
        }

        public int getDuration(){
            return mediaPlayer.getDuration()/1000;
        }

        public void seekToPosition(int sec){
            mediaPlayer.seekTo(sec);
        }


    }*/



    private String url;
    private MediaPlayer mediaPlayer;
    boolean isStopThread;

    public void onCreate()
    {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
    }

    public class  MyBinder extends Binder{

        public void play()
        {
            mediaPlayer.start();
            updateSeekBar();
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

        private void updateSeekBar()
        {
            new Thread(){
                public void run(){
                    while (!isStopThread){
                        try{
                            int currentPosition=mediaPlayer.getCurrentPosition();
                            Message message=Message.obtain();
                            message.what=3;
                            message.arg1=currentPosition;
                            MusicList.handler.sendMessage(message);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

        public long getCurrentPosition()
        {
            return 0;
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
                    MusicList.handler.sendMessage(message);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    MusicList.handler.sendEmptyMessage(2);
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

        return  super.onUnbind(intent);
    }
}

