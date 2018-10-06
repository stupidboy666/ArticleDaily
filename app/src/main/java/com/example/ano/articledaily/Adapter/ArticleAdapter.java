package com.example.ano.articledaily.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ano.articledaily.Bean.Artical;
import com.example.ano.articledaily.R;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>{
    private List<Artical> list;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,author;

        public ViewHolder(View view)
        {
            super(view);
            title=(TextView)view.findViewById(R.id.item_title);
            author=(TextView)view.findViewById(R.id.item_author);
        }
    }

    public ArticleAdapter(List<Artical> list)
    {
        this.list=list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.artile_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    public void onBindViewHolder(ViewHolder holder,int position)
    {
        Artical at=list.get(position);
        holder.title.setText(at.title);
        holder.author.setText(at.authour);
    }

    public int getItemCount()
    {
        return list.size();
    }
}
