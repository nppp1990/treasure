package demo.treasure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import demo.treasure.R;
import demo.treasure.user.UserMockUtils;

/**
 * Created by daxiong on 2023/12/19.
 */
public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // 延时2s，跳转
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed()) {
                    return;
                }
                if (isLogin()) {
                    // 跳转到主页面
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                } else {
                    // 跳转到登录页面
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 2000);
    }

    private boolean isLogin() {
        return UserMockUtils.isLogin(this);
    }
}
