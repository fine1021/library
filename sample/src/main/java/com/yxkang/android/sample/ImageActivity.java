package com.yxkang.android.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.yxkang.android.sample.adapter.ImageAdapter;
import com.yxkang.android.sample.asynctask.LoadFilesTask;
import com.yxkang.android.sample.bean.FileInfoBean;

import java.io.File;
import java.util.List;


@SuppressWarnings("ConstantConditions")
public class ImageActivity extends AppCompatActivity {

    private static final String TAG = ImageActivity.class.getSimpleName();
    private ImageAdapter mAdapter;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        GridView mGridView = (GridView) findViewById(R.id.gv_image_files);
        mAdapter = new ImageAdapter(this, mGridView);
        mGridView.setAdapter(mAdapter);
        queryFiles();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfoBean info = (FileInfoBean) mAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(new File(info.getAbsolutePath()));
                Log.d(TAG, uri.toString());
                intent.setDataAndType(uri, info.getMimeType());
                if (intent.resolveActivity(ImageActivity.this.getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(ImageActivity.this, "无法打开！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));

    }

    private void queryFiles() {

        new LoadFilesTask(this) {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(ImageActivity.this);
                dialog.setMessage("加载中");
                dialog.show();
            }

            @Override
            protected void onPostExecute(List<FileInfoBean> result) {
                super.onPostExecute(result);
                mAdapter.loadFiles(result);
                mAdapter.notifyDataSetChanged();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (result.size() == 0) {
                    Toast.makeText(ImageActivity.this, "未找到文件！", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute();
    }

    @Override
    public void finish() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (mAdapter != null) {
            mAdapter.cancelTask();
        }
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_image, menu);
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
