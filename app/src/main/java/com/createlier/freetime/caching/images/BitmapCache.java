package com.createlier.freetime.caching.images;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.createlier.freetime.services.ServiceConnector;
import com.createlier.freetime.services.ServiceRunnable;
import com.createlier.freetime.services.ServicesManager;
import com.createlier.freetime.services.shared.SharedServicesManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pedro on 29/06/2016.
 */
final public class BitmapCache {

    // Private Static Variables
    private static boolean mSetuped = false;
    private static LruCache<String, Bitmap> mMemoryCache;
    private static List<BitmapCacheIdentifier> mIdentifiers = new ArrayList<>();
    private static Context mContext;
    private static SharedServicesManager mSharedServicesManager;

    /** Module Class */
    private BitmapCache(){};

    /**
     * Setup Bitmap Cache
     */
    public static void setup(final Context context, final SharedServicesManager sharedServicesManager, float memCachePercent) {
        //
        if(mSetuped)
            return;
        //
        mContext = context;
        mSharedServicesManager = sharedServicesManager;
        // Create Cache
        int memCacheSize = Math.round(memCachePercent * Runtime.getRuntime().maxMemory() / 1024);
        Log.d("LogTest", "Memory cache created (size = " + memCacheSize + ")");
        mMemoryCache = new LruCache<String, Bitmap>(memCacheSize) {

            /**
             * Measure Data
             *
             * @param key
             * @param bitmap
             * @return
             */
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                final int bitmapSize = bitmap.getByteCount() / 1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }
        };

