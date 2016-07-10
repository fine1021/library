package com.yxkang.android.sample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yxkang.android.sample.service.MediaModifyService;

public class MediaScanReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
            Intent service = new Intent(context, MediaModifyService.class);
            service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(service);
        }
    }
}
