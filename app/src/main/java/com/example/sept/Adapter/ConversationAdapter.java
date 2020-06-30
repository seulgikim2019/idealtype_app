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
import com.example.sept.Class.ConversationList1;
import com.example.sept.R;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {


    private ArrayList<ConversationList1> list;
    Context context;



    //아이템 뷰를 저장하는 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView convers_email;
        TextView convers_roomNum;
        TextView convers_nick;
        TextView convers_content;
        ImageView convers_pic;

        OnItemClickListener listener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.convers_email=itemView.findViewById(R.id.convers_email);
            this.convers_nick=itemView.findViewById(R.id.convers_nick);
            this.convers_content=itemView.findViewById(R.id.convers_content);
            this.convers_pic=itemView.findViewById(R.id.convers_pic);
            this.convers_roomNum=itemView.findViewById(R.id.convers_room);
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



    public ConversationAdapter(ArrayList<ConversationList1> list){
        this.list=list;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        //inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.conversation_list_content, parent, false) ;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //값들을 넣어줌.. 연결성을 지어줌..


        holder.convers_email.setText(list.get(position).getId());
        holder.convers_nick.setText(list.get(position).getNickname());
        holder.convers_content.setText(list.get(position).getContent());
        holder.convers_roomNum.setText(list.get(position).getC_roomNum());

        Glide.with(context).load(list.get(position).getC_img_uri()).circleCrop().into(holder.convers_pic);


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
