package demo.treasure.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import demo.treasure.R;
import demo.treasure.dao.AppDatabase;
import demo.treasure.dao.Record;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by dx on 2023/12/19.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final CompositeDisposable disposable = new CompositeDisposable();

    private TextView totalTextView;
    private TextView incomeTextView;
    private TextView spendTextView;
    private ListView listView;
    private RecordAdapter adapter;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_float).setOnClickListener(this);
        totalTextView = findViewById(R.id.tv_total);
        incomeTextView = findViewById(R.id.tv_income);
        spendTextView = findViewById(R.id.tv_spend);
        listView = findViewById(R.id.list_view);
        findViewById(R.id.btn_statics).setOnClickListener(this);
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10 || requestCode == 20) && resultCode == RESULT_OK) {
            loadData();
        }
    }

    private void loadData() {
        disposable.add(AppDatabase.getInstance(this).recordDao()
                .getAllRecord()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Record>>() {
                    @Override
                    public void accept(List<Record> records) throws Throwable {
                        updateList(records);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                }));
    }

    @SuppressLint("SetTextI18n")
    private void updateList(List<Record> list) {
        double incomeTotal = 0;
        double spendTotal = 0;
        for (Record record : list) {
            if (record.isIncome()) {
                incomeTotal += record.getPrice();
            } else {
                spendTotal += record.getPrice();
            }
        }
        totalTextView.setText((incomeTotal >= spendTotal ? "+" : "-") + " ￥" + Math.abs(incomeTotal - spendTotal));
        incomeTextView.setText(String.valueOf(incomeTotal));
        spendTextView.setText(String.valueOf(spendTotal));
        if (adapter == null) {
            adapter = new RecordAdapter(this, list);
            listView.setAdapter(adapter);
        } else {
            adapter.setData(list);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_float) {
            AddActivity.start(this);
        } else if (v.getId() == R.id.btn_statics) {
            startActivity(new Intent(this, StaticsActivity.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }

    private static class RecordAdapter extends BaseAdapter implements View.OnLongClickListener, View.OnClickListener {
        private Context context;
        private List<Record> data;

        private RecordAdapter(Context context, List<Record> data) {
            this.context = context;
            this.data = data;
        }

        public void setData(List<Record> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Record getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_record_item, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            convertView.setTag(R.id.poi, i);
            convertView.setOnLongClickListener(this);
            convertView.setOnClickListener(this);
            Record record = getItem(i);
            holder.imageView.setImageResource(getResourceId(record.getType(), record.isIncome()));
            holder.nameView.setText(getTagName(record.getType(), record.isIncome()));
            holder.priceView.setText((record.isIncome() ? "+" : "-") + " ￥" + record.getPrice());
            holder.remarkView.setText(record.getRemark());
            holder.dateView.setText(AddActivity.getShowDataStr(record.getDate()));
            return convertView;
        }

        private String getTagName(int type, boolean isIncome) {
            if (isIncome) {
                switch (type) {
                    case 0:
                        return "工资";
                    case 1:
                        return "红包";
                    case 2:
                        return "报销";
                    case 3:
                        return "理财收益";
                    case 4:
                        return "社保公积金";
                    case 5:
                        return "借款";
                    case 6:
                        return "退款";
                    case 7:
                        return "现金";
                    case 8:
                        return "他人转入";
                    default:
                        return "其他";
                }
            } else {
                switch (type) {
                    case 0:
                        return "餐饮";
                    case 1:
                        return "出行";
                    case 2:
                        return "红包";
                    case 3:
                        return "房租房贷";
                    case 4:
                        return "休闲娱乐";
                    case 5:
                        return "医疗保健";
                    case 6:
                        return "充值缴费";
                    case 7:
                        return "购物";
                    case 8:
                        return "文体教育";
                    default:
                        return "其他";
                }
            }
        }

        private int getResourceId(int type, boolean isIncome) {
            if (isIncome) {
                switch (type) {
                    case 0:
                        return R.drawable.ic_income1;
                    case 1:
                        return R.drawable.ic_income2;
                    case 2:
                        return R.drawable.ic_income3;
                    case 3:
                        return R.drawable.ic_income4;
                    case 4:
                        return R.drawable.ic_income5;
                    case 5:
                        return R.drawable.ic_income6;
                    case 6:
                        return R.drawable.ic_income7;
                    case 7:
                        return R.drawable.ic_income8;
                    case 8:
                        return R.drawable.ic_income9;
                    default:
                        return R.drawable.ic_more;
                }
            } else {
                switch (type) {
                    case 0:
                        return R.drawable.ic_spend1;
                    case 1:
                        return R.drawable.ic_spend2;
                    case 2:
                        return R.drawable.ic_spend3;
                    case 3:
                        return R.drawable.ic_spend4;
                    case 4:
                        return R.drawable.ic_spend5;
                    case 5:
                        return R.drawable.ic_spend6;
                    case 6:
                        return R.drawable.ic_spend7;
                    case 7:
                        return R.drawable.ic_spend8;
                    case 8:
                        return R.drawable.ic_spend9;
                    default:
                        return R.drawable.ic_more;
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            // 弹框确认是否删除
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示");
            builder.setMessage("是否删除该条记录？");
            builder.setPositiveButton("确定", (dialog, which) -> {
                // 删除
                Record record = getItem((Integer) view.getTag(R.id.poi));
                AppDatabase.getInstance(context).recordDao()
                        .deleteRecord(record)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Throwable {
                                data.remove(record);
                                notifyDataSetChanged();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Throwable {

                            }
                        });
            });
            builder.setNegativeButton("取消", null);
            builder.show();
            return false;
        }

        @Override
        public void onClick(View view) {
            // 跳转到编辑页面
            Record record = getItem((Integer) view.getTag(R.id.poi));
            AddActivity.goEditPage((AppCompatActivity) context, record);
        }

        private static class Holder {
            private ImageView imageView;
            private TextView nameView;
            private TextView priceView;
            private TextView remarkView;
            private TextView dateView;

            private Holder(View item) {
                imageView = item.findViewById(R.id.type_icon);
                nameView = item.findViewById(R.id.type_name);
                priceView = item.findViewById(R.id.price);
                remarkView = item.findViewById(R.id.remark);
                dateView = item.findViewById(R.id.date);
            }
        }
    }
}
