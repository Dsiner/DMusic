package com.d.lib.common.util.provider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import java.io.File;

public class FileProviderCompat extends FileProvider {
    private static final String AUTHORITY_SUFFIX = ".common.provider";

    public static Uri getUriForFile(@NonNull Context context, @NonNull File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile24(context, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Uri getUriForFile24(@NonNull Context context, @NonNull File file) {
        // Provider authorities
        return FileProvider.getUriForFile(context,
                context.getPackageName() + AUTHORITY_SUFFIX, file);
    }

    public static void setDataAndType(@NonNull Context context,
                                      @NonNull Intent intent,
                                      @NonNull File file,
                                      @Nullable String type,
                                      boolean writable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writable) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.setDataAndType(getUriForFile24(context, file), type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }
}
