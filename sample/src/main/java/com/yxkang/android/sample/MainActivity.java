package com.yxkang.android.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yxkang.android.os.WeakReferenceHandler;
import com.yxkang.android.provider.Settings;
import com.yxkang.android.sample.bean.DisplayInfoBean;
import com.yxkang.android.sample.db.DatabaseHelper;
import com.yxkang.android.sample.media.MediaScannerService;
import com.yxkang.android.util.ContextUtil;
import com.yxkang.android.util.RootUtil;
import com.yxkang.android.util.ThreadManager;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@SuppressWarnings("TryWithIdenticalCatches")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final MainHandler mHandler = new MainHandler(this);
    private ProgressDialog progressDialog;
    private static final int MESSAGE_SHOW_DIALOG = 0x100;
    private static final int MESSAGE_DISMISS_DIALOG = 0x101;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        findViewById(R.id.bt_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFavoritePictures();
            }
        });
        findViewById(R.id.bt_reboot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRebootOptions();
            }
        });
        String value = Settings.Global.getString(getContentResolver(), "table_name", "unknown");
        Log.i(TAG, "table_name = " + value);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getReadableDatabase();
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

    private void showFavoritePictures() {
//        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/QQ_Favorite";
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
        String noMediaFilePath = root + File.separator + MediaStore.MEDIA_IGNORE_FILENAME;
        File file = new File(noMediaFilePath);
        if (file.exists()) {
            Log.i(TAG, "delete file = " + file.delete());
        }
        Intent service = new Intent(this, MediaScannerService.class);
        service.putExtra(MediaScannerService.EXTRA_SCAN_TYPE, MediaScannerService.SCAN_DIR);
        service.putExtra(MediaScannerService.EXTRA_SCAN_PATH, root);
        startService(service);
        Toast.makeText(MainActivity.this, "Start Scan Files", Toast.LENGTH_SHORT).show();
    }

    private void showDisplayInfo() {
        DisplayInfoBean bean = new DisplayInfoBean(this);
        new MaterialDialog.Builder(this)
                .title("Display Information")
                .titleGravity(GravityEnum.START)
                .content(bean.toString())
                .contentGravity(GravityEnum.START)
                .positiveText(android.R.string.ok)
                .show();
    }

    private void showRebootOptions() {
        new MaterialDialog.Builder(this)
                .title("Reboot Options")
                .titleGravity(GravityEnum.CENTER)
                .items(R.array.reboot_options)
                .itemsGravity(GravityEnum.CENTER)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Log.i(TAG, "which = " + which + " text = " + text.toString());
                        reboot(which);
                    }
                })
                .show();
    }

    private void reboot(int which) {
        String command = "reboot";
        switch (which) {
            case 0:
                command = "reboot";
                break;
            case 1:
                command = "reboot -p";
                break;
            case 2:
                command = "reboot recovery";
                break;
            case 3:
                command = "reboot bootloader";
                break;
            default:
                break;
        }
        Log.i(TAG, "command = " + command);
        Future<Boolean> future = ThreadManager.getInstance().submit(new RootTask(command));
        ThreadManager.getInstance().submit(new WaitingTask(future));
    }

    private synchronized void showProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.waiting_please));
        progressDialog.show();
    }

    private synchronized void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private class RootTask implements Callable<Boolean> {

        final String command;

        public RootTask(String command) {
            this.command = command;
        }

        @Override
        public Boolean call() throws Exception {
            mHandler.sendEmptyMessage(MESSAGE_SHOW_DIALOG);
            Thread.sleep(3000);
            return RootUtil.exeRootCommand(command);
        }
    }

    private class WaitingTask implements Runnable {
        final Future<Boolean> future;

        public WaitingTask(Future<Boolean> future) {
            this.future = future;
        }

        @Override
        public void run() {
            try {
                boolean result = future.get();
                Log.i(TAG, "result = " + result);
                mHandler.sendEmptyMessage(MESSAGE_DISMISS_DIALOG);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThreadManager.getInstance().shutdownNow();
        mHandler.removeCallbacksAndMessages(null);
    }

    private static class MainHandler extends WeakReferenceHandler<MainActivity> {

        public MainHandler(MainActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(MainActivity reference, Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_DIALOG:
                    reference.showProgressDialog();
                    break;
                case MESSAGE_DISMISS_DIALOG:
                    reference.dismissProgressDialog();
                    break;
            }
        }
    }
}
