package com.android.sample.database;

import android.os.Bundle;
import android.support.database.Session;
import android.support.database.log.StatusLogger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.sample.database.db.MySQLiteHelper;
import com.android.sample.database.db.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Session mSession;
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusLogger.getLogger().setDebugEnabled(true);
        StatusLogger.getLogger().setInfoEnabled(true);
        MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(this);
        mSession = mySQLiteHelper.getSession();
        user.setId(1);
        user.setName("fine");
        user.setAge(10);
        mSession.replace(user);
        debug();

        findViewById(android.R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                user.setId(10);
                user.setBytes(new byte[]{1, 2, 3});
                user.setName(String.valueOf(time));
                mSession.replace(user);
                debug();
            }
        });
    }

    private void debug() {
        List<User> users = mSession.query(User.class);
        Log.i(TAG, "debug: " + users.toString());
    }
}
