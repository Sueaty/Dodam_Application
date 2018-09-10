package com.example.sm_pc.myapplication.setting;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sm_pc.myapplication.R;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private final static String TAG = "DodamApp";
    LayoutInflater inflater;
    List<LoginInfo> datas;
    Context context;

    public ListAdapter(List<LoginInfo> datas, Context context){
        Log.i(TAG, "=================================ListAdapter()");
        this.datas = datas;
        this.context = context;
        this.inflater = (LayoutInflater) context. getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        Log.i(TAG, "=================================getCount()");
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i(TAG, "=================================getItem()");
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.i(TAG, "=================================getItemId()");
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "=================================getView()");
        if(convertView == null){
            convertView = inflater.inflate(R.layout.user_list_item, null);
        }

        TextView textEmail = (TextView)convertView.findViewById(R.id.textEmail);
        TextView textRegisterDate = (TextView)convertView.findViewById(R.id.textRegisterDate);

        LoginInfo info = datas.get(position);
        textEmail.setText(info.email);
        textRegisterDate.setText(info.registerDate);

        return convertView;
    }
}
