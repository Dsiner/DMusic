package com.d.lib.common.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;

import com.d.lib.common.util.provider.FileProviderCompat;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CALL_PHONE;

public final class IntentUtils {
    public static final int MIME_TYPE_ALL = 0;
    public static final int MIME_TYPE_IMAGE = 1;
    public static final int MIME_TYPE_VIDEO = 2;

    @IntDef({MIME_TYPE_ALL, MIME_TYPE_IMAGE, MIME_TYPE_VIDEO})
    @Target({ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MimeType {

    }

    private IntentUtils() {
    }

    /**
     * Return whether the intent is available.
     *
     * @param intent The intent.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isIntentAvailable(final Context context, final Intent intent) {
        return queryIntentActivities(context, intent).size() > 0;
    }

    /**
     * Retrieve all activities that can be performed for the given intent.
     *
     * @param context Context
     * @param intent  The desired intent as per resolveActivity().
     * @return Returns a List of ResolveInfo objects containing one entry for
     * each matching activity, ordered from best to worst.
     * If there are no matching activities, an
     * empty list is returned.
     */
    @NonNull
    private static List<ResolveInfo> queryIntentActivities(final Context context,
                                                           final Intent intent) {
        return context.getApplicationContext()
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    /**
     * Return the intent of install app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param file The file.
     * @return the intent of install app
     */
    public static Intent getInstallAppIntent(final Context context, final File file) {
        if (file == null) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String type = "application/vnd.android.package-archive";
        FileProviderCompat.setDataAndType(context, intent, file, type, false);
        return intent;
    }

    /**
     * Return the intent of uninstall app.
     *
     * @param packageName The name of the package.
     * @return the intent of uninstall app
     */
    public static Intent getUninstallAppIntent(final String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        return intent;
    }

    /**
     * Return the intent of launch app.
     *
     * @param packageName The name of the package.
     * @return the intent of launch app
     */
    public static Intent getLaunchAppIntent(final Context context, final String packageName) {
        return context.getApplicationContext().getPackageManager()
                .getLaunchIntentForPackage(packageName);
    }

    /**
     * Return the intent of launch app details settings.
     *
     * @param packageName The name of the package.
     * @return the intent of launch app details settings
     */
    public static Intent getLaunchAppDetailsSettingsIntent(final String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        return intent;
    }

    /**
     * Return the intent of share text.
     *
     * @param content The content.
     * @return the intent of share text
     */
    public static Intent getShareTextIntent(final String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        return intent;
    }

    /**
     * Return the intent of share image.
     *
     * @param content The content.
     * @param image   The file of image.
     * @return the intent of share image
     */
    public static Intent getShareImageIntent(final Context context,
                                             final String content,
                                             final File image) {
        if (image == null || !image.isFile()) {
            return null;
        }
        return getShareImageIntent(content, FileProviderCompat.getUriForFile(context, image));
    }

    /**
     * Return the intent of share image.
     *
     * @param content The content.
     * @param uri     The uri of image.
     * @return the intent of share image
     */
    public static Intent getShareImageIntent(final String content, final Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/*");
        return intent;
    }

    /**
     * Return the intent of share images.
     *
     * @param content The content.
     * @param images  The files of images.
     * @return the intent of share images
     */
    public static Intent getShareImageIntent(final Context context,
                                             final String content,
                                             final List<File> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        ArrayList<Uri> uris = new ArrayList<>();
        for (File image : images) {
            if (!image.isFile()) {
                continue;
            }
            uris.add(FileProviderCompat.getUriForFile(context, image));
        }
        return getShareImageIntent(content, uris);
    }

    /**
     * Return the intent of share images.
     *
     * @param content The content.
     * @param uris    The uris of images.
     * @return the intent of share images
     */
    public static Intent getShareImageIntent(final String content, final ArrayList<Uri> uris) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.setType("image/*");
        return intent;
    }

    /**
     * Return the intent of component.
     *
     * @param packageName The name of the package.
     * @param className   The name of class.
     * @param bundle      The Bundle of extras to add to this intent.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String packageName,
                                            final String className,
                                            final Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        return intent;
    }

    /**
     * Return the intent of shutdown.
     * <p>Requires root permission
     * or hold {@code android:sharedUserId="android.uid.system"},
     * {@code <uses-permission android:name="android.permission.SHUTDOWN" />}
     * in manifest.</p>
     *
     * @return the intent of shutdown
     */
    public static Intent getShutdownIntent() {
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        return intent;
    }

    /**
     * Return the intent of dial.
     *
     * @param phoneNumber The phone number.
     * @return the intent of dial
     */
    public static Intent getDialIntent(final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        return intent;
    }

    /**
     * Return the intent of call.
     * <p>Must hold {@code <uses-permission android:name="android.permission.CALL_PHONE" />}</p>
     *
     * @param phoneNumber The phone number.
     * @return the intent of call
     */
    @RequiresPermission(CALL_PHONE)
    public static Intent getCallIntent(final String phoneNumber) {
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
        return intent;
    }

    /**
     * Return the intent of send SMS.
     *
     * @param phoneNumber The phone number.
     * @param content     The content of SMS.
     * @return the intent of send SMS
     */
    public static Intent getSendSmsIntent(final String phoneNumber, final String content) {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        return intent;
    }

    /**
     * Return the intent of capture.
     *
     * @param outUri The uri of output.
     * @return the intent of capture
     */
    public static Intent getCaptureIntent(final Uri outUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    public static Intent getPickIntent(final Activity activity,
                                       @MimeType final int mimeType,
                                       final boolean multiple) {
        final Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent documentIntent = getPickDocumentIntent(mimeType, multiple);
            if (isIntentAvailable(activity, documentIntent)) {
                intent = documentIntent;
            } else {
                intent = getPickContentIntent(mimeType, multiple);
            }
        } else {
            intent = getPickContentIntent(mimeType, multiple);
        }

        if (queryIntentActivities(activity, intent).size() > 1) {
            // Create and start the chooser
            return Intent.createChooser(intent, "Pick an image");
        }
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static Intent getPickDocumentIntent(@MimeType final int mimeType,
                                                final boolean multiple) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        setIntentExtra(intent, mimeType, multiple);
        return intent;
    }

    private static Intent getPickContentIntent(@MimeType final int mimeType,
                                               final boolean multiple) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        setIntentExtra(intent, mimeType, multiple);
        return intent;
    }

    private static void setIntentExtra(final Intent intent,
                                       @MimeType final int mimeType,
                                       final boolean multiple) {
        if (MIME_TYPE_IMAGE == mimeType) {
            intent.setType("image/*");
        } else if (MIME_TYPE_VIDEO == mimeType) {
            intent.setType("video/*");
        } else {
            intent.setType("*/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
    }
}
