package com.yxkang.android.image.core.ref;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * RefImageView
 */
public class RefImageView extends RefView<ImageView> {

    public RefImageView(ImageView reference) {
        super(reference);
    }

    @Override
    protected void setImageDrawable(Drawable drawable, ImageView reference) {
        reference.setImageDrawable(drawable);
    }

    @Override
    protected void setImageBitmap(Bitmap bitmap, ImageView reference) {
        reference.setImageBitmap(bitmap);
    }
}
