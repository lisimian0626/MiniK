package com.beidousat.libbns.net.request;

/**
 * Created by J Wong on 2015/10/9 17:59.
 */
public interface StoreHttpRequestListener {

     void onStoreSuccess(String method, Object object);

     void onStoreFailed(String method, String error);

     void onStoreStart(String method);


}
