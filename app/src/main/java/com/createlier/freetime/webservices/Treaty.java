package com.createlier.freetime.webservices;

/**
 * Created by pedro on 28/04/17.
 */

final public class Treaty {

    //
    public static boolean DEFAULT_CACHING = false;
    public static boolean DEFAULT_CACHING_COMPARE = true;
    public static boolean DEFAULT_SHOW_CACHE = true;

    // Private Variables
    private boolean mCaching = DEFAULT_CACHING;
    private boolean mCachingCompare = DEFAULT_CACHING_COMPARE;
    private boolean mShowCache = DEFAULT_SHOW_CACHE;

    /**
     * Constructor
     */
    public Treaty() {};

    /**
     * Constructor
     */
    public Treaty(final boolean caching, final boolean cachingCompare, final boolean showCache) {
        mCaching = caching;
        mCachingCompare = cachingCompare;
        mShowCache = showCache;
    };

    /**
     * Set Caching
     *
     * @param caching
     */
    final public void setCaching(final boolean caching) {
        mCaching = caching;
    }

    /**
     * Set Caching Compare
     *
     * @param cachingCompare
     */
    final public void setCachingCompare(final boolean cachingCompare) {
        mCachingCompare = cachingCompare;
    }

    /**
     * Set Show Cache
     *
     * @param showCache
     */
    final public void setShowCache(final boolean showCache) {
        mShowCache = showCache;
    }

    /**
     * Get Caching
     *
     * @return
     */
    final public boolean caching() {
        return mCaching;
    }

    /**
     * Get Caching Compare
     *
     * @return
     */
    final public boolean cachingCompare() {
        return mCachingCompare;
    }

    /**
     * Get Show Cache
     *
     * @return
     */
    final public boolean showCache() {
        return mShowCache;
    }
}
