package com.lynn.library.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Lynn.
 */

public class FileParams {
    public String key;
    public String fileName;
    public String mediaType;
    public byte[] bytes;

    public FileParams(@NonNull String key, @Nullable String fileName, @NonNull String mediaType, @NonNull byte[] bytes) {
        this.key = key;
        this.fileName = fileName;
        this.mediaType = mediaType;
        this.bytes = bytes;
    }
}
