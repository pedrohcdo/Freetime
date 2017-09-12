package com.createlier.freetime.caching.images;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by pedro on 27/04/17.
 */

public class ImageViewCache extends BitmapCacheContainer {

    //
    final ImageView mProfileImageView;
    final int mPlaceHolder;

    /**
     * Constructor
     */
    private ImageViewCache(final ImageView profileImageView, final int placeHolder) {
        getIdentifier().enableCache(profileImageView);
        mProfileImageView = profileImageView;
        mPlaceHolder = placeHolder;
    }

    /**
     * Create instance from Progressed Image View
     * @param profileImageView
     * @return
     */
    public static ImageViewCache from(final ImageView profileImageView, int placeHolder) {
        return new ImageViewCache(profileImageView, placeHolder);
    }

    /**
     * On Download Start
     */
    @Override
    public void onDownloadStart() {
        mProfileImageView.setImageResource(mPlaceHolder);
    }

    /**
     * On Download Progress
     *
     * @param progress
     */
    @Override
    public void onDownloadProgress(final float progress) {}

    /**
     * On Download End
     */
    @Override
    public void onDownloadEnd() {}

    /**
     * On Cached
     *
     * @param bitmap
     */
    @Override
    public void onCached(final Bitmap bitmap) {
        if(bitmap == null) {
            mProfileImageView.setImageResource(mPlaceHolder);
            return;
        }
        mProfileImageView.setImageBitmap(bitmap);
    }

    /**
     * Prepare Container
     */
    @Override
    public void onPrepareContainer() {
        mProfileImageView.setImageResource(mPlaceHolder);
    }
}
