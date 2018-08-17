package com.beidousat.karaoke.player.proxy;

import android.text.TextUtils;

import com.beidousat.karaoke.nanohttpd.protocols.http.IHTTPSession;
import com.beidousat.karaoke.nanohttpd.protocols.http.NanoHTTPD;
import com.beidousat.karaoke.nanohttpd.protocols.http.response.DecodMediaInputStream;
import com.beidousat.karaoke.nanohttpd.protocols.http.response.Response;
import com.beidousat.karaoke.nanohttpd.protocols.http.response.Status;
import com.beidousat.karaoke.nanohttpd.util.IHandler;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by andrei on 7/30/15.
 */
public class MyServer extends NanoHTTPD {
    private final static int PORT = 2800;
    private static final String TAG = "MyServer";

    public MyServer() throws IOException {
        super(PORT);

        addHTTPInterceptor(new IHandler<IHTTPSession, Response>() {
            @Override
            public Response handle(IHTTPSession input) {
                return newResponse(input, getFilePath(input.getUri()));
            }
        });

        start();
    }

    private String getFilePath(String uri) throws IllegalArgumentException {
        if (TextUtils.isEmpty(uri))
            return null;
        String dir = DiskFileUtil.getFileSavedPath("");
        if (TextUtils.isEmpty(dir)) {
            throw new IllegalArgumentException("MyServer 文件路径不能为空！");
        }
        dir = dir.substring(0, dir.length()-1);
        return dir + uri.replace("http://127.0.0.1:2800/", "");
    }

    private Response newResponse(IHTTPSession input, String filePath) {
        Logger.d(TAG, filePath);

        String mimeType = "video/mp4";
        String currentUri = input.getUri();
        if (currentUri != null) {
            String range = null;
            Logger.d(TAG, "Request headers:");
            for (String key : input.getHeaders().keySet()) {
                Logger.d(TAG, "  " + key + ":" + input.getHeaders().get(key));
                if ("range".equals(key)) {
                    range = input.getHeaders().get(key);
                }
            }
            try {
                if (range == null) {
                    return getFullResponse(mimeType, filePath);
                } else {
                    return getPartialResponse(mimeType, range, filePath);
                }
            } catch (IOException e) {
                Logger.e(TAG, "Exception serving file: ", e);
            }
        } else {
            Logger.d(TAG, "Not serving request for: " + currentUri);
        }

        return Response.newFixedLengthResponse(Status.NOT_FOUND, mimeType, "File not found");
    }

    private Response getFullResponse(String mimeType, String filePath) throws
            FileNotFoundException, IOException {
        if (TextUtils.isEmpty(filePath))
            throw new NullPointerException("filepath can not be null");

        File file = new File(filePath);
        if (!file.exists())
            throw new FileNotFoundException(filePath + "not found!");

        FileInputStream fileInputStream = new FileInputStream(filePath);
        DecodMediaInputStream dis = new DecodMediaInputStream(fileInputStream, file);
        return Response.newFixedLengthResponse(Status.OK, mimeType, dis, dis.available());
    }

    private Response getPartialResponse(String mimeType, String rangeHeader, String filePath) throws IOException {
        File file = new File(filePath);
        String rangeValue = rangeHeader.trim().substring("bytes=".length());
        long fileLength = file.length();
        long start, end;
        if (rangeValue.startsWith("-")) {
            end = fileLength - 1;
            start = fileLength - 1
                    - Long.parseLong(rangeValue.substring("-".length()));
        } else {
            String[] range = rangeValue.split("-");
            start = Long.parseLong(range[0]);
            end = range.length > 1 ? Long.parseLong(range[1])
                    : fileLength - 1;
        }
        if (end > fileLength - 1) {
            end = fileLength - 1;
        }

        if (start <= end) {
            if (TextUtils.isEmpty(filePath))
                throw new NullPointerException("filepath can not be null");

            if (!file.exists())
                throw new FileNotFoundException(filePath + "not found!");

            //noinspection ResultOfMethodCallIgnored
            FileInputStream fileInputStream = new FileInputStream(filePath);
            DecodMediaInputStream dis = new DecodMediaInputStream(fileInputStream, file);
            dis.skip(start);

            Response response = Response.newFixedLengthResponse(Status.PARTIAL_CONTENT, mimeType, dis, end -start + 1);
            response.fillContentLength(start, end, fileLength);
            return response;
        } else {
            return Response.newFixedLengthResponse(Status.RANGE_NOT_SATISFIABLE, MIME_HTML, rangeHeader);
        }
    }

}
