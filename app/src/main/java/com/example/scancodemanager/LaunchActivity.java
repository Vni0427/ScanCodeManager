package com.example.scancodemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.scancodemanager.Other.CodeMsg;
import com.example.scancodemanager.util.JsonCallback;
import com.lzy.okgo.OkGo;

public class LaunchActivity extends AppCompatActivity {

    private ImageView ivLaunch;
    private Boolean isFirstIn = true;
    private Boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        //为启动页设置动画
        initImage();
    }

    /**
     * 设置动画
     */
    private void initImage() {
        ivLaunch = findViewById(R.id.iv_launch_image);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.4f,1.0f,1.4f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                inPage();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ivLaunch.startAnimation(scaleAnimation);
    }

    /**
     * 判断进入哪个页面，登录页面或主页面
     */
    private void inPage() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        isFirstIn = sharedPreferences.getBoolean("FirstIn", true);
        isLogin = sharedPreferences.getBoolean("Login",false);
        //如果不是第一次打开，且已经登录，检测登录是否过期
        if ((!isFirstIn) && isLogin) {
            isExpired();
        } else {
            goLogin();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("FirstIn", false);
            editor.commit();
        }
    }

    /**
     * 验证登录是否过期
     */
    private void isExpired() {
        String checkUrl = "http://llp.free.idcfengye.com/user/checkapp";
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkGo.<CodeMsg>post(checkUrl)
                        .tag(this)
                        .execute(new JsonCallback<CodeMsg>() {
                            @Override
                            public void onSuccess(com.lzy.okgo.model.Response<CodeMsg> response) {
                                super.onSuccess(response);
                                if(response.body().getCode() == 1){
                                    goMain();
                                }else {
                                    goLogin();
                                }
                            }

                            @Override
                            public void onError(com.lzy.okgo.model.Response<CodeMsg> response) {
                                super.onError(response);
                                goLogin();
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();

                            }
                        });
            }
        }).start();
    }

    /**
     * 跳转登录页面
     */
    private void goLogin() {
        Intent intent = new Intent(LaunchActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转主页面
     */
    private void goMain() {
        Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
