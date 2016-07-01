package com.acmezon.acmezon_dash.image_url;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "LazyList");
        } else {
            cacheDir = context.getCacheDir();
        }

        if(!cacheDir.exists()) {
            boolean created = cacheDir.mkdirs();
            if(!created) {
                Log.d("ACMEZON", "File not created");
            }
        }
    }

    public File getFile(String url) {

        String filename = String.valueOf(url.hashCode());

        return new File(cacheDir, filename);
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if(files == null) {
            return;
        }

        for(File f : files) {
            boolean deleted = f.delete();

            if(!deleted) {
                Log.d("ACMEZON", "File couldn\'t be deleted");
            }
        }
    }
}
