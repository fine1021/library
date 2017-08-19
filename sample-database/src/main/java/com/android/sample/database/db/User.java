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
    @Column(name = "user_name", notNull = true)
    private String name;
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
                ", age=" + age +
                '}';
    }
}
