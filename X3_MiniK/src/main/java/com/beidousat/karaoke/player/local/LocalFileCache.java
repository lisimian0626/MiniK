package com.beidousat.karaoke.player.local;

import com.beidousat.karaoke.player.proxy.Config;
import com.beidousat.karaoke.player.proxy.IDownState;

import java.io.File;

public class LocalFileCache implements IDownState {

    public static final int CacheLen = 1024 * 1024;
    private static final String BASE_PATH = "/sdcard/cache/";
    private static LocalFileCache _this = new LocalFileCache();
    private FileHeadLoadThread thread = null;
    private int curDown = 0;
    private Cache caches[] = new Cache[]{new Cache(), new Cache()};

    public static LocalFileCache getInstance() {
        return _this;
    }

    private LocalFileCache() {
        new File(BASE_PATH).mkdir();
    }

    public int find(String url) {
        int ret = -1;
        if (url.equals(caches[0].url))
            ret = 0;
        else if (url.equals(caches[1].url))
            ret = 1;
        return ret;
    }

    private void startDown(String url, int ind) {
        if (null != thread) {
            thread.stopThread();
            thread = null;
        }
        if (null == url) return;
        System.out.println("startDown = " + url);
        int indx = find(url);
        if (0 != caches[indx].totalLen && caches[indx].downedLen > 0)
            return;
        thread = new FileHeadLoadThread(url, BASE_PATH + ind, this, CacheLen);

        thread.startThread();
    }

    public void add(String curUrl, String nextUrl) {
        int ind = find(curUrl);
        if (ind == -1) {
            caches[0].iserror = false;
            caches[1].iserror = false;
            caches[0].url = curUrl;
            caches[0].downedLen = caches[0].totalLen = 0;
            if (!curUrl.equals(nextUrl)) {
                caches[1].url = nextUrl;
                caches[1].downedLen = caches[1].totalLen = 0;
            }
            startDown(curUrl, 0);
        } else if (ind == 0) {
            if (find(nextUrl) == -1) {
                caches[1].iserror = false;
                caches[1].url = nextUrl;
                caches[1].downedLen = caches[1].totalLen = 0;
                if (0 != caches[0].totalLen && caches[0].downedLen > 0) startDown(nextUrl, 1);
            }
        } else if (ind == 1) {
            if (find(nextUrl) == -1) {
                caches[0].iserror = false;
                caches[0].url = nextUrl;
                caches[0].downedLen = caches[0].totalLen = 0;
                if (0 != caches[1].totalLen && caches[1].downedLen > 0) startDown(nextUrl, 0);
            }
        }
    }

    public Cache getBuf(String url, int start) {
        synchronized (LocalFileCache.class) {
            int ret = 0;
            int ind = find(url);
            if (ind != -1) {
                if (caches[ind].downedLen > start) {
                    return caches[ind];
                }
            }
        }
        return null;
    }

    public boolean checkCache(int start) {
        return start < LocalFileCache.CacheLen;
    }

    public boolean isEnc(String url) {
        int ind = find(url);
        if (ind != -1) {
            return caches[ind].isenc;
        }
        return false;
    }

    public boolean checkend(String url, int len) {
        int ind = find(url);
        if (ind != -1) {
            if (len >= caches[ind].totalLen)
                return true;
        } else
            return true;
        return false;

    }

    public boolean isError(String url) {
        int ind = find(url);
        if (ind != -1) {
            if (caches[ind].iserror)
                return true;
        }
        return false;
    }

    public String getHeader(String url, long len) {
        int ind = find(url);
        if (ind != -1) {
            if (caches[ind].iserror) {
                StringBuilder out = new StringBuilder();
                out.append("HTTP/1.1 404 Not Found" + Config.LINE_END);
                out.append("Server: nginx/1.2.9" + Config.LINE_END);
                out.append("Content-Type: text/html" + Config.LINE_END);
                out.append("Content-Length: 0" + Config.LINE_END);
                out.append("Connection: close" + Config.HTTP_BODY_END);

                return out.toString();
            }
            if (caches[ind].totalLen > 0) {
                StringBuilder out = new StringBuilder();
                out.append("HTTP/1.1 200 OK" + Config.LINE_END);
                out.append("Server: nginx/1.2.9" + Config.LINE_END);
                out.append("Content-Type: video/mp4" + Config.LINE_END);
                out.append("Accept-Ranges: bytes" + Config.LINE_END);
                out.append("Content-Length: " + (caches[ind].totalLen - len) + Config.LINE_END);
                out.append("Content-Range: bytes " + len + "-" + (caches[ind].totalLen - 1) + "/" + caches[ind].totalLen + Config.LINE_END);
                out.append("Connection: keep-alive" + Config.HTTP_BODY_END);
                return out.toString();
            }
        }
        if (thread.checkStop())
            startDown(url, ind);
        return null;
    }


    @Override
    public void onInit(String url, int tatol, boolean isenc) {
        int ind = find(url);
        if (-1 != ind) {
            caches[ind].totalLen = tatol;
            caches[ind].isenc = isenc;
        }
    }

    @Override
    public void onDown(String url, int len, DownState state, byte[] buf, int bufLen) {
        synchronized (LocalFileCache.class) {
            int ind = find(url);
            if (-1 != ind) {
                if (state == DownState.DOWNING) {
                    if (LocalFileCache.CacheLen < caches[ind].downedLen + bufLen)
                        bufLen = LocalFileCache.CacheLen - caches[ind].downedLen;
                    if (bufLen > 0) {
                        System.arraycopy(buf, 0, caches[ind].buf, caches[ind].downedLen, bufLen);
                        caches[ind].downedLen = caches[ind].downedLen + bufLen;
                    }

                } else if (state == DownState.SUCCESS) {
                    if (ind == 0) ind = 1;
                    else ind = 0;
                    if (0 == caches[ind].totalLen || caches[ind].downedLen < 1)
                        startDown(caches[ind].url, ind);
                } else if (state == DownState.ERROREXIT) {
                    caches[ind].iserror = true;
                    if (ind == 0) ind = 1;
                    else ind = 0;
                    if (0 == caches[ind].totalLen || caches[ind].downedLen < 1)
                        startDown(caches[ind].url, ind);
                }
            }
        }
    }
}

class Cache {
    public String url;
    public int totalLen;
    public int downedLen;
    public boolean isenc;
    public boolean iserror = false;
    public byte[] buf = new byte[LocalFileCache.CacheLen];
}