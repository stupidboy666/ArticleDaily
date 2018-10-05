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
import android.widget.Toast;


public class MusicPlayer extends MediaPlayer
{
    public MediaPlayer mediaPlayer;
    private String videoUrl;


    public MusicPlayer(String videoUrl) {
        this.videoUrl = videoUrl;
        this.mediaPlayer=new MediaPlayer();
    }

    public void play()
    {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepareAsync();
        }catch (IOException e)
        {
            e.printStackTrace();
            Log.e("media", "play:media error " );
        }
    }
}
