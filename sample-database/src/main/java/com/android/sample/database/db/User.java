package com.android.sample.database.db;

import android.support.database.annotation.Column;
import android.support.database.annotation.Table;

import java.util.Arrays;

/**
 * Created by yexiaokang on 2017/8/17.
 */

@Table(name = "User")
public class User {

    @Column(name = "_id", primaryKey = true)
    private int id;
    @Column(name = "user_name")
    private String name;
    @Column
    private int sex;
    @Column
    private int age;
    @Column
    private Integer mInteger = 1;
    @Column
    private Double mDouble;
    @Column
    private byte[] mBytes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getInteger() {
        return mInteger;
    }

    public void setInteger(Integer integer) {
        mInteger = integer;
    }

    public Double getDouble() {
        return mDouble;
    }

    public void setDouble(Double aDouble) {
        mDouble = aDouble;
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public void setBytes(byte[] bytes) {
        mBytes = bytes;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                ", mInteger=" + mInteger +
                ", mDouble=" + mDouble +
                ", mBytes=" + Arrays.toString(mBytes) +
                '}';
    }
}
