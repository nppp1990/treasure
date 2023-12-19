package demo.treasure.dao;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by dx on 2023/12/19.
 */

@Entity(tableName = "record")
public class Record implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    // 存price、isIncome、type、remark、date
    private double price;
    private boolean isIncome;
    private int type;
    private String remark;
    private long date;

    public void setId(int id) {
        this.id = id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public int getType() {
        return type;
    }

    public String getRemark() {
        return remark;
    }

    public long getDate() {
        return date;
    }

    public Record(double price, boolean isIncome, int type, String remark, long date) {
        this.price = price;
        this.isIncome = isIncome;
        this.type = type;
        this.remark = remark;
        this.date = date;
    }
}
