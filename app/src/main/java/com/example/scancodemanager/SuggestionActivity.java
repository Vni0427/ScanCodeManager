package com.example.scancodemanager;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scancodemanager.Other.CodeMsg;
import com.example.scancodemanager.util.JsonCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

public class SuggestionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText suggestionContent;
    private Button suggestionSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        suggestionContent = findViewById(R.id.et_suggestion_content);
        suggestionContent.setFocusable(true);
        suggestionContent.setFocusableInTouchMode(true);
        suggestionContent.requestFocus();

        suggestionSubmit = findViewById(R.id.btn_suggestion_submit);
        suggestionSubmit.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_suggestion_submit:
                if(suggestionContent.getText().equals("")){
                    Toast.makeText(this, "文本内容为空", Toast.LENGTH_SHORT).show();
                }else {
                    String content = String.valueOf(suggestionContent.getText());
                    String suggestionUrl = "http://llp.free.idcfengye.com/suggestion/submit";
                    OkGo.<CodeMsg>post(suggestionUrl)
                            .tag(this)
                            .params("content",content)
                            .execute(new JsonCallback<CodeMsg>() {
                                @Override
                                public void onSuccess(Response<CodeMsg> response) {
                                    super.onSuccess(response);
                                    if(response.body().getCode() == 1){
                                        Toast.makeText(SuggestionActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        Toast.makeText(SuggestionActivity.this, "提交失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(Response<CodeMsg> response) {
                                    super.onError(response);
                                    Toast.makeText(SuggestionActivity.this, "网络请求失败！", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
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
