package com.example.scancodemanager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.scancodemanager.Adapter.MainAdapter;
import com.example.scancodemanager.Fragment.ActionFragment;
import com.example.scancodemanager.Fragment.TableFragment;
import com.example.scancodemanager.Fragment.UserFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager vpMain;
    private TabLayout tabMain;
    private MainAdapter mainAdapter;
    private List<Fragment> fragments;
    private List<String> titles = new ArrayList<>();
    private int images[] = {R.drawable.action_selector,R.drawable.table_selector,R.drawable.user_selector};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabMain = findViewById(R.id.tab_main);
        vpMain = findViewById(R.id.vp_main);

        fragments = new ArrayList<>();
        fragments.add(new ActionFragment());
        fragments.add(new TableFragment());
        fragments.add(new UserFragment());
        titles.add("Action");
        titles.add("Table");
        titles.add("User");

        mainAdapter = new MainAdapter(getSupportFragmentManager(),this, fragments,titles,images);
        vpMain.setAdapter(mainAdapter);
        vpMain.setOffscreenPageLimit(3);
        //该方法会清除tab
        tabMain.setupWithViewPager(vpMain);
        tabMain.setTabMode(TabLayout.MODE_FIXED);

        for(int i = 0;i < tabMain.getTabCount();i++){
            TabLayout.Tab tab = tabMain.getTabAt(i);
            tab.setCustomView(mainAdapter.getTabView(i));
        }
    }
}
