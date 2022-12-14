package cn.alien95.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by linlongxin on 2015/12/26.
 */
public class Utils {

    private static String TAG = "";

    private static Context mContext;

    private static boolean IS_DEBUG = false;

    private static Handler handler = new Handler();

    /**
     * ???????????????
     */
    public static final String REG_PHONE_CHINA = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    /**
     * ????????????
     */
    public static final String REG_EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

    public static void initialize(Context context) {
        mContext = context;
    }

    public static Context getContext(){
        return mContext;
    }

    public static void setDebug(boolean isDebug, String tag) {
        IS_DEBUG = isDebug;
        TAG = tag;
    }

    public static void Log(String content) {
        if (IS_DEBUG) {
            Log.i(TAG, content);
        }
    }

    public static void Toast(String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
    }

    public static void ToastLong(String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();

    }

    public static void SnackbarShort(View view, String hintStr) {
        Snackbar.make(view, hintStr, Snackbar.LENGTH_SHORT)
                .setAction("", null).show();
    }

    public static void SnackbarLong(View view, String hintStr, String actionStr, View.OnClickListener clickListener) {
        Snackbar.make(view, hintStr, Snackbar.LENGTH_LONG)
                .setAction(actionStr, clickListener).show();
    }

    public static int getScreenWidth() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeith() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void closeInputMethod(Activity activity) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * ?????????????????????
     */
    public static void writeObjectToFile(Object object, File file) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(
                    new FileOutputStream(file));
            os.writeObject(object);// ???User??????????????????
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????????????????
     */
    public static Object readObjectFromFile(File file) {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
            Object object = is.readObject();// ???????????????User?????????
            is.close();
            return object;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ???????????????
     */
    public static int getAppVersion() {
        PackageManager manager = mContext.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionCode;
    }

    /**
     * ?????????????????????cache??????
     */
    public static File getCacheDir() {
        return mContext.getExternalCacheDir();
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     *
     * @param uniqueName ???????????????APP???????????????
     * @return ??????????????????
     */
    public static File setDiskCacheDir(String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = mContext.getExternalCacheDir().getPath();
        } else {
            cachePath = mContext.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * file?????????bitmap
     */
    public static Bitmap fileToBitmap(File file) {
        String filePath = file.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, getBitmapOption(2)); //???????????????????????????????????????1/2
        return bitmap;
    }

    private static BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    /**
     * bitmap?????????????????????
     */
    public static void saveBitmapFile(Bitmap bitmap, String filePath) {
        File file = new File(filePath);//???????????????????????????
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * MD5??????????????????????????????32?????????
     *
     * @param key ????????????????????????
     * @return ??????MD5?????????????????????
     */
    public static String MD5(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    /**
     * ???????????????16???????????????
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * ?????????????????????APP???????????????????????????????????????????????????????????????????????????????????????
     *
     * @param url ??????????????????
     */
    public static void downloadImage(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection urlConnection = null;
                    try {
                        urlConnection = (HttpURLConnection) new URL(url).openConnection();
                        urlConnection.setRequestMethod("GET");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    urlConnection.setDoInput(true);
                    urlConnection.setConnectTimeout(10 * 1000);
                    urlConnection.setReadTimeout(10 * 1000);
                    //???HttpURLConnection????????????????????????????????????connect()???????????????????????????
                    int respondCode;
                    urlConnection.connect();
                    final InputStream inputStream = urlConnection.getInputStream();
                    respondCode = urlConnection.getResponseCode();
                    if (respondCode == HttpURLConnection.HTTP_OK) {
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap == null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.Toast("????????????");
                                }
                            });
                            return;
                        }
                        final File f = new File(Utils.getCacheDir(), Utils.MD5(url) + ".jpg");
                        if (f.exists()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.Toast("???????????????");
                                }
                            });
                            return;
                        }
                        try {
                            FileOutputStream out = new FileOutputStream(f);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();

                            // ????????????????????????????????????
                            try {
                                MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                                        getCacheDir().getAbsolutePath(), Utils.MD5(url) + ".jpg", "");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            // ????????????????????????
                            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + f.getAbsolutePath())));
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.Toast("????????????APP???????????????");
                                }
                            });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * SP
     */
    public static SharedPreferences getSharedPreferences(String name, int mode) {
        return mContext.getSharedPreferences(name, mode);
    }

    /**
     * SP??????string
     */
    public static void SPPutString(SharedPreferences sp, String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * SP??????Int
     */
    public static void SPPutInt(SharedPreferences sp, String key, int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * ????????????
     */
    public static void call(Activity activity,String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
    }

    //??????????????????
    public static void jumpDialUI(Activity activity,String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
    /**
     * ?????????????????????
     */
    public static void callDial(String phoneNumber) {
        mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
    }

    /**
     * ????????????
     */
    public static void sendSms(String phoneNumber,
                               String content) {
        Uri uri = Uri.parse("smsto:"
                + (TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", TextUtils.isEmpty(content) ? "" : content);
        mContext.startActivity(intent);
    }

    /**
     * ?????????????????????
     */
    public static void wakeUpAndUnlock() {
        KeyguardManager km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //??????
        kl.disableKeyguard();
        //???????????????????????????
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        //??????PowerManager.WakeLock??????,???????????????|???????????????????????????,????????????LogCat?????????Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //????????????
        wl.acquire();
        //??????
        wl.release();
    }

    /**
     * ????????????App??????????????????????????????
     */
    public static boolean isApplicationBackground() {
        ActivityManager am = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * ????????????????????????????????????(??????)??????
     */
    public static boolean isSleeping() {
        KeyguardManager kgMgr = (KeyguardManager) mContext
                .getSystemService(Context.KEYGUARD_SERVICE);
        boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();
        return isSleeping;
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    public static boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) mContext
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * ?????????????????????WIFI????????????
     */
    public static boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * ??????APK
     */
    public static void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * ?????????????????????????????????
     */
    public static boolean isPhone() {
        TelephonyManager telephony = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * ?????????????????????IMEI?????????????????????isPhone()????????????
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static String getDeviceIMEI(Context context) {
        String deviceId;
        if (isPhone()) {
            TelephonyManager telephony = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephony.getDeviceId();
        } else {
            deviceId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

        }
        return deviceId;
    }

    /**
     * ?????????????????????MAC??????
     */
    public static String getMacAddress() {
        String macAddress;
        WifiManager wifi = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        macAddress = info.getMacAddress();
        if (null == macAddress) {
            return "";
        }
        macAddress = macAddress.replace(":", "");
        return macAddress;
    }

    /**
     * ?????????SD???
     */
    public static boolean haveSDCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * ?????????????????????
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void hideSoftInput(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void hideSoftInput(Context context, EditText edit) {
        edit.clearFocus();
        InputMethodManager inputmanger = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    /**
     * ?????????????????????
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void showSoftInput(Context context, EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(edit, 0);
    }

    /**
     * ????????????Home???????????????
     */
    public static void goHome() {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        mContext.startActivity(mHomeIntent);
    }

    /**
     * ?????????????????????
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * ??????MCC+MNC?????? (SIM????????????????????????????????????????????????)
     * ???????????????????????????????????????, CDMA ?????????????????????????????????46000 46002, ???????????????46001,???????????????46003???
     */
    public static String getNetworkOperator() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperator();
    }

    /**
     * ????????????????????????????????????
     * (????????????????????????????????????????????????) ???????????????????????????????????????, CDMA ???????????????)
     */
    public static String getNetworkOperatorName() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    /**
     * ????????????????????????
     * PHONE_TYPE_NONE :0 ??????????????????
     * PHONE_TYPE_GSM :1 ???????????????GSM??????????????????
     * PHONE_TYPE_CDMA :2 ???????????????CDMA?????????
     * PHONE_TYPE_SIP:3
     */
    public static int getPhoneType() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType();
    }


    public class Constants {
        /**
         * Unknown network class
         */
        public static final int NETWORK_CLASS_UNKNOWN = 0;

        /**
         * wifi net work
         */
        public static final int NETWORK_WIFI = 1;

        /**
         * "2G" networks
         */
        public static final int NETWORK_CLASS_2_G = 2;

        /**
         * "3G" networks
         */
        public static final int NETWORK_CLASS_3_G = 3;

        /**
         * "4G" networks
         */
        public static final int NETWORK_CLASS_4_G = 4;

    }

    /**
     * ?????????????????????????????????(2G,3G,4G)
     * ?????????3G???UMTS???HSDPA?????????????????????2G???GPRS???EGDE????????????2G???CDMA????????????3G???EVDO
     */
    public static int getNetWorkClass() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return Constants.NETWORK_CLASS_2_G;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return Constants.NETWORK_CLASS_3_G;

            case TelephonyManager.NETWORK_TYPE_LTE:
                return Constants.NETWORK_CLASS_4_G;

            default:
                return Constants.NETWORK_CLASS_UNKNOWN;
        }
    }

    /**
     * ?????????????????????????????????(WIFI??????2,3,4G)
     */
    public static int getNetWorkStatus() {
        int netWorkType = Constants.NETWORK_CLASS_UNKNOWN;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();

            if (type == ConnectivityManager.TYPE_WIFI) {
                netWorkType = Constants.NETWORK_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                netWorkType = getNetWorkClass();
            }
        }

        return netWorkType;
    }

    /**
     * px-sp??????
     */
    public static int px2sp(float pxValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp-px??????
     */
    public static int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * ???????????????????????????
     */
    public static long string2Millis(String str, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern,
                Locale.getDefault());
        long millis = 0;
        try {
            millis = format.parse(str).getTime();
        } catch (ParseException e) {
            Log.e("TAG", e.getMessage());
        }
        return millis;
    }

    /**
     * ??????cpu??????
     */
    public static int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 1;
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
        } catch (SecurityException e) {
            cores = 0;
        }
        return cores;
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

}
