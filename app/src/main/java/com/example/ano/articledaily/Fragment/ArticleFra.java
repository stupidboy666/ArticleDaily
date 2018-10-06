package com.example.ano.articledaily.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ano.articledaily.Bean.Artical;
import com.example.ano.articledaily.Bean.Book;
import com.example.ano.articledaily.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;


public class ArticleFra extends Fragment {
    private boolean exits;
    private com.example.ano.articledaily.Bean.Artical at = new Artical();
    String url = "http://www.meiriyiwen.com";

    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle saveIntanceState) {
        View view = inflater.inflate(R.layout.article_frag, containter, false);
        final TextView title = (TextView) view.findViewById(R.id.article_title);
        final TextView article = (TextView) view.findViewById(R.id.content);
        final TextView author = (TextView) view.findViewById(R.id.article_author);


        final FloatingActionButton addfab=view.findViewById(R.id.fab1);
        addfab.setEnabled(false);
        if(at.booked){
            addfab.setImageResource(R.drawable.ic_done);
        }else {
            addfab.setImageResource(R.drawable.add);
        }

        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Artical> list=LitePal.findAll(Artical.class);
                for (Artical arti:list)
                {
                    if(arti.title.equals(title.getText()))
                    {
                        if (arti.booked){
                            arti.setToDefault("booked");
                            addfab.setImageResource(R.drawable.add);
                        }else {
                            arti.booked=true;
                            addfab.setImageResource(R.drawable.ic_done);
                        }
                        arti.updateAll("title=?",arti.title);
                    }
                }
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Artical artical=new Artical();
                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            addfab.setImageResource(R.drawable.add);
                            title.setText(artical.getTitle());
                            article.setText(artical.getContent());
                            author.setText(artical.getAuthour()+"\n");
                            addfab.setEnabled(true);
                        }
                    }
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Connection connection = Jsoup.connect("http://www.meiriyiwen.com/random");
                            connection.timeout(10000);
                            Document document = connection.get();

                            Elements read_con = document.select("div#article_show");
                            String title = read_con.first().getElementsByTag("h1").text();
                            artical.title = title;

                            Elements select1 = read_con.select("p.article_author");
                            String author = select1.first().text();
                            artical.authour = author;

                            Elements select = read_con.select("div.article_text>p");
                            StringBuilder stringBuilder = new StringBuilder();
                            for (Element element : select) {
                                stringBuilder.append("  ").append(element.text()).append("\n\n");
                            }
                            artical.content = stringBuilder.toString();
                            List<Artical> list=LitePal.findAll(Artical.class);
                            for(Artical artical:list)
                            {
                                if (artical.title.equals(title)) {exits=true;}
                            }
                            if(!exits) artical.save();
                            exits=false;
                            handler.sendEmptyMessage(1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    title.setText(at.getTitle());
                    article.setText(at.getContent());
                    author.setText(at.getAuthour()+"\n");
                    article.setMovementMethod(ScrollingMovementMethod.getInstance());
                    addfab.setEnabled(true);
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Connection connection = Jsoup.connect(url);
                    connection.timeout(10000);
                    Document document = connection.get();

                    Elements read_con = document.select("div#article_show");
                    String title = read_con.first().getElementsByTag("h1").text();
                    at.title = title;

                    Elements select1 = read_con.select("p.article_author");
                    String author = select1.first().text();
                    at.authour = author;

                    Elements select = read_con.select("div.article_text>p");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Element element : select) {
                        stringBuilder.append("  ").append(element.text()).append("\n\n");
                    }
                    at.content = stringBuilder.toString();
                    List<Artical> list=LitePal.findAll(Artical.class);
                    for(Artical artical:list)
                    {
                        if(artical.title.equals(title)){
                            exits=true;
                            at.booked=artical.booked;
                        }
                    }
                    if(!exits) at.save();
                    exits=false;
                    handler.sendEmptyMessage(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return view;
    }

}


