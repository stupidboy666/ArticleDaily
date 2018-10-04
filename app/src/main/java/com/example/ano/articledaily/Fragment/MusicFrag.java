package com.example.ano.articledaily.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.ano.articledaily.Adapter.MusicAdapter;
import com.example.ano.articledaily.Bean.MusicBean;
import com.example.ano.articledaily.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MusicFrag extends Fragment {
    private List<MusicBean> musicBeanList=new ArrayList<MusicBean>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.music_frag,container,false);
        final RecyclerView recyclerView=view.findViewById(R.id.recycler_view);

        final Handler handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==1)
                {
                    Context context=getActivity();
                    GridLayoutManager layoutManager=new GridLayoutManager(context,1);
                    recyclerView.setLayoutManager(layoutManager);
                    MusicAdapter adapter=new MusicAdapter(context,musicBeanList);
                    recyclerView.setAdapter(adapter);
                }
            }
        };


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Document document = Jsoup.connect("http://voice.meiriyiwen.com/").get();
                    Elements elements = document.body().getElementsByClass("list_box");
                    for (Element element : elements) {
                        String title = element.select(".list_author").get(0)
                                .select("a").text();

                        String author = element.select(".author_name").get(0)
                                .text();

                        String imgURL = element.select("a.box_list_img").select("img")
                                .attr("abs:src");

                        String musicURL = element.select(".box_list_img")
                                .attr("abs:href");

                        Connection connection=Jsoup.connect(musicURL);
                        Document doc=connection.get();
                        String swfurl=doc.body().select(".p_file").select("embed").attr("src");
                        Pattern pattern=Pattern.compile("=(.*?)&");
                        Matcher matcher=pattern.matcher(swfurl);
                        if(matcher.find())
                        {
                            musicURL=new String(Base64.decode(matcher.group(1),Base64.DEFAULT));
                        };
                        MusicBean music = new MusicBean(title, author, imgURL, musicURL);
                        musicBeanList.add(music);
                        handler.sendEmptyMessage(1);
                    }

                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
        return view;
    }
}
