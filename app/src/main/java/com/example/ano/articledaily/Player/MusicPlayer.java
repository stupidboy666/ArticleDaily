package com.example.ano.articledaily.Player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;


public class MusicPlayer implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener
{
    public MediaPlayer mediaPlayer;
    private SeekBar progress;
    private Timer timer=new Timer();
    private String videoUrl;
    private boolean pause;
    private int playPosition;

    public MusicPlayer(String videoUrl, SeekBar skbProgress) {
        this.progress = skbProgress;
        this.videoUrl = videoUrl;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }

        timer.schedule(mTimerTask, 0, 1000);
    }

    TimerTask mTimerTask=new TimerTask() {
        @Override
        public void run() {
            if(mediaPlayer.isPlaying()&&progress.isPressed()==false)
            {
                handler.sendEmptyMessage(1);
            }
        }
    };

    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1) {
                int position = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if(duration>0)
                {
                    long pos=progress.getMax()*position/duration;
                    progress.setProgress((int)pos);
                }
            }
        }
    };


    public void play()
    {
        playfrom(0);
    }

    public boolean pause()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            pause=true;
        }
        else {
            mediaPlayer.start();
            pause=false;
        }
        return pause;
    }

    public void onBufferingUpdate(MediaPlayer med,int bufferingProgress)
    {
        progress.setSecondaryProgress(bufferingProgress);
        int current=progress.getMax()*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();
    }

    public void onCompletion(MediaPlayer arg0)
    {

    }

    public void playfrom(final int playPosition)
    {
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    if (playPosition>0)
                    {
                        mediaPlayer.seekTo(playPosition);
                    }
                }
            });
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
