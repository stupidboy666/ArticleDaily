package com.example.ano.articledaily.Player;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.ano.articledaily.Bean.MusicBean;

public class DownloadAsync  extends AsyncTask<Void,Integer,Boolean> {
    private Context mContext;

    private MusicBean music;

    public DownloadAsync(Context context, MusicBean bean)
    {
        this.mContext=context;
        this.music=bean;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        download(mContext,music.getMusicURL(),music.getTitle());
        return true;
    }

    protected  void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    public  void download(Context context,String url,String title)
    {
        DownloadManager.Request downloadReq=new DownloadManager.Request(Uri.parse(url));
        downloadReq.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        downloadReq.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        downloadReq.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadReq.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS,title+".mp3");
    }
}
