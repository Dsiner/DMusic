package com.d.lib.common.module.permissioncompat.support.lollipop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.util.SimpleArrayMap;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.d.lib.common.module.permissioncompat.support.ManufacturerSupport;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

public class PermissionsChecker {
    private static final String TAG = "PermissionCompat";
    private static final String TAG_NUMBER = "1";
    private static boolean granted = false;

    // Map of dangerous permissions introduced in later framework versions.
    // Used to conditionally bypass permission-hold checks on older devices.
    protected final static SimpleArrayMap<String, Integer> MIN_SDK_PERMISSIONS;

    static {
        MIN_SDK_PERMISSIONS = new SimpleArrayMap<String, Integer>(8);
        MIN_SDK_PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", 14);
        MIN_SDK_PERMISSIONS.put("android.permission.BODY_SENSORS", 20);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.USE_SIP", 9);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.SYSTEM_ALERT_WINDOW", 23);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_SETTINGS", 23);
    }

    /**
     * Returns true if the permission exists in this SDK version
     *
     * @param permission permission
     * @return returns true if the permission exists in this SDK version
     */
    private static boolean permissionExists(String permission) {
        // Check if the permission could potentially be missing on this device
        Integer minVersion = MIN_SDK_PERMISSIONS.get(permission);
        // If null was returned from the above call, there is no need for a device API level check for the permission;
        // otherwise, we check if its minimum API level requirement is met
        return minVersion == null || Build.VERSION.SDK_INT >= minVersion;
    }

    /**
     * Whether cached permission is granted
     */
    public static boolean isPermissionGranted(String permission) {
        return PermissionCache.get(permission);
    }

    /**
     * Ensure whether permission granted
     */
    public synchronized static boolean requestPermissions(Context context, String permission) {
        boolean granted;
        try {
            context = context.getApplicationContext();
            if (!permissionExists(permission)) {
                PermissionCache.put(permission, true);
                return true;
            }
            switch (permission) {
                case Manifest.permission.READ_CONTACTS:
                    granted = checkReadContacts(context);
                    break;
                case Manifest.permission.WRITE_CONTACTS:
                    granted = checkWriteContacts(context);
                    break;
                case Manifest.permission.GET_ACCOUNTS:
                    granted = true;
                    break;

                case Manifest.permission.READ_CALL_LOG:
                    granted = checkReadCallLog(context);
                    break;
                case Manifest.permission.READ_PHONE_STATE:
                    granted = checkReadPhoneState(context);
                    break;
                case Manifest.permission.CALL_PHONE:
                    granted = true;
                    break;
                case Manifest.permission.WRITE_CALL_LOG:
                    granted = checkWriteCallLog(context);
                    break;
                case Manifest.permission.USE_SIP:
                    granted = true;
                    break;
                case Manifest.permission.PROCESS_OUTGOING_CALLS:
                    granted = true;
                    break;
                case Manifest.permission.ADD_VOICEMAIL:
                    granted = true;
                    break;

                case Manifest.permission.READ_CALENDAR:
                    granted = checkReadCalendar(context);
                    break;
                case Manifest.permission.WRITE_CALENDAR:
                    granted = true;
                    break;

                case Manifest.permission.BODY_SENSORS:
                    granted = checkBodySensors(context);
                    break;

                case Manifest.permission.CAMERA:
                    granted = true;
                    break;

                case Manifest.permission.ACCESS_COARSE_LOCATION:
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    granted = checkLocation(context);
                    break;

                case Manifest.permission.READ_EXTERNAL_STORAGE:
                    granted = checkReadStorage(context);
                    break;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    granted = checkWriteStorage(context);
                    break;

                case Manifest.permission.RECORD_AUDIO:
                    granted = checkRecordAudio(context);
                    break;

                case Manifest.permission.READ_SMS:
                    granted = checkReadSms(context);
                    break;
                case Manifest.permission.SEND_SMS:
                case Manifest.permission.RECEIVE_WAP_PUSH:
                case Manifest.permission.RECEIVE_MMS:
                case Manifest.permission.RECEIVE_SMS:
                    granted = true;
                    break;
                default:
                    granted = true;
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "throwing exception in PermissionChecker: ", e);
            granted = false;
        }
        PermissionCache.put(permission, granted);
        return granted;
    }

    /**
     * record audio, {@link Manifest.permission#RECORD_AUDIO},
     * it will consume some resource!!
     */
    private static boolean checkRecordAudio(Context activity) throws Exception {
        AudioRecordManager recordManager = new AudioRecordManager();
        recordManager.startRecord(activity.getExternalFilesDir(Environment.DIRECTORY_RINGTONES)
                + "/" + TAG + ".3gp");
        recordManager.stopRecord();
        // TODO: @dsiner not right here 2018/4/27
        return recordManager.getSuccess();
    }

    /**
     * read calendar, {@link Manifest.permission#READ_CALENDAR}
     */
    private static boolean checkReadCalendar(Context activity) throws Exception {
        Cursor cursor = activity.getContentResolver().query(Uri.parse("content://com" +
                ".android.calendar/calendars"), null, null, null, null);
        if (cursor != null) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * write or delete call log, {@link Manifest.permission#WRITE_CALL_LOG}
     */
    private static boolean checkWriteCallLog(Context activity) throws Exception {
        ContentResolver contentResolver = activity.getContentResolver();
        ContentValues content = new ContentValues();
        content.put(CallLog.Calls.TYPE, CallLog.Calls.INCOMING_TYPE);
        content.put(CallLog.Calls.NUMBER, TAG_NUMBER);
        content.put(CallLog.Calls.DATE, 20140808);
        content.put(CallLog.Calls.NEW, "0");
        contentResolver.insert(Uri.parse("content://call_log/calls"), content);

        contentResolver.delete(Uri.parse("content://call_log/calls"), "number = ?", new
                String[]{TAG_NUMBER});

        return true;
    }

    /**
     * read sms, {@link Manifest.permission#READ_SMS}
     * in MEIZU 5.0~6.0, just according normal phone request
     * in XIAOMI 6.0~, need force judge
     * in XIAOMI 5.0~6.0, not test!!!
     */
    private static boolean checkReadSms(Context context) throws Exception {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"), null, null,
                null, null);
        if (cursor != null) {
            if (ManufacturerSupport.isForceManufacturer()) {
                if (isNumberIndexInfoIsNull(cursor, cursor.getColumnIndex(Telephony.Sms.DATE))) {
                    cursor.close();
                    // TODO: @dsiner not right here: sms is empty 2018/4/27
                    return true;
                }
            }
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * write storage, {@link Manifest.permission#WRITE_EXTERNAL_STORAGE}
     */
    private static boolean checkWriteStorage(Context context) throws Exception {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath(), TAG);
        if (!file.exists()) {
            boolean newFile;
            try {
                newFile = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return newFile;
        } else {
            return file.delete();
        }
    }

    /**
     * read storage, {@link Manifest.permission#READ_EXTERNAL_STORAGE}
     */
    private static boolean checkReadStorage(Context context) throws Exception {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath());
        File[] files = file.listFiles();
        return files != null;
    }

    /**
     * use location, {@link Manifest.permission#ACCESS_FINE_LOCATION},
     * {@link Manifest.permission#ACCESS_COARSE_LOCATION}
     */
    @SuppressLint("MissingPermission")
    private static boolean checkLocation(Context context) throws Exception {
        granted = false;
        final LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        List<String> list = locationManager.getProviders(true);
        if (list.contains(LocationManager.GPS_PROVIDER)) {
            return true;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            if (!locationManager.isProviderEnabled("gps")) {
                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                    requestLocationUpdates(locationManager);
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            requestLocationUpdates(locationManager);
                        }
                    });
                }
            }
            // TODO: @dsiner not right here 2018/4/27
            return granted;
        }
    }

    @SuppressLint("MissingPermission")
    private static void requestLocationUpdates(final LocationManager locationManager) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, new
                LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        locationManager.removeUpdates(this);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        locationManager.removeUpdates(this);
                        granted = true;
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        locationManager.removeUpdates(this);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        locationManager.removeUpdates(this);
                    }
                });
    }

    /**
     * use sensors, {@link Manifest.permission#BODY_SENSORS}
     */
    private static boolean checkBodySensors(Context context) throws Exception {
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor((Sensor.TYPE_ACCELEROMETER));
        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.unregisterListener(listener, sensor);
        return true;
    }

    /**
     * read phone state
     * in XIAOMI OPPO TelephonyManager#getDeviceId() will be null if deny permission
     * in MEIZU TelephonyManager#getSubscriberId() will be null if deny permission
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    private static boolean checkReadPhoneState(Context context) throws Exception {
        TelephonyManager service = (TelephonyManager) context
                .getSystemService(TELEPHONY_SERVICE);
        if (ManufacturerSupport.isMeizu()) {
            return !TextUtils.isEmpty(service.getDeviceId()) || !TextUtils.isEmpty(service.getSubscriberId());
        } else if (ManufacturerSupport.isXiaomi() || ManufacturerSupport.isOppo()) {
            return !TextUtils.isEmpty(service.getDeviceId()) || !TextUtils.isEmpty(service.getSubscriberId());
        } else {
            return !TextUtils.isEmpty(service.getDeviceId())
                    || !TextUtils.isEmpty(service.getSubscriberId());
        }
    }

    /**
     * read call log, {@link Manifest.permission#READ_CALL_LOG}
     */
    private static boolean checkReadCallLog(Context context) throws Exception {
        Cursor cursor = context.getContentResolver().query(Uri.parse
                        ("content://call_log/calls"), null, null,
                null, null);
        if (cursor != null) {
            if (ManufacturerSupport.isForceManufacturer()) {
                if (isNumberIndexInfoIsNull(cursor, cursor.getColumnIndex(CallLog.Calls.NUMBER))) {
                    cursor.close();
                    // TODO: @dsiner not right here: call log is empty 2018/4/27
                    return true;
                }
            }
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * write and delete contacts info, {@link Manifest.permission#WRITE_CONTACTS}
     * and we should get read contacts permission first.
     */
    private static boolean checkWriteContacts(Context context) throws Exception {
        if (checkReadContacts(context)) {
            // write some info
            ContentValues values = new ContentValues();
            ContentResolver contentResolver = context.getContentResolver();
            Uri rawContactUri = contentResolver.insert(ContactsContract.RawContacts
                    .CONTENT_URI, values);
            long rawContactId = ContentUris.parseId(rawContactUri);
            values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds
                    .StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, TAG);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, TAG_NUMBER);
            contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);

            // delete info
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Contacts.Data._ID},
                    "display_name=?", new String[]{TAG}, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(0);
                    resolver.delete(uri, "display_name=?", new String[]{TAG});
                    uri = Uri.parse("content://com.android.contacts/data");
                    resolver.delete(uri, "raw_contact_id=?", new String[]{id + ""});
                }
                cursor.close();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * read contacts, {@link Manifest.permission#READ_CONTACTS}
     */
    private static boolean checkReadContacts(Context context) throws Exception {
        Cursor cursor = context.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            if (ManufacturerSupport.isForceManufacturer()) {
                if (isNumberIndexInfoIsNull(cursor,
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))) {
                    cursor.close();
                    // TODO: @dsiner not right here: contacts is empty 2018/4/27
                    return true;
                }
            }
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * in XIAOMI
     * 1.denied android.Manifest.permission#READ_CONTACTS permission
     * ---->cursor.getCount == 0
     * 2.granted android.Manifest.permission#READ_CONTACTS permission
     * ---->cursor.getCount return real count in contacts
     * so when there are no user or permission denied, it will return 0
     */
    private static boolean isNumberIndexInfoIsNull(Cursor cursor, int numberIndex) {
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                return TextUtils.isEmpty(cursor.getString(numberIndex));
            }
            return false;
        } else {
            return true;
        }
    }
}
