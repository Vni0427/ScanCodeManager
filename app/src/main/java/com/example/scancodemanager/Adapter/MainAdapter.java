package com.example.scancodemanager.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scancodemanager.R;

import java.util.List;

public class MainAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<Fragment> fragments;
    private List<String> titles;
    private int images[];

    public MainAdapter(FragmentManager fm, Context context, List<Fragment> fragments,List<String> titles,int images[]) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        this.titles = titles;
        this.images = images;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public View getTabView(int position){
        View v = LayoutInflater.from(context).inflate(R.layout.tab_custom,null);
        ImageView imageView = v.findViewById(R.id.iv_tab_icon);
        imageView.setImageResource(images[position]);
        TextView textView = v.findViewById(R.id.tv_tab_title);
        textView.setText(titles.get(position));
        return v;
    }
}
