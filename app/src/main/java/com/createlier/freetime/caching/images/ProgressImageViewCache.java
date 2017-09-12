package com.createlier.freetime.caching.images;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.createlier.freetime.R;

/**
 * Created by pedro on 27/04/17.
 */

public class ProgressImageViewCache extends BitmapCacheContainer {

    //
    final ProgressBar mProgressBar;
    final ImageView mImageView;
    final int mPlaceHolder;

    /**
     * Constructor
     */
    public ProgressImageViewCache(final View progressedImageView, final int placeHolder) {
        getIdentifier().enableCache(progressedImageView);
        mProgressBar = (ProgressBar) progressedImageView.findViewById(R.id.imageview_ext_indicator_progress_bar);
        mImageView = (ImageView) progressedImageView.findViewById(R.id.imageview_ext_indicator_image_view);
        mPlaceHolder = placeHolder;
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Create instance from Progressed Image View
     * @param progressedImageView
     * @return
     */
    public static ProgressImageViewCache from(final View progressedImageView, int placeHolder) {
        return new ProgressImageViewCache(progressedImageView, placeHolder);
    }

    /**
     * On Download Start
     */
    @Override
    public void onDownloadStart() {
        mImageView.setImageResource(mPlaceHolder);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setIndeterminate(true);
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
    public void onDownloadEnd() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * On Cached
     *
     * @param bitmap
     */
    @Override
    public void onCached(final Bitmap bitmap) {
        if(bitmap == null) {
            mImageView.setImageResource(mPlaceHolder);
            return;
        }
        mImageView.setImageBitmap(bitmap);
    }

    /**
     * Prepare Container
     */
    @Override
    public void onPrepareContainer() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setIndeterminate(true);
        mImageView.setImageResource(mPlaceHolder);
    }
}
