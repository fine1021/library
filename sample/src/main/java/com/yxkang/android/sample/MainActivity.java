package com.yxkang.android.sample;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.yxkang.android.sample.asynctask.MyAsyncTask;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionbar();
        findViewById(R.id.bt_support_library).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SupportActivity.class));
            }
        });
        findViewById(R.id.bt_asynctask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAsyncTask();
            }
        });

    }

    @SuppressWarnings("ConstantConditions")
    private void initActionbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));
    }

    private void testAsyncTask() {
        new MyAsyncTask().execute();
        new MyAsyncTask().execute();
        new MyAsyncTask().execute();
        new MyAsyncTask().execute();
        new MyAsyncTask().execute();
        new MyAsyncTask().execute();
        new MyAsyncTask().execute();
        new MyAsyncTask().execute();
        new MyAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
