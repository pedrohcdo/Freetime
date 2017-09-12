package com.createlier.freetime.caching.images;

import android.graphics.Bitmap;

/**
 * Created by pedro on 27/04/17.
 */

public class BitmapCacheContainer {

    //
    final BitmapCacheIdentifier mIdentifier = new BitmapCacheIdentifier();

    /** On Download Start */
    public void onDownloadStart() {};

    /** On Download Progress */
    public void onDownloadProgress(final float progress) {};

    /** On Downloaded */
    public void onDownloadEnd() {};

    /** On Loaded */
    public void onCached(final Bitmap bitmap) {};

    /** On Clear */
    public void onClear() {};

    /** On Prepare Container */
    public void onPrepareContainer() {};

    /**
     * Get Identifier
     *
     * @return
     */
    final public BitmapCacheIdentifier getIdentifier() {
        return mIdentifier;
    }
}
