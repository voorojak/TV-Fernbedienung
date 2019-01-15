package com.example.yousefebrahimzadeh.tv_fernbedienung;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChannelList extends ArrayAdapter<String> {

    ChannelList (Context context, String [] kanaele) {
        super(context, R.layout.activity_kanal, kanaele)  ;

    }


    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        LayoutInflater myLayoutInflater = LayoutInflater.from(getContext());
        View customView = myLayoutInflater.inflate(R.layout.activity_kanal, parent, false);

        String singleKnaeleItem = getItem(position);

        

        return customView;
    }

}
