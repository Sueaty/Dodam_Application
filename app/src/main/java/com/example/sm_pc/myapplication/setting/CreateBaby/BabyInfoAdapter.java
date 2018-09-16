package com.example.sm_pc.myapplication.setting.CreateBaby;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sm_pc.myapplication.R;

import java.util.List;

public class BabyInfoAdapter extends ArrayAdapter<Baby>{
    private Activity context;
    private List<Baby> babyList;

    public BabyInfoAdapter(Activity context, List<Baby>babyList){
        super(context, R.layout.baby_list_view, babyList);
        this.context = context;
        this.babyList = babyList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View listView = inflater.inflate(R.layout.baby_list_view, null, true);

        TextView babyName = (TextView) listView.findViewById(R.id.name);
        TextView babyDate = (TextView) listView.findViewById(R.id.date);

        Baby baby = babyList.get(position);
        babyName.setText(baby.getBabyName());//getter!
        babyDate.setText(baby.getBabyDate());//getter!

        return listView;
    }
}
