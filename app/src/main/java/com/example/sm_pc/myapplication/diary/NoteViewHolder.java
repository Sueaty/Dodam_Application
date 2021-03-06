package com.example.sm_pc.myapplication.diary;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sm_pc.myapplication.R;

public class NoteViewHolder extends RecyclerView.ViewHolder{

    View mView;

    TextView textTitle, textTime, textContent;
    CardView noteCard;

    public NoteViewHolder(View itemView) {
        super(itemView);

        mView=itemView;

        textTitle = mView.findViewById(R.id.note_title);
        textContent = mView.findViewById(R.id.note_content);
        textTime = mView.findViewById(R.id.note_time);
        noteCard = mView.findViewById(R.id.note_card);
    }

    public void setNoteTitle(String title){
        textTitle.setText(title);
    }
    public void setNoteContent(String content){
        textContent.setText(content);
    }
    public void setNoteTime(String time){
        textTime.setText(time);
    }
}
