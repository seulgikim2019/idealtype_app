package com.example.sept.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.sept.Class.CustomContent1;
import com.example.sept.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {


    private ArrayList<CustomContent1> list;
    Context context;

    //아이템 뷰를 저장하는 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView cus_contents_email;
        TextView cus_contents_nickname;
        ImageView cus_contents_pic;
        TextView cus_contents_age_gender;
        TextView cus_contents_region;

        OnItemClickListener listener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cus_contents_email=itemView.findViewById(R.id.cus_contents_email);
            this.cus_contents_nickname=itemView.findViewById(R.id.cus_contents_nickname);
            this.cus_contents_pic=itemView.findViewById(R.id.cus_contents_pic);
            this.cus_contents_age_gender=itemView.findViewById(R.id.cus_contents_age_gender);
            this.cus_contents_region=itemView.findViewById(R.id.cus_contents_region);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("recylerview","click");
                    int position=getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });

        }


        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }


    OnItemClickListener listener;
    public static interface OnItemClickListener {
        public void onItemClick(ViewHolder holder, View view, int position);
    }



    public CustomAdapter(ArrayList<CustomContent1> list){
        this.list=list;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        //inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.custom_list_content, parent, false) ;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //값들을 넣어줌.. 연결성을 지어줌..

        holder.cus_contents_email.setText(list.get(position).getCus_contents_email());
        holder.cus_contents_nickname.setText("닉네임: "+String.valueOf(list.get(position).getCus_contents_nickname()));
        Glide.with(context).load(list.get(position).getCus_contents_pic()).into(holder.cus_contents_pic);
        holder.cus_contents_age_gender.setText("나이(성별): "+list.get(position).getCus_contents_age_gender());
        holder.cus_contents_region.setText("지역: "+list.get(position).getCus_contents_region());

        holder.setOnItemClickListener(listener);




    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return (null != list? list.size():0);
    }


}
