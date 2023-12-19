package demo.treasure.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import demo.treasure.R;
import demo.treasure.dao.AppDatabase;
import demo.treasure.dao.Record;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static android.text.format.DateUtils.isToday;

/**
 * Created by dx on 2023/12/19.
 */
public class AddActivity extends BaseActivity implements View.OnClickListener {
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");

    private RecyclerView gridView;
    private GridAdapter gridAdapter;
    private long dateInMills;
    private Record record;

    public static void start(Activity activity) {
        activity.startActivityForResult(new Intent(activity, AddActivity.class), 10);
    }

    public static void goEditPage(Activity activity, Record record) {
        Intent intent = new Intent(activity, AddActivity.class);
        intent.putExtra("record_detail", record);
        activity.startActivityForResult(intent, 20);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        findViewById(R.id.tag_income).setOnClickListener(this);
        findViewById(R.id.tag_expend).setOnClickListener(this);
        findViewById(R.id.date).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        gridView = findViewById(R.id.type_grid);
        gridView.setLayoutManager(new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false));
        record = null;
        if (getIntent().hasExtra("record_detail")) {
            setTitle(R.string.demo_title_edit);
            record = (Record) getIntent().getSerializableExtra("record_detail");
            setAddIncome(record.isIncome());
            ((EditText) findViewById(R.id.price)).setText(String.valueOf(record.getPrice()));
            ((TextView) findViewById(R.id.date)).setText(getShowDataStr(record.getDate()));
            dateInMills = record.getDate();
            gridAdapter.setSelectedIndex(record.getType());
            ((Button) findViewById(R.id.btn_add)).setText("确认修改");
        } else {
            setTitle(R.string.demo_title_add);
            setAddIncome(false);
            dateInMills = System.currentTimeMillis();
            ((TextView) findViewById(R.id.date)).setText(getShowDataStr(dateInMills));
            ((Button) findViewById(R.id.btn_add)).setText("确认添加");
        }
    }

    public static String getShowDataStr(long timeInMills) {
        // 如果是今天
        if (isToday(timeInMills)) {
            return "今天";
        } else if (isToday(timeInMills + 24 * 60 * 60 * 1000)) {
            return "昨天";
        } else if (isToday(timeInMills + 24 * 60 * 60 * 1000 * 2)) {
            return "前天";
        } else if (isToday(timeInMills - 24 * 60 * 60 * 1000)) {
            return "明天";
        } else {
            // year-m-d
            return formatter.format(new Date(timeInMills));
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMills);
        // 日期弹框
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
            dateInMills = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            ((TextView) findViewById(R.id.date)).setText(getShowDataStr(dateInMills));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setAddIncome(boolean isIncome) {
        View incomeView = findViewById(R.id.tag_income);
        View expendView = findViewById(R.id.tag_expend);
        if (gridAdapter == null) {
            gridAdapter = new GridAdapter(isIncome ? incomeTypeList : spendTypeList, 0);
            gridView.setAdapter(gridAdapter);
        } else {
            gridAdapter.setSelectedIndex(0);
            gridAdapter.setData(isIncome ? incomeTypeList : spendTypeList);
        }
        if (isIncome) {
            incomeView.setSelected(true);
            expendView.setSelected(false);
        } else {
            incomeView.setSelected(false);
            expendView.setSelected(true);
        }
    }

    @SuppressLint("CheckResult")
    private void addRecord() {
        EditText priceText = findViewById(R.id.price);
        double price;
        try {
            price = Double.parseDouble(priceText.getText().toString());
        } catch (Exception e) {
            showToast("请输入正确的金额");
            return;
        }
        if (price <= 0) {
            showToast("请输入金额");
            return;
        }
        boolean isIncome = findViewById(R.id.tag_income).isSelected();
        int type = gridAdapter.selectedIndex;
        String remark = ((EditText) findViewById(R.id.remark)).getText().toString();

        if (record == null) {
            record = new Record(price, isIncome, type, remark, dateInMills);
        } else {
            record.setPrice(price);
            record.setIncome(isIncome);
            record.setType(type);
            record.setDate(dateInMills);
            record.setRemark(remark);
        }
        AppDatabase db = AppDatabase.getInstance(this);
        db.recordDao().insertOrUpdate(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        setResult(RESULT_OK);
                        finish();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tag_income) {
            if (v.isSelected()) {
                return;
            }
            setAddIncome(true);
        } else if (v.getId() == R.id.tag_expend) {
            if (v.isSelected()) {
                return;
            }
            setAddIncome(false);
        } else if (v.getId() == R.id.date) {
            showDatePicker();
        } else if (v.getId() == R.id.btn_add) {
            addRecord();
        }
    }

    public static List<GridItem> spendTypeList = new ArrayList<>();
    public static List<GridItem> incomeTypeList = new ArrayList<>();

    static {
        spendTypeList.add(new GridItem("餐饮", R.drawable.ic_spend1));
        spendTypeList.add(new GridItem("出行", R.drawable.ic_spend2));
        spendTypeList.add(new GridItem("红包", R.drawable.ic_spend3));
        spendTypeList.add(new GridItem("房租房贷", R.drawable.ic_spend4));
        spendTypeList.add(new GridItem("休闲娱乐", R.drawable.ic_spend5));
        spendTypeList.add(new GridItem("医疗保健", R.drawable.ic_spend6));
        spendTypeList.add(new GridItem("充值缴费", R.drawable.ic_spend7));
        spendTypeList.add(new GridItem("购物", R.drawable.ic_spend8));
        spendTypeList.add(new GridItem("文体教育", R.drawable.ic_spend9));
        spendTypeList.add(new GridItem("其他", R.drawable.ic_more));

        incomeTypeList.add(new GridItem("工资", R.drawable.ic_income1));
        incomeTypeList.add(new GridItem("红包", R.drawable.ic_income2));
        incomeTypeList.add(new GridItem("报销", R.drawable.ic_income3));
        incomeTypeList.add(new GridItem("理财收益", R.drawable.ic_income4));
        incomeTypeList.add(new GridItem("社保公积金", R.drawable.ic_income5));
        incomeTypeList.add(new GridItem("借款", R.drawable.ic_income6));
        incomeTypeList.add(new GridItem("退款", R.drawable.ic_income7));
        incomeTypeList.add(new GridItem("现金", R.drawable.ic_income8));
        incomeTypeList.add(new GridItem("他人转入", R.drawable.ic_income9));
        incomeTypeList.add(new GridItem("其他", R.drawable.ic_more));
    }

    private static class GridItem {
        private int drawableId;
        private String name;

        private GridItem(String name, int drawableId) {
            this.name = name;
            this.drawableId = drawableId;
        }
    }

    private static class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> implements View.OnClickListener {

        private List<GridItem> data;
        private int selectedIndex;

        private GridAdapter(List<GridItem> data, int index) {
            this.data = data;
            this.selectedIndex = index;
        }

        private void setData(List<GridItem> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        private void setSelectedIndex(int index) {
            this.selectedIndex = index;
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_type_item, parent, false);
            return new GridViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
            GridItem item = data.get(position);
            holder.imageView.setImageResource(item.drawableId);
            holder.nameView.setText(item.name);
            holder.imageView.setSelected(selectedIndex == position);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(this);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onClick(View v) {
            setSelectedIndex((int) v.getTag());
        }

        static class GridViewHolder extends RecyclerView.ViewHolder {
            private TextView nameView;
            private ImageView imageView;

            public GridViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.type_icon);
                nameView = itemView.findViewById(R.id.type_name);
            }
        }
    }
}
