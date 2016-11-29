package com.labs.svaithin.goalplanner_v0_01;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Siddharth on 27/11/16.
 */

public class MySimpleStringAdapter extends ArrayAdapter<String> {


    public MySimpleStringAdapter (Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
    }
/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        String myString = getItem(position);
        Log.d("arrayadaptor",myString);
        //check your string
        //Change the color as you want
        //((TextView)convertView).setTextColor();
        return convertView;
    }*/
}
