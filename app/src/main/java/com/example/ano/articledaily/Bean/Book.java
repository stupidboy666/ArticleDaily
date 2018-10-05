package com.example.ano.articledaily.Bean;

import java.io.Serializable;

public class Book implements Serializable {
    public String imgUrl;
    public String title;
    public String author;
    public String bookUrl;
    public Book(String imgUrl,String title,String author,String bookUrl)
    {
        this.author=author;
        this.bookUrl=bookUrl;
        this.imgUrl=imgUrl;
        this.title=title;
    }
}
