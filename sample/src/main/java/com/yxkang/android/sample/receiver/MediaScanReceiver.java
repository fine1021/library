package com.yxkang.android.sample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yxkang.android.sample.service.MediaModifyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaScanReceiver extends BroadcastReceiver {


    private static final Logger logger = LoggerFactory.getLogger(MediaScanReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            logger.info(action);
            if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
                Intent service = new Intent(context, MediaModifyService.class);
                context.startService(service);
            }
        }
    }
}
