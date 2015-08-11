package com.yxkang.android.sample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yxkang.android.sample.asynctask.MyAsyncTask;
import com.yxkang.android.sample.bean.DisplayInfoBean;

import java.util.List;


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
        findViewById(R.id.bt_display).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDisplayInfo();
            }
        });
        findViewById(R.id.bt_gms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp("com.google.android.gms");
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

    /**
     * open a app
     *
     * @param packageName pkg
     */
    private void openApp(String packageName) {
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);

        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(
                resolveIntent, 0);

        if (resolveInfos.size() > 0) {
            ResolveInfo riInfo = resolveInfos.iterator().next();
            if (riInfo != null) {
                String pkg = riInfo.activityInfo.packageName;
                String cls = riInfo.activityInfo.name;

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName name = new ComponentName(pkg, cls);
                intent.setComponent(name);
                startActivity(intent);
            }
        } else {
            Toast.makeText(MainActivity.this, "open failed!", Toast.LENGTH_SHORT).show();
        }

    }

    private void showDisplayInfo() {
        DisplayInfoBean bean = new DisplayInfoBean(this);
        new MaterialDialog.Builder(this)
                .title("Display Information")
                .titleGravity(GravityEnum.CENTER)
                .content(bean.toString())
                .contentGravity(GravityEnum.CENTER)
                .positiveText(android.R.string.ok)
                .show();
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
