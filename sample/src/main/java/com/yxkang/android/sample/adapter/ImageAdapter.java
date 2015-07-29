package com.yxkang.android.sample.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yxkang.android.image.ImageDownloader;
import com.yxkang.android.image.ImageLoader;
import com.yxkang.android.sample.R;
import com.yxkang.android.sample.bean.FileInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yexiaokang on 2015/7/29.
 */
public class ImageAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    public static final String TAG = ImageAdapter.class.getSimpleName();

    private List<FileInfoBean> list = new ArrayList<>();

    private Context context;

    private Drawable defaultIcon, videoIcon;

    private ImageLoader imageLoader;

    private GridView mGridView;

    private boolean isFirstEnter = true;

    private int mFirstVisibleItem;

    private int mVisibleItemCount;

    public ImageAdapter(Context context, GridView mGridView) {
        this.context = context;
        this.mGridView = mGridView;
        this.imageLoader = new ImageLoader();
        this.mGridView.setOnScrollListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            this.defaultIcon = context.getResources().getDrawable(R.mipmap.ic_action_picture);
            this.videoIcon = context.getResources().getDrawable(R.mipmap.ic_action_video);
        } else {
            this.defaultIcon = context.getDrawable(R.mipmap.ic_action_picture);
            this.videoIcon = context.getDrawable(R.mipmap.ic_action_video);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_thumbnail);
            holder.textView = (TextView) convertView.findViewById(R.id.item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileInfoBean info = list.get(position);

        holder.imageView.setTag(info.getAbsolutePath());
        holder.textView.setText(info.getFileTitle());

        if (!info.isUpdate()) {
            holder.imageView.setImageDrawable(defaultIcon);
        } else {
            holder.imageView.setImageDrawable(info.getFileIcon());
        }
        return convertView;
    }

    private void showImage(int first, int count) {

        imageLoader.startTask();

        for (int i = first; i < first + count && !imageLoader.isTaskCancelled(); i++) {

            final FileInfoBean info = list.get(i);

            final ImageView imageView = (ImageView) mGridView.findViewWithTag(info.getAbsolutePath());

            if (!info.isUpdate()) {
                String uri = ImageDownloader.Protocol.FILE.wrap(info.getAbsolutePath());
                imageLoader.displayImageAsync(uri, new ImageLoader.OnImageLoaderListener() {

                    @Override
                    public void onImageLoaderSuccess(String uri, Bitmap bitmap) {
                        if (imageView != null && bitmap != null) {
                            info.setFileIcon(new BitmapDrawable(context.getResources(), bitmap));
                            info.setUpdate(true);
                            if (!imageLoader.isTaskCancelled()) imageView.setImageDrawable(info.getFileIcon());
                            Log.d(TAG, "onImageLoaderSuccess : " + uri);
                        }
                    }

                    @Override
                    public void onImageLoaderFail(String uri) {

                    }

                    @Override
                    public void onImageLoaderStart(String uri) {
                        if (imageView != null) {
                            imageView.setImageDrawable(defaultIcon);
                        }
                        Log.d(TAG, "onImageLoaderStart : " + uri);
                    }
                });
            } else {
                imageView.setImageDrawable(info.getFileIcon());
            }

        }
    }

    public void cancelTask() {
        imageLoader.cancelTask();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            showImage(mFirstVisibleItem, mVisibleItemCount);
        } else {
            cancelTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;

        if (isFirstEnter && visibleItemCount > 0) {
            showImage(mFirstVisibleItem, mVisibleItemCount);
            isFirstEnter = false;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    public void loadFiles(List<FileInfoBean> fileInfos) {
        this.list.clear();
        this.list.addAll(fileInfos);
    }
}

