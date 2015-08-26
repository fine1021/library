package com.yxkang.android.sample;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yxkang.android.sample.asynctask.MyAsyncTask;
import com.yxkang.android.util.RootUtil;
import com.yxkang.android.util.WifiPassword;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class SupportActivity extends AppCompatActivity {

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_RESULT2 = 0x2;
    private final InternalHandler mHandler = new InternalHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        initActionbar();
        findViewById(R.id.bt_spt_security).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportActivity.this, SecurityActivity.class));
            }
        });
        findViewById(R.id.bt_spt_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportActivity.this, ImageActivity.class));
            }
        });
        findViewById(R.id.bt_spt_status_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportActivity.this, StatusBarActivity.class));
            }
        });
        findViewById(R.id.bt_spt_xml).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportActivity.this, XmlActivity.class));
            }
        });
        findViewById(R.id.bt_spt_asynctask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAsyncTask();
            }
        });
        findViewById(R.id.bt_spt_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache();
            }
        });
        findViewById(R.id.bt_spt_wifiPwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWifiPwd();
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

    private void clearCache() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String cmd = "rm /data/app/*.tmp";
                boolean result = RootUtil.exeRootCommand(cmd);
                if (result) {
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_POST_RESULT, "Clear Success !"));
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_POST_RESULT, "Clear Fail !"));
                }
            }
        }).start();
    }

    private void showWifiPwd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WifiPassword wifiPassword = new WifiPassword();
                ArrayList<WifiPassword.Password> list = new ArrayList<>();
                try {
                    list = wifiPassword.getWifiPwds();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (list.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (WifiPassword.Password password : list) {
                        builder.append(password.toString());
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_POST_RESULT2, builder.toString()));
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_POST_RESULT, "Can't Find Wifi Password !"));
                }
            }
        }).start();
    }

    public void showToast(String msg) {
        Toast.makeText(SupportActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    public void showDialog(String content) {
        new MaterialDialog.Builder(SupportActivity.this)
                .title("WifiPwd")
                .titleGravity(GravityEnum.CENTER)
                .content(content)
                .contentGravity(GravityEnum.CENTER)
                .positiveText("OK")
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private static class InternalHandler extends Handler {

        private WeakReference<SupportActivity> mActivity;

        public InternalHandler(SupportActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    if (mActivity.get() != null)
                        mActivity.get().showToast((String) msg.obj);
                    break;
                case MESSAGE_POST_RESULT2:
                    if (mActivity.get() != null)
                        mActivity.get().showDialog((String) msg.obj);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_support, menu);
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
