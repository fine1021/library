package com.yxkang.android.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yxkang.android.os.SystemProperties;
import com.yxkang.android.os.WeakReferenceHandler;
import com.yxkang.android.sample.asynctask.MyAsyncTask;
import com.yxkang.android.sample.asynctask.MyTimeoutAsyncTask;
import com.yxkang.android.util.RootUtil;
import com.yxkang.android.util.ThreadPoolFactory;
import com.yxkang.android.util.WifiPassword;
import com.yxkang.android.util.ZipManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;


public class SupportActivity extends AppCompatActivity {

    private static final String TAG = "SupportActivity";

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_RESULT2 = 0x2;
    private static final int MESSAGE_POST_RESULT3 = 0x3;
    private final InternalHandler mHandler = new InternalHandler(this);
    private static final AtomicInteger mAsyncTaskID = new AtomicInteger(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
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
        findViewById(R.id.bt_spt_systemProperties).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSystemProperties();
            }
        });
        findViewById(R.id.bt_spt_crashHandler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportActivity.this, CrashActivity.class));
            }
        });
        findViewById(R.id.bt_spt_zipManger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testZipManager();
            }
        });
        findViewById(R.id.bt_spt_lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportActivity.this, LockActivity.class));
            }
        });
        findViewById(R.id.bt_spt_drag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportActivity.this, DragActivity.class));
            }
        });
    }


    private void testAsyncTask() {
        for (int i = 0; i < 8; i++) {
            timeoutAsyncTask2();
        }
        new MyAsyncTask() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                for (int i = 0; i < 20; i++) {
                    timeoutAsyncTask();
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return super.doInBackground(params);
            }
        }.execute();

    }

    @SuppressWarnings("unchecked")
    private void timeoutAsyncTask() {
        new MyTimeoutAsyncTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.i(TAG, "onPostExecute: " + s);
            }

            @Override
            protected String doInBackground(Void... params) {
                int value = mAsyncTaskID.getAndIncrement();
                Log.i(TAG, "doInBackground: " + value);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return String.valueOf(value);
            }

            @Override
            protected long getTimeout() {
                return 5000;
            }
        }.execute();
    }

    private void timeoutAsyncTask2() {
        new MyTimeoutAsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.i(TAG, "onPostExecute: " + s);
            }

            @Override
            protected String doInBackground(Void... params) {
                int value = mAsyncTaskID.getAndIncrement();
                Log.i(TAG, "doInBackground: " + value);
                try {
                    Thread.sleep(50000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return String.valueOf(value);
            }

            @Override
            protected long getTimeout() {
                return 10000;
            }
        }.execute();
    }

    private void clearCache() {
        ThreadPoolFactory.getCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                String cmd = "rm /data/app/*.tmp";
                boolean result = RootUtil.exeRootCommand(cmd);
                if (result) {
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_POST_RESULT, "Clear Success !"));
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_POST_RESULT, "Clear Fail !"));
                }
                RootUtil.exeRootCommand("rm /data/local/tmp/*");
            }
        });
    }

    private void showWifiPwd() {
        ThreadPoolFactory.getCachedThreadPool().submit(new Runnable() {
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
                        builder.append(WifiPassword.Password.SSID).append(" : ").append(password.ssid).append("\n");
                        builder.append(WifiPassword.Password.PSK).append(" : ").append(password.psk).append("\n");
                        builder.append(WifiPassword.Password.PRIORITY).append(" : ").append(password.priority).append("\n");
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_POST_RESULT2, builder.toString()));
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_POST_RESULT, "Can't Find Wifi Password !"));
                }
            }
        });
    }

    private void showSystemProperties() {
        ThreadPoolFactory.getCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                StringBuilder builder = new StringBuilder();
                String stringValue;
                int intValue;
                SystemProperties.setDebug(SupportActivity.this);
                stringValue = SystemProperties.get("ro.build.display.id");
                builder.append("id").append(" : ").append(stringValue).append("\n");
                stringValue = SystemProperties.get("ro.build.version.incremental");
                builder.append("incremental").append(" : ").append(stringValue).append("\n");
                intValue = SystemProperties.getInt("ro.build.version.sdk", 0);
                builder.append("sdk").append(" : ").append(intValue).append("\n");
                stringValue = SystemProperties.get("ro.build.version.release");
                builder.append("release").append(" : ").append(stringValue).append("\n");
                stringValue = SystemProperties.get("ro.build.date");
                builder.append("date").append(" : ").append(stringValue).append("\n");
                stringValue = SystemProperties.get("ro.product.name");
                builder.append("name").append(" : ").append(stringValue).append("\n");
                stringValue = SystemProperties.get("ro.product.manufacturer");
                builder.append("manufacturer").append(" : ").append(stringValue).append("\n");
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_POST_RESULT3, builder.toString()));
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(SupportActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void showDialog(String content, String title) {
        new MaterialDialog.Builder(SupportActivity.this)
                .title(title)
                .titleGravity(GravityEnum.START)
                .content(content)
                .contentGravity(GravityEnum.START)
                .positiveText("OK")
                .show();
    }

    private void testZipManager() {
        ThreadPoolFactory.getCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                String src = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "VideoCache";
                File file = new File(src);
                String dest = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "VideoCache.zip";
                File file1 = new File(dest);
                try {
                    ZipManager.getInstance().zipFiles(Arrays.asList(file.listFiles()), file1);
                } catch (IOException e) {
                    android.util.Log.e("ZipManager", "", e);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private static class InternalHandler extends WeakReferenceHandler<SupportActivity> {

        public InternalHandler(SupportActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(SupportActivity reference, Message msg) {
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    reference.showToast((String) msg.obj);
                    break;
                case MESSAGE_POST_RESULT2:
                    reference.showDialog((String) msg.obj, "WifiPwd");
                    break;
                case MESSAGE_POST_RESULT3:
                    reference.showDialog((String) msg.obj, "Properties");
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
