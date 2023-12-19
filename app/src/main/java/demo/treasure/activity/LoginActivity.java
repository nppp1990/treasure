package demo.treasure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import demo.treasure.R;
import demo.treasure.user.UserMockUtils;

/**
 * Created by dx on 2023/12/19.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_register) {// 跳转到注册页面
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (v.getId() == R.id.btn_login) {
            final String name = ((EditText) findViewById(R.id.et_account)).getText().toString();
            final String password = ((EditText) findViewById(R.id.et_password)).getText().toString();
            if (check(name, password)) {
                if (UserMockUtils.login(this, name, password)) {
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    Toast.makeText(this, "用户名密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean check(String name, String password) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
