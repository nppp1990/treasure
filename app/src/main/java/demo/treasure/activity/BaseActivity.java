package demo.treasure.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by dx on 2023/12/19.
 */
class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (showBack()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected boolean showBack() {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
