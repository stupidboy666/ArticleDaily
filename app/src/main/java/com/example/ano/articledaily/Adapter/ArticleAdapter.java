package com.example.ano.articledaily.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ano.articledaily.Bean.Artical;
import com.example.ano.articledaily.R;
import com.example.ano.articledaily.bookedArticle;

import java.io.Serializable;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>{
    private List<Artical> list;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,author;
        View articleView;

        public ViewHolder(View view)
        {
            super(view);
            articleView=view;
            title=(TextView)view.findViewById(R.id.item_title);
            author=(TextView)view.findViewById(R.id.item_author);
        }
    }

    public ArticleAdapter(Context context,List<Artical> list)
    {
        this.context=context;
        this.list=list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.artile_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.articleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Artical artical=list.get(position);
                Intent intent=new Intent(context,bookedArticle.class);
                intent.putExtra("article", (Serializable) artical);
                context.startActivity(intent);
            }
        });
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
