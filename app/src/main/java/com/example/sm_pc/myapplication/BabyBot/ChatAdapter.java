package com.example.sm_pc.myapplication.BabyBot;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sm_pc.myapplication.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int SELF = 100;
    private ArrayList<Message> messageArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        //밑에서 ViewHolder을 만들어 줘서 생긴 것, alt+enter를 해줘서 생성됨...?
        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    public ChatAdapter(ArrayList<Message> messageArrayList) {
        this.messageArrayList=messageArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else {
            // WatBot message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_watson, parent, false);
        }
        return new ViewHolder(itemView);
        //XML디자인 한 부분 적용
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getId().equals("1")) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        message.setMessage(message.getMessage());
        ((ViewHolder) holder).message.setText(message.getMessage());
        //XNL디자인한 부분에 안에 내용 변경? 데이터를 넣어주는 부분
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
        //아이템을 측정하는 카운터,,메시지 배열의 사이즈를 받아서 그만큼 반복
    }
}
