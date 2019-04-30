package com.beidousat.libbns.model;

/**
 * Created by J Wong on 2017/5/15.
 */

public class ServerConfigData {

    private static ServerConfigData mServerConfigData;
    private ServerConfig mServerConfig;
    public static ServerConfigData getInstance() {
        if (mServerConfigData == null)
            mServerConfigData = new ServerConfigData();
        return mServerConfigData;
    }

    public void setConfigData(ServerConfig configData) {
        mServerConfig = configData;
    }

    public ServerConfig getServerConfig() {
        return mServerConfig;
    }



}
