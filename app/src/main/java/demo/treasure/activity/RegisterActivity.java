package demo.treasure.activity;

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
public class RegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ((EditText) findViewById(R.id.et_username)).getText().toString();
                final String password1 = ((EditText) findViewById(R.id.et_password1)).getText().toString();
                final String password2 = ((EditText) findViewById(R.id.et_password2)).getText().toString();
                if (check(name, password1, password2)) {
                    UserMockUtils.register(RegisterActivity.this, name, password1);
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private boolean check(String name, String password1, String password2) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password1)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password2)) {
            Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password1.equals(password2)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
