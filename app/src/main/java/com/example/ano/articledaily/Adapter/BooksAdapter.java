package com.example.ano.articledaily.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ano.articledaily.Bean.Book;
import com.example.ano.articledaily.Bookshow;
import com.example.ano.articledaily.R;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    private Context context;
    private List<Book> list;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View booksView;
        CardView cardView;
        ImageView image;
        TextView booksName;
        TextView booksAuthor;

        ViewHolder(View view)
        {
            super(view);
            this.booksView=view;
            this.cardView=(CardView) view;
            this.image=(ImageView)view.findViewById(R.id.books_image);
            this.booksName=(TextView)view.findViewById(R.id.books_name);
            this.booksAuthor=(TextView)view.findViewById(R.id.books_author);
        }

    }

    public BooksAdapter(Context context, List<Book>list)
    {
        this.context=context;
        this.list=list;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(context==null){
            context=parent.getContext();
        }
        View view=LayoutInflater.from(context).inflate(R.layout.book_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.booksView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Book book=list.get(position);
                Intent intent=new Intent(context,Bookshow.class);
                intent.putExtra("Books",book);
                context.startActivity(intent);
            }
        });
        return holder;
    }



    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull BooksAdapter.ViewHolder holder, int position)
    {
        Book book =list.get(position);
        holder.booksName.setText(book.title);
        holder.booksAuthor.setText(book.author);

        Glide.with(context)
                .load(book.imgUrl)
                .apply(new RequestOptions()
                        .fitCenter())
                .into(holder.image);
    }

    public int getItemCount()
    {
        return list.size();
    }

}
