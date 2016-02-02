package com.yxkang.android.sample.db;

import com.yxkang.android.annotation.Column;
import com.yxkang.android.annotation.Table;

/**
 * Created by yexiaokang on 2016/2/1.
 */
@Table(name = "SettingsInfo")
public class SettingsInfo {

    @Column(name = "id", primary = true, notNull = true, order = 1)
    private int id;

    @Column(name = "key", type = "VARCHAR(40)", notNull = true, order = 2)
    private String key;

    @Column(name = "value", order = 3)
    private String value;

    @Column(name = "extend", order = 4)
    private String extend;
}
