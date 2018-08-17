package com.beidousat.libbns.net.upload;

import java.io.File;

/**
 * Created by J Wong on 2016/6/22.
 */
public interface FileUploadListener {

    void onUploadStart(File file);

    void onUploading(File file, float progress);

    void onUploadCompletion(File file, String desPath);

    void onUploadFailure(File file, String errInfo);

}
