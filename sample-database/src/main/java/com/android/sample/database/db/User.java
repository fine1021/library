package com.android.sample.database.db;

import android.support.database.annotation.Column;
import android.support.database.annotation.Table;

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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                '}';
    }
}
