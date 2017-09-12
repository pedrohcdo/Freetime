package com.createlier.freetime.caching.images;

import android.view.View;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pedro on 27/04/17.
 */

final public class BitmapCacheIdentifier {

    //
    final static HashMap<String, Integer> mIdentifierStateCache = new HashMap<>();

    //
    private volatile AtomicInteger mControlKey = new AtomicInteger();
    private volatile boolean mCacheEnabled = false;
    private volatile String mCacheKey = null;

    /**
     * Get Control Key
     * @return
     */
    final synchronized public int getControlKey() {
        if(mCacheEnabled) {
            synchronized (BitmapCacheIdentifier.class) {
                if(mIdentifierStateCache.containsKey(mCacheKey))
                    return mIdentifierStateCache.get(mCacheKey) - 1;
                else {
                    mIdentifierStateCache.put(mCacheKey, mControlKey.get());
                    return mControlKey.get() - 1;
                }
            }
        } else
            return mControlKey.get() - 1;
    }

    /**
     * Enable Cache
     */
    final synchronized public void enableCache(final String key) {
        if(key == null)
            throw new RuntimeException("Key can not be null.");
        mCacheEnabled = true;
        mCacheKey = key;
    }

    /**
     * Enable Cache
     */
    final synchronized public void enableCache(final View view) {
        if(view == null)
            throw new RuntimeException("View can not be null.");
        mCacheEnabled = true;
        mCacheKey = view.getClass().getName() + ":" + view.hashCode() + ":" + view.getId();
    }

    /**
     * Disable Cache
     */
    final synchronized public void disableCache() {
        mCacheEnabled = false;
    }

    /**
     * Gen Control Key
     *
     * @return
     */
    final synchronized public int genControlKey() {
        if(mCacheEnabled) {
            synchronized (BitmapCacheIdentifier.class) {
                final int key;
                if(mIdentifierStateCache.containsKey(mCacheKey))
                    key = mIdentifierStateCache.get(mCacheKey);
                else
                    key = mControlKey.get();
                mIdentifierStateCache.put(mCacheKey, key + 1);
                return key;
            }
        } else
            return mControlKey.getAndIncrement();
    }
}
