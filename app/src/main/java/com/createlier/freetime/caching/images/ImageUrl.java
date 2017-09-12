package com.createlier.freetime.caching.images;

import java.net.URL;

/**
 * Created by pedro on 27/04/17.
 */

public class ImageUrl {

    /**
     * Type
     */
    enum Type {
        THUMB,
        ORIGINAL;
    }

    //
    final String mUrl;
    final String mKey;
    final Type mType;

    /**
     * Constructor
     *
     * @param url
     * @param key
     */
    public ImageUrl(final String url, final String key) {
        mUrl = url;
        mKey = key;
        mType = Type.THUMB;
    }

    /**
     * Constructor
     *
     * @param url
     * @param key
     * @param type
     */
    public ImageUrl(final String url, final String key, final Type type) {
        mUrl = url;
        mKey = key;
        mType = type;
    }

    /**
     * Get Url
     *
     * @return
     */
    public URL getUrl() {
        String finalUrl = mUrl;
        if(mType == Type.ORIGINAL)
            finalUrl += "-original.jpg";
        else
            finalUrl += "-thumb.jpg";
        try {
            return new URL(finalUrl);
        } catch (Exception e) {
            throw new RuntimeException("Wrong image url: " + finalUrl);
        }
    }

    /**
     * Original Version of ImageUrl
     *
     * @return
     */
    public ImageUrl original() {
        return new ImageUrl(mUrl, mKey, Type.ORIGINAL);
    }

    /**
     * Thumb Version of ImageUrl
     * @return
     */
    public ImageUrl thumb() {
        return new ImageUrl(mUrl, mKey, Type.THUMB);
    }

    /**
     * To String
     * @return
     */
    @Override
    public String toString() {
        if(mUrl == null || mKey == null)
            return null;
        return mUrl + "?" + mKey;
    }

    /**
     * Generate ImageUrl from complete url
     *
     * @param completeUrl
     * @return
     */
    public static ImageUrl generateFrom(String completeUrl) {
        String url = null;
        String key = null;
        if(completeUrl == null || "null".equals(completeUrl) || completeUrl.trim().isEmpty())
            completeUrl = null;
        if(completeUrl != null) {
            final int lastIndex = completeUrl.lastIndexOf('?');
            try {
                url = completeUrl.substring(0, lastIndex);
                key = completeUrl.substring(lastIndex + 1);
                return new ImageUrl(url, key);
            } catch (Exception e) {
                throw new RuntimeException("Erro ImageUrl: " + completeUrl + ", " + url + ", " + key);
            }
        }
        return new ImageUrl(null, null);
    }

    /**
     * Is Valid
     *
     * @return
     */
    public boolean isValid() {
        return mUrl != null && mKey != null;
    }
}