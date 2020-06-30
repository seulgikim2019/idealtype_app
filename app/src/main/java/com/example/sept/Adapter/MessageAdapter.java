package com.example.sept.Adapter;

import android.content.Context;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private ArrayList<ConversationList1> list;
    Context context;



    //아이템 뷰를 저장하는 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView message_nick;
        TextView message_content;
        ImageView message_pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message_nick=itemView.findViewById(R.id.message_nick);
            message_content=itemView.findViewById(R.id.message_content);
            message_pic=itemView.findViewById(R.id.message_pic);


        }



    }




    public MessageAdapter(ArrayList<ConversationList1> list){
        this.list=list;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        //inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.message_content, parent, false) ;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //값들을 넣어줌.. 연결성을 지어줌..


        holder.message_nick.setText(list.get(position).getNickname());

        //content다시 만들기
        holder.message_content.setText(list.get(position).getContent());

        Glide.with(context).load(list.get(position).getC_img_uri()).circleCrop().into(holder.message_pic);


    }

    @Override
    public int getItemCount() {
        return (null != list? list.size():0);
    }


}
