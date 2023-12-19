package demo.treasure.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;

import demo.treasure.R;

/**
 * Created by dx on 2023/12/20.
 */
public class StaticsActivity extends BaseActivity {
    LineChart lineChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statics);
        lineChart = findViewById(R.id.line_chart);
        test();
    }

    private void test() {
        // TODO dx 2023/12/20 折线图
    }
}
