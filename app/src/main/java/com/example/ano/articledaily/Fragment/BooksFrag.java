package com.example.ano.articledaily.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ano.articledaily.Adapter.BooksAdapter;
import com.example.ano.articledaily.Adapter.MusicAdapter;
import com.example.ano.articledaily.Bean.Book;
import com.example.ano.articledaily.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BooksFrag extends Fragment {
    private List<Book> list=new ArrayList<Book>();
    private String Url="http://book.meiriyiwen.com";
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.book_list,container,false);
        final RecyclerView recyclerView=view.findViewById(R.id.book_list);

        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==3)
                {
                    Context context=getActivity();
                    GridLayoutManager layoutManager=new GridLayoutManager(context,2);
                    recyclerView.setLayoutManager(layoutManager);
                    BooksAdapter adapter=new BooksAdapter(context,list);
                    recyclerView.setAdapter(adapter);
                }
            }
        };


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = Jsoup.connect(Url);
                    Document document = connection.get();
                    Elements elements=document.body().getElementsByTag("li");
                    for(Element element:elements)
                    {
                        String bookUrl=element.select(".book-bg").attr("abs:href");
                        String imgUrl=element.select(".book-bg").select("img").attr("abs:src");
                        String title=element.select(".book-bg").attr("title");
                        String author=element.select("div.book-author").text();

                        Book book=new Book(imgUrl,title,author,bookUrl);
                        list.add(book);
                    }
                    handler.sendEmptyMessage(3);
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
        return  view;
    }

}
