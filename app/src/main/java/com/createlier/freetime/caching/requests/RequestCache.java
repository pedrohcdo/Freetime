package com.createlier.freetime.caching.requests;

import android.util.Log;

import com.createlier.freetime.db.FreetimeDatabase;
import com.createlier.freetime.localdb.LocalDatabase;
import com.createlier.freetime.localdb.dao.WebRequestCacheDao;
import com.createlier.freetime.localdb.objects.WebRequestCacheDBO;
import com.createlier.freetime.utils.GeneralUtils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by pedro on 28/04/17.
 */

final public class RequestCache {


    //
    static ConcurrentLinkedQueue<WebRequestCacheDBO> mRequestsCache = new ConcurrentLinkedQueue<>();


    /**
     *
     */
    final static public void cahceRequest(final String url, final int type, final String params, final int resultCode, final String resultBody) {
        if(resultCode == 404) {
            return;
        }
        final LocalDatabase database = FreetimeDatabase.singleton();
        final WebRequestCacheDao webRequestCacheDao = new WebRequestCacheDao(database);
        final WebRequestCacheDBO webRequestCacheDBO = new WebRequestCacheDBO();
        webRequestCacheDBO.setUrl(url);
        webRequestCacheDBO.setType(type);
        webRequestCacheDBO.setParameters(params);
        webRequestCacheDBO.setResultCode(resultCode);
        webRequestCacheDBO.setResultBody(resultBody);
        try {
            Log.d("FreetimeLog", "Inserting!!!!!: " + resultBody);
            GeneralUtils.SyncThreadPass.obtainSyncThreadPass(WebRequestCacheDao.class, 10, 5000);
            Log.d("FreetimeLog", "InsertedAAAAA!!!: " + resultBody);
            webRequestCacheDao.deleteType(webRequestCacheDBO);
            webRequestCacheDao.insert(webRequestCacheDBO);
            Log.d("FreetimeLog", "Inserted!!!: " + resultBody);
        } catch (Exception e){
            Log.d("FreetimeLog", e.getMessage());
        }
        GeneralUtils.SyncThreadPass.releaseSyncThreadPass(WebRequestCacheDao.class);
    }

    /**
     * Search Request
     *
     * @param url
     * @param type
     * @param params
     * @return
     */
    final static public WebRequestCacheDBO searchrequest(final String url, final int type, final String params) {
        final LocalDatabase database = FreetimeDatabase.singleton();
        final WebRequestCacheDao webRequestCacheDao = new WebRequestCacheDao(database);
        Log.d("FreetimeLog", "A");
        try {
            GeneralUtils.SyncThreadPass.obtainSyncThreadPass(WebRequestCacheDao.class, 10, 5000);
            final List<WebRequestCacheDBO> requests = webRequestCacheDao.listAll(url, type);
            Log.d("FreetimeLog", "Count: " + requests.size());
            for(int i = 0; i<requests.size(); i++) {
                final WebRequestCacheDBO webRequestCacheDBO = requests.get(requests.size() - i - 1);
                GeneralUtils.SyncThreadPass.releaseSyncThreadPass(WebRequestCacheDao.class);
                return webRequestCacheDBO;
            }
        } catch (Exception e) {
            Log.d("FreetimeLog", "Error: " + e.getMessage());
        }
        GeneralUtils.SyncThreadPass.releaseSyncThreadPass(WebRequestCacheDao.class);

        return null;
    }
}
