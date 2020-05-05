package com.witype.Dragger.integration;

import android.app.Application;

/**
 * Created by WiType on 2020/5/5.
 * Email witype716@gmail.com
 * Desc:
 */
public class FileManager {

    public static FileManager instance;

    private String fileCache;

    public static FileManager newInstance(Application application) {
        if (instance == null) {
            instance = new FileManager(application);
        }
        return instance;
    }

    public static FileManager getInstance() {
        return instance;
    }

    public FileManager(Application application) {
        fileCache = application.getApplicationContext().getFilesDir().getPath();
    }

    public String getFileCacheDir() {
        return fileCache;
    }
}
