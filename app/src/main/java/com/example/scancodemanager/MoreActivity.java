package com.example.scancodemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvAccount;
    private TextView tvSuggestion;
    private TextView tvAboutVersion;
    private TextView tvSignOutAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        tvAccount = findViewById(R.id.tv_more_account);
        tvSuggestion = findViewById(R.id.tv_more_suggestion);
        tvAboutVersion = findViewById(R.id.tv_more_about_version);
        tvSignOutAccount = findViewById(R.id.tv_more_signout_account);

        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        String userName = sharedPreferences.getString("Account","用户名");
        tvAccount.setText(userName);

        tvSuggestion.setOnClickListener(this);
        tvAboutVersion.setOnClickListener(this);
        tvSignOutAccount.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_more_suggestion:
                Intent intent1 = new Intent(MoreActivity.this,SuggestionActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_more_about_version:
                Intent intent2 = new Intent(MoreActivity.this,VersionActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_more_signout_account:
                Intent intent3 = new Intent(MoreActivity.this,LoginActivity.class);
                intent3.putExtra("page",1);
                startActivity(intent3);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
