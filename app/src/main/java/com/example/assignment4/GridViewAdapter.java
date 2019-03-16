package com.example.assignment4;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.util.Random;

// I took most of this from my last assignment in Android.
public class GridViewAdapter extends BaseAdapter {
    // Holds the grid.
    private String[][] lst_source;
    // Unique Adapter context.
    private Context m_context;
    // The [i, j] tuple that keeps track of what block was
    // just added. (-1, -1) if none. I use this because I want
    // the most recent block to be colored white for contrast.
    private int[] most_recent = {-1, -1};

    private static final int NUM_ELEMENTS = 64;

    GridViewAdapter(String[][] lst_source, Context m_context) {
        this.lst_source = lst_source;
        this.m_context = m_context;
    }

    // The count is always 16. This is the constant total block number
    // used by the Adapter interface, NOT the number of non-empty blocks.
    // Use available_spaces for the total number of empty blocks.
    @Override
    public int getCount() {
        return NUM_ELEMENTS;
    }

    /* Positioning system:
        1  2  3  4  5  6  7  8
        9  10 11 12 13 14 15 16
        ...
     */
    @Override
    public Object getItem(int position) {
        return lst_source[position/8][position%8];
    }

    // Thanks to the above system, we don't need to change the way
    // that the getItemId function works - just give what you have.
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        final Button button;
        if (convert_view == null) {
            button = new Button(m_context);
            button.setLayoutParams(new GridView.LayoutParams(150,150));
            button.setPadding(0,0,0,0);
            if (((position % 2) == 1) ^ (((position / 8) % 2) == 1)) {
                button.setBackgroundColor(0xFFFFFFFF);
                if (position < 24) {
                    button.setBackground(ContextCompat
                            .getDrawable(this.m_context, R.drawable.blackpiece));
                } else if (position >= 40) {
                    button.setBackground(ContextCompat
                            .getDrawable(this.m_context, R.drawable.redpiece));
                }
            } else {
                button.setBackgroundColor(0xFF000000);
            }
/*
            if ((getItem(position)).equals("rn")) {
                button.setBackground(ContextCompat
                        .getDrawable(this.m_context, R.drawable.redpiece));
            } else if ((getItem(position)).equals("bn")) {
                button.setBackground(ContextCompat
                        .getDrawable(this.m_context, R.drawable.blackpiece));
            }*/
        } else {
            // Not really sure why this is here. I assume it's to reuse a view, but
            // even when I update the lst_source after it's been created, it satisfies
            // the "if" block. Shouldn't it always get here on an update? Ask about this.
            button = (Button) convert_view;
        }

        return button;
    }
}
