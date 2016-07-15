package com.yxkang.android.sample.bean;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by fine on 2016/7/16.
 */
public class BatteryInfo {

    private static final String prefix = "POWER_SUPPLY";
    private static final String root = "sys/class/power_supply/battery";
    private static final String root2 = "sys/class/power_supply/Battery";

    private String brand;
    private String capacity;
    private String capacity_fcc;
    private String capacity_level;
    private String capacity_rm;
    private String charge_full;
    private String charge_full_design;
    private String charge_now;
    private String current_now;
    private String cycle_count;
    private String fcp_status;
    private String health;
    private String id_voltage;
    private String online;
    private String present;
    private String status;
    private String technology;
    private String temp;
    private String type;
    private String uevent;
    private String voltage_max;
    private String voltage_now;

    public BatteryInfo() {
        init();
    }

    private void init() {
        uevent = getValue(root, "uevent");
        if (TextUtils.isEmpty(uevent)) {
            uevent = getValue(root2, "uevent");
        }
    }


    private String getValue(String rootDir, String fileName) {
        try {
            File file = new File(rootDir, fileName);
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                if (line.startsWith(prefix)) {
                    line = line.substring(13).toLowerCase();
                }
                builder.append(line).append('\n');
                line = br.readLine();
            }
            return builder.toString();
        } catch (Throwable throwable) {
            return null;
        }
    }

    @Override
    public String toString() {
        return uevent;
    }
}
