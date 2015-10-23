package com.yxkang.android.sample;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yxkang.android.provider.Settings;
import com.yxkang.android.sample.bean.DisplayInfoBean;
import com.yxkang.android.util.ContextUtil;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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
        findViewById(R.id.bt_display).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDisplayInfo();
            }
        });
        findViewById(R.id.bt_gms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startApplication("com.google.android.gms");
            }
        });
        String value = Settings.Global.getString(getContentResolver(), "table_name", "unknown");
        Log.i(TAG, "table_name = " + value);
    }

    @SuppressWarnings("ConstantConditions")
    private void initActionbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));
    }

    /**
     * open a app
     *
     * @param packageName pkg
     */
    private void startApplication(String packageName) {
        int result = ContextUtil.startApplication(MainActivity.this, packageName);
        switch (result) {
            case ContextUtil.TYPE_SUCCESS:
                break;
            case ContextUtil.TYPE_FAILED:
                Toast.makeText(MainActivity.this, "Can't Find The Application !", Toast.LENGTH_SHORT).show();
                break;
            case ContextUtil.TYPE_NOT_UNIQUE:
                Toast.makeText(MainActivity.this, "The Application Has More Then One Main Entry Point!", Toast.LENGTH_SHORT).show();
                break;
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
