package com.example.ano.articledaily.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.ano.articledaily.Bean.MusicBean;
import com.example.ano.articledaily.R;


import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private Context context;
    private List<MusicBean> list;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView image;
        TextView musicName;
        TextView musicAuthor;

        ViewHolder(View view)
        {
            super(view);
            cardView=(CardView) view;
            image=(ImageView)view.findViewById(R.id.music_image);
            musicName=(TextView)view.findViewById(R.id.music_name);
            musicAuthor=(TextView)view.findViewById(R.id.music_author);
        }

    }

    public MusicAdapter(Context context,List<MusicBean> music)
    {
        this.context=context;
        this.list=music;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(context==null){
            context=parent.getContext();
        }
        View view=LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        MusicBean music =list.get(position);
        holder.musicName.setText(music.title);
        holder.musicAuthor.setText(music.author);

        Glide.with(context)
                .load(music.getImgURL())
                .apply(new RequestOptions()
                        .fitCenter())
                .into(holder.image);
    }

    public int getItemCount()
    {
        return list.size();
    }
}