        // Setuped Flag
        mSetuped = true;
    }

    /**
     * Get Shared Services Manager
     *
     * @return
     */
    final public static SharedServicesManager getSharedServicesManager() {
        return mSharedServicesManager;
    }

    /**
     * Put Bitmap to Cache
     *
     * @param key
     * @param bitmap
     */
    public static void putBitmap(String key, Bitmap bitmap) {
        if(!mSetuped)
            throw new RuntimeException("BitmapCache not setuped!");
        if (key == null || bitmap == null || mMemoryCache.get(key) != null)
            return;
        mMemoryCache.put(key, bitmap);
    }

    /**
     * Add Identifier
     *
     * @param identifier
     */
    public static void addIdentifier(final BitmapCacheIdentifier identifier) {
        mIdentifiers.add(identifier);
    }

    /**
     * Get from memory cache.
     *
     */
    public static Bitmap getBitmap(final String key) {
        if(!mSetuped)
            throw new RuntimeException("BitmapCache not setuped!");
        return mMemoryCache.get(key);
    }

    /**
     * Get from memory cache.
     */
    public static Bitmap getBitmap(final Uri uri) {
        if(!mSetuped)
            throw new RuntimeException("BitmapCache not setuped!");
        return mMemoryCache.get(generateKeyForBitmap(uri));
    }

    /**
     * Get from memory cache.
     */
    public static Bitmap getBitmap(final int res) {
        if(!mSetuped)
            throw new RuntimeException("BitmapCache not setuped!");
        return mMemoryCache.get(generateKeyForBitmap(res));
    }

    /**
     * Get from memory cache.
     */
    public static Bitmap getBitmap(final Uri uri, final int reqWidth, final int reqHeight) {
        if(!mSetuped)
            throw new RuntimeException("BitmapCache not setuped!");
        return mMemoryCache.get(generateKeyForBitmap(uri, reqWidth, reqHeight));
    }

    /**
     * Has Bitmap
     *
     * @param uri
     * @return
     */
    public static boolean hasBitmap(final Uri uri) {
        return mMemoryCache.get(generateKeyForBitmap(uri)) != null;
    }

    /**
     * Has Bitmap
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static boolean hasBitmap(final Uri uri, final int reqWidth, final int reqHeight) {
        return mMemoryCache.get(generateKeyForBitmap(uri)) != null;
    }

    /**
     * Has Bitmap
     *
     * @param res
     * @return
     */
    public static boolean hasBitmap(final int res) {
        return mMemoryCache.get(generateKeyForBitmap(res)) != null;
    }

    /**
     * Generate Key for Bitmap Uri
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static String generateKeyForBitmap(final Uri uri, final int reqWidth, final int reqHeight) {
        return uri.toString() + "[" + reqWidth + "," + reqHeight + "]";
    }

    /**
     * Generate Key for Bitmap File
     *
     * @param
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static String generateKeyForBitmap(final File file, final int reqWidth, final int reqHeight) {
        return file.getName() + "[" + reqWidth + "," + reqHeight + "]";
    }

    /**
     * Generate Key for Bitmap Url
     *
     * @param
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static String generateKeyForBitmap(final ImageUrl url, final int reqWidth, final int reqHeight) {
        return url.toString() + "[" + reqWidth + "," + reqHeight + "]";
    }

    /**
     * Generate Key for Bitmap Uri
     *
     * @param uri
     * @return
     */
    public static String generateKeyForBitmap(final Uri uri) {
        return uri.toString() + "[--]";
    }

    /**
     * Generate Key for Bitmap Uri
     *
     * @param res
     * @return
     */
    public static String generateKeyForBitmap(final int res) {
        return "__key[fromres]:" + res + "[--]";
    }

    /**
     * Generate Key for Bitmap for String
     *
     * @param res
     * @return
     */
    public static String generateKeyForBitmap(final String res) {
        return "__key[fromres]:" + res + "[--]";
    }

    /**
     * Put Bitmap From Uri
     *
     * @param reqWidth
     * @param reqHeight
     * @param container
     *
     * @return Request Identifier
     */
    public static void loadAsyncBitmapFromFile(final File file, final int reqWidth, final int reqHeight, final BitmapCacheContainer container) {
        // Get Control Key for compare in future
        final int currentKey = container.getIdentifier().genControlKey();
        //
        if(file == null) {
            mSharedServicesManager.postMessage(new Runnable() {
                @Override
                public void run() {
                    if (currentKey == container.getIdentifier().getControlKey())
                        container.onCached(null);
                }
            });
            return;
        }
        final Bitmap bmpCache = getBitmap(generateKeyForBitmap(file, reqWidth, reqHeight));
        if(bmpCache != null) {
            mSharedServicesManager.postMessage(new Runnable() {
                @Override
                public void run() {
                    mSharedServicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey())
                                container.onCached(bmpCache);
                        }
                    });
                }
            });
        } else {
            mSharedServicesManager.addService(new ServiceRunnable() {
                @Override
                public void run(ServicesManager servicesManager, ServiceConnector connector) {
                    // Post Start
                    servicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey())
                                container.onDownloadStart();
                        }
                    });
                    // Load Bitmap
                    final Bitmap bitmap = loadBitmapFromFile(file, reqWidth, reqHeight);
                    // Post Downloaded
                    servicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey()) {
                                container.onDownloadProgress(100);
                                container.onDownloadEnd();
                                container.onCached(bitmap);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * Prepare Container
     *
     * @param container
     */
    public static synchronized  void prepareContainer(final BitmapCacheContainer container) {
        // Get Control Key for compare in future
        final int currentKey = container.getIdentifier().genControlKey();
        //
        mSharedServicesManager.postMessage(new Runnable() {
            @Override
            public void run() {
                if (currentKey == container.getIdentifier().getControlKey())
                    container.onPrepareContainer();
            }
        });
    }

    /**
     * Put Bitmap From Uri
     *
     * @param container
     *
     * @return Request Identifier
     */
    public static synchronized void loadAsyncBitmapFromUrl(final ImageUrl url, final int reqWidth, final int reqHeight, final BitmapCacheContainer container) {
        // Get Control Key for compare in future
        final int currentKey = container.getIdentifier().genControlKey();
        //
        if(!url.isValid()) {
            mSharedServicesManager.postMessage(new Runnable() {
                @Override
                public void run() {
                    if (currentKey == container.getIdentifier().getControlKey())
                        container.onCached(null);
                }
            });
            return;
        }
        //
        final Bitmap bmpCache = getBitmap(generateKeyForBitmap(url, reqWidth, reqHeight));
        // If bitmap has cached
        if(bmpCache != null) {
            mSharedServicesManager.postMessage(new Runnable() {
                @Override
                public void run() {
                    mSharedServicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey())
                                container.onCached(bmpCache);
                        }
                    });
                }
            });
        } else {
            mSharedServicesManager.addService(new ServiceRunnable() {
                @Override
                public void run(ServicesManager servicesManager, ServiceConnector connector) {
                    // Post Start
                    servicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey())
                                container.onDownloadStart();
                        }
                    });
                    // Load Bitmap
                    final Bitmap bitmap = loadBitmapFromUrl(url, reqWidth, reqHeight);
                    // Post Downloaded
                    servicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("ImageLoad", "Download end, key " + currentKey + ", " + container.getIdentifier().getControlKey());
                            if(currentKey == container.getIdentifier().getControlKey()) {
                                container.onDownloadProgress(100);
                                container.onDownloadEnd();
                                container.onCached(bitmap);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * Put Bitmap From Uri
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @param container
     *
     * @return Request Identifier
     */
    public static void loadAsyncBitmapFromUri(final Uri uri, final int reqWidth, final int reqHeight, final BitmapCacheContainer container) {
        // Get Control Key for compare in future
        final int currentKey = container.getIdentifier().genControlKey();
        //
        if(container == null) {
            mSharedServicesManager.postMessage(new Runnable() {
                @Override
                public void run() {
                    if (currentKey == container.getIdentifier().getControlKey())
                        container.onCached(null);
                }
            });
            return;
        }
        final Bitmap bmpCache = getBitmap(generateKeyForBitmap(uri, reqWidth, reqHeight));
        if(bmpCache != null) {
            mSharedServicesManager.postMessage(new Runnable() {
                @Override
                public void run() {
                    mSharedServicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey())
                                container.onCached(bmpCache);
                        }
                    });
                }
            });
        } else {
            mSharedServicesManager.addService(new ServiceRunnable() {
                @Override
                public void run(ServicesManager servicesManager, ServiceConnector connector) {
                    // Post Start
                    servicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey())
                                container.onDownloadStart();
                        }
                    });
                    // Load Bitmap
                    final Bitmap bitmap = loadBitmapFromUri(uri, reqWidth, reqHeight);
                    // Post Downloaded
                    servicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey()) {
                                container.onDownloadProgress(100);
                                container.onDownloadEnd();
                                container.onCached(bitmap);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * Put Bitmap From Uri
     *
     * @param uri
     * @param container
     *
     * @return Request Identifier
     */
    public static void loadAsyncBitmapFromUri(final Uri uri, final BitmapCacheContainer container) {
        // Get Control Key for compare in future
        final int currentKey = container.getIdentifier().genControlKey();
        //
        if(container == null) {
            mSharedServicesManager.postMessage(new Runnable() {
                @Override
                public void run() {
                    if (currentKey == container.getIdentifier().getControlKey())
                        container.onCached(null);
                }
            });
            return;
        }
        final Bitmap bmpCache = getBitmap(generateKeyForBitmap(uri));
        if(bmpCache != null) {
            mSharedServicesManager.postMessage(new Runnable() {
                @Override
                public void run() {
                    mSharedServicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey())
                                container.onCached(bmpCache);
                        }
                    });
                }
            });
        } else {
            mSharedServicesManager.addService(new ServiceRunnable() {
                @Override
                public void run(ServicesManager servicesManager, ServiceConnector connector) {
                    // Post Start
                    servicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey())
                                container.onDownloadStart();
                        }
                    });
                    // Load Bitmap
                    final Bitmap bitmap = loadBitmapFromUri(uri);
                    // Post Downloaded
                    servicesManager.postMessage(new Runnable() {
                        @Override
                        public void run() {
                            if(currentKey == container.getIdentifier().getControlKey()) {
                                container.onDownloadProgress(100);
                                container.onDownloadEnd();
                                container.onCached(bitmap);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * Load Bitmap From Uri
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap loadBitmapFromUri(final Uri uri, final int reqWidth, final int reqHeight) {
        Bitmap bitmap = null;
        // If exist in cache
        final String key = generateKeyForBitmap(uri, reqWidth, reqHeight);
        bitmap = getBitmap(key);
        if(bitmap != null)
            return bitmap;
        // Load if not exist
        try {
            // Get File Descriptor
            final AssetFileDescriptor assetFileDescriptor = mContext.getContentResolver().openAssetFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
            // Decode info
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            // Decode Bitmap
            options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            // Close Descriptor
            assetFileDescriptor.close();
        } catch (Exception e) {
            Log.d("LogTest", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        // Save on cache
        putBitmap(key, bitmap);
        // Return Bitmap
        return bitmap;
    }

    /**
     * Load Bitmap From Uri
     *
     * @param uri
     * @return
     */
    public static Bitmap loadBitmapFromUri(final Uri uri) {
        Bitmap bitmap = null;
        // If exist in cache
        final String key = generateKeyForBitmap(uri);
        bitmap = getBitmap(key);
        if(bitmap != null) {
            Log.d("LogTest", "Recycled!");
            return bitmap;
        }
        // Load if not exist
        try {
            // Get File Descriptor
            final AssetFileDescriptor assetFileDescriptor = mContext.getContentResolver().openAssetFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            // Close Descriptor
            assetFileDescriptor.close();
        } catch (Exception e) {
            Log.d("LogTest", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        // Save on cache
        putBitmap(key, bitmap);
        // Return Bitmap
        return bitmap;
    }

    /**
     * Load Bitmap From Resource
     *
     * @param res
     * @return
     */
    public static Bitmap loadBitmapFromResource(final int res) {
        Bitmap bitmap = null;
        // If exist in cache
        final String key = generateKeyForBitmap(res);
        bitmap = getBitmap(key);
        if (bitmap != null)
            return bitmap;
        // Load if not exist
        try {
            // Decode Bitmap
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), res);
        } catch (Exception e) {
            Log.d("LogTest", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        // Save on cache
        putBitmap(key, bitmap);
        // Return Bitmap
        return bitmap;
    }

    /**
     * Load Bitmap From Filename
     *
     * @return
     */
    public static Bitmap loadBitmapFromUrl(final ImageUrl url, int reqWidth, int reqHeight) {
        //
        if(!url.isValid())
            return null;
        //
        Bitmap bitmap = null;
        // If exist in cache
        final String key = generateKeyForBitmap(url, reqWidth, reqHeight);
        bitmap = getBitmap(key);
        if(bitmap != null)
            return bitmap;
        Log.d("LogTest", "Loadded: " + url);
        // Load if not exist
        try {
            HttpURLConnection conn = (HttpURLConnection) url.getUrl().openConnection();
            Log.d("ImageLoad", "Loading from: " + url.getUrl().toString());
            conn.setDoInput(true);
            conn.connect();
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 1;
            opt.inDither = false;
            opt.inScaled = false;
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            InputStream is = conn.getInputStream();
            Bitmap result = BitmapFactory.decodeStream(is, null,  opt);
            if(reqWidth != -1 && reqHeight != -1) {
                final Bitmap scaled = Bitmap.createScaledBitmap(result, reqWidth, reqHeight, true);
                if(scaled != result)
                    result.recycle();
                result = scaled;
            } else if(reqWidth == -1 && reqHeight >= 0) {
                final float aspect = (result.getHeight() * 1.0f) / result.getWidth();
                final Bitmap scaled = Bitmap.createScaledBitmap(result, (int)(reqHeight / aspect), reqHeight, true);
                if(scaled != result)
                    result.recycle();
                result = scaled;
            } else if(reqWidth >= 0 && reqHeight == -1) {
                final float aspect = (result.getHeight() * 1.0f) / result.getWidth();
                final Bitmap scaled = Bitmap.createScaledBitmap(result, reqWidth, (int)(reqWidth * aspect), true);
                if(scaled != result)
                    result.recycle();
                result = scaled;
            }
            is.close();
            // Save Bitmap to cache
            putBitmap(key, result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Load Bitmap From Filename
     *
     * @return
     */
    public static Bitmap loadBitmapFromFile(final File file, int reqWidth, int reqHeight) {
        Bitmap bitmap;
        // If exist in cache
        final String key = generateKeyForBitmap(file, reqWidth, reqHeight);
        bitmap = getBitmap(key);
        if(bitmap != null)
            return bitmap;
        // Load if not exist
        try {
            // Decode Bitmap
            Log.d("LogTest", "loaded " + file.getAbsolutePath().toString());
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());


            if(reqWidth != -1 && reqHeight != -1) {
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
                if(scaled != bitmap)
                    bitmap.recycle();
                bitmap = scaled;
            } else if(reqWidth == -1 && reqHeight >= 0) {
                final float aspect = (bitmap.getHeight() * 1.0f) / bitmap.getWidth();
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, (int)(reqHeight / aspect), reqHeight, true);
                if(scaled != bitmap)
                    bitmap.recycle();
                bitmap = scaled;
            } else if(reqWidth >= 0 && reqHeight == -1) {
                final float aspect = (bitmap.getHeight() * 1.0f) / bitmap.getWidth();
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, reqWidth, (int)(reqWidth * aspect), true);
                if(scaled != bitmap)
                    bitmap.recycle();
                bitmap = scaled;
            }
        } catch (Exception e) {
            Log.d("LogTest", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        // Save on cache
        putBitmap(key, bitmap);
        // Return Bitmap
        return bitmap;
    }

    /**
     * Calculate In Sample Size
     *
     * @param width
     * @param height
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(final int width, final int height, final int reqWidth, final int reqHeight) {
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
}