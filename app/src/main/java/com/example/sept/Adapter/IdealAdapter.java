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
import com.example.sept.Class.IdealContent1;
import com.example.sept.R;

import java.util.ArrayList;

import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.YELLOW;
import static com.example.sept.IdealPic2Activity.b_holder_2;
import static com.example.sept.IdealPicActivity.b_holder;

public class IdealAdapter extends RecyclerView.Adapter<IdealAdapter.ViewHolder> {


    private ArrayList<IdealContent1> list;
    Context context;


    //아이템 뷰를 저장하는 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView ideal_num_show;
        public TextView ideal_name_show;
        public ImageView ideal_pic_show;

        OnItemClickListener listener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ideal_num_show=itemView.findViewById(R.id.ideal_num_show);
            this.ideal_name_show=itemView.findViewById(R.id.ideal_name_show);
            this.ideal_pic_show=itemView.findViewById(R.id.ideal_pic_show);


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



    public IdealAdapter(ArrayList<IdealContent1> list){
        this.list=list;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        //inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.ideal_pic_content, parent, false) ;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //값들을 넣어줌.. 연결성을 지어줌..

        if (list.get(position).getIdeal_num().equals("100")){
            Log.i("hello",list.get(position).getIdeal_contents_name());
            holder.ideal_num_show.setText(list.get(position).getIdeal_num());
            holder.ideal_name_show.setText(list.get(position).getIdeal_contents_name());
            Glide.with(context).load(list.get(position).getIdeal_contents_pic()).
                    circleCrop().into(holder.ideal_pic_show);
            holder.ideal_pic_show.setBackgroundColor(RED);
            b_holder=holder;
            holder.setOnItemClickListener(listener);
        }else if(list.get(position).getIdeal_num().equals("1000")){
            Log.i("hello1",list.get(position).getIdeal_contents_name());
            holder.ideal_num_show.setText(list.get(position).getIdeal_num());
            holder.ideal_name_show.setText(list.get(position).getIdeal_contents_name());
            Glide.with(context).load(list.get(position).getIdeal_contents_pic()).
                    circleCrop().into(holder.ideal_pic_show);
            holder.ideal_pic_show.setBackgroundColor(RED);
            b_holder=holder;
        }else if(list.get(position).getIdeal_num().equals("10000")){
            Log.i("hello2",list.get(position).getIdeal_contents_name());
            holder.ideal_num_show.setText(list.get(position).getIdeal_num());
            holder.ideal_name_show.setText(list.get(position).getIdeal_contents_name());
            Glide.with(context).load(list.get(position).getIdeal_contents_pic()).
                    circleCrop().into(holder.ideal_pic_show);
            holder.ideal_pic_show.setBackgroundColor(YELLOW);
            b_holder_2=holder;
        }else if(list.get(position).getIdeal_num().equals("100000")){
            Log.i("hello3",list.get(position).getIdeal_contents_name());
            holder.ideal_num_show.setText(list.get(position).getIdeal_num());
            holder.ideal_name_show.setText(list.get(position).getIdeal_contents_name());
            Glide.with(context).load(list.get(position).getIdeal_contents_pic()).
                    circleCrop().into(holder.ideal_pic_show);
            holder.ideal_pic_show.setBackgroundColor(YELLOW);
            b_holder_2=holder;
            holder.setOnItemClickListener(listener);
        }else{
            Log.i("hello4",list.get(position).getIdeal_contents_name());
            holder.ideal_num_show.setText(list.get(position).getIdeal_num());
            holder.ideal_name_show.setText(list.get(position).getIdeal_contents_name());
            Glide.with(context).load(list.get(position).getIdeal_contents_pic()).
                    circleCrop().into(holder.ideal_pic_show);
            holder.ideal_pic_show.setBackgroundColor(WHITE);
            holder.setOnItemClickListener(listener);
        }


    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return (null != list? list.size():0);
    }


}
