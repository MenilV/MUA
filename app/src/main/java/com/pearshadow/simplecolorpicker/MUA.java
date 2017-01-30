package com.pearshadow.simplecolorpicker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Menil on 1/30/2017.
 */

public class MUA {

    private static MUA instance = null;
    private static DimensionUtils dimensionInstance = null;
    private static SharedPreferenceUtils sharedPreferenceInstance = null;

    private MUA() {
        // only useful to defeat instantiation
    }

    public MUA getInstance() {
        if (instance == null)
            instance = new MUA();

        return instance;
    }

    // General
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isAppInstalled(Context context, String uri) {

        PackageManager pm = context.getPackageManager();

        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static boolean invalidString(String s) {
        return !validString(s);
    }

    private static boolean validString(String s) {
        return s != null && !s.trim().equalsIgnoreCase("");
    }

    public static void openFile(Context context, File url) throws IOException {
        // Create URI

        Uri uri = Uri.fromFile(url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Locale getCurrentLocale(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return context.getResources().getConfiguration().getLocales().get(0);
            } else {
                //noinspection deprecation
                return context.getResources().getConfiguration().locale;
            }
        } catch (Exception e) {
            return Locale.US;
        }
    }

    public static DimensionUtils getDimensionUtilsInstance() {
        if (dimensionInstance == null)
            dimensionInstance = new DimensionUtils();

        return dimensionInstance;
    }

    public static SharedPreferenceUtils getSharedPreferanceUtilsInstance(Context context) {
        if (sharedPreferenceInstance == null)
            sharedPreferenceInstance = new SharedPreferenceUtils(context);

        return sharedPreferenceInstance;
    }

    // Other utils

    private static class StaticValues {
        static final String SERVICE_URL = "http://localhost:3000/";
        static final long TIMEOUT_SEC = 30;

        // request codes
        public static final int REQUEST_CODE_CALENDAR_PERMISSION = 5;
        public static final int REQUEST_CODE_CONTACTS_PERMISSION = 6;
        public static final int REQUEST_CODE_CAMERA_PERMISSION = 7;

        // intents
        public static final int CAMERA_INTENT = 11;
        public static final int GALLERY_INTENT = 12;

        // date formats
        public static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"; // without timezone
        public static String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
        public static String DATE_FORMAT_NO_TIME = "dd/MM/yyyy";
        public static String DATE_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss";

        // placeholder images
        public static final String PROFILE_IMG = "https://s-media-cache-ak0.pinimg.com/originals/de/01/a9/de01a9254b3eb6855e40c3a622c62dfa.png";

    }

    private static class DimensionUtils {
        private static boolean isInitialised = false;
        private static float pixelsPerOneDp;

        // Suppress default constructor for non-instantiability.
        private DimensionUtils() {
            throw new AssertionError();
        }

        private static void init(View view) {
            pixelsPerOneDp = view.getResources().getDisplayMetrics().densityDpi / 160f;
            isInitialised = true;
        }

        public static float pxToDp(View view, float px) {
            if (!isInitialised) {
                init(view);
            }

            return px / pixelsPerOneDp;
        }

        public static float dpToPx(View view, float dp) {
            if (!isInitialised) {
                init(view);
            }

            return dp * pixelsPerOneDp;
        }
    }

    private static class SharedPreferenceUtils {
        private static final int PRIVATE_MODE = 0;
        private static final String PREF_NAME = "SharedPref";
        private static final String KEY_LOGGED = "key_logged";
        private static final String KEY_ID = "key_id";
        private static final String KEY_PASSWORD = "key_password";
        private static final String KEY_MAIL = "key_email";
        private static final String KEY_TOKEN = "key_token";

        private Context context;
        private SharedPreferences preferences;

        private SharedPreferenceUtils(Context context) {
            this.context = context;
            preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        }

        public static SharedPreferenceUtils getInstance(Context context) {
            return new SharedPreferenceUtils(context);
        }

        public SharedPreferenceUtils saveIsLoggedIn() {
            return saveIsLoggedIn(true);
        }

        public SharedPreferenceUtils saveIsLoggedIn(boolean isLogged) {
            preferences.edit().putBoolean(KEY_LOGGED, isLogged).apply();
            return this;
        }

        public boolean readLoggedIn() {
            return preferences.getBoolean(KEY_LOGGED, false);
        }

        public SharedPreferenceUtils saveUserId(String userId) {
            preferences.edit().putString(KEY_ID, userId).apply();
            return this;
        }

        public SharedPreferenceUtils savePassword(String password) {
            preferences.edit().putString(KEY_PASSWORD, password).apply();
            return this;
        }

        public String readPassword() {
            return preferences.getString(KEY_PASSWORD, "");
        }


        public String readUserId() {
            return preferences.getString(KEY_ID, "");
        }

        public SharedPreferenceUtils saveEmail(String email) {
            preferences.edit().putString(KEY_MAIL, email).apply();
            return this;
        }

        public String readEmail() {
            return preferences.getString(KEY_MAIL, "");
        }

        public String readToken() {
            return preferences.getString(KEY_TOKEN, "");
        }

        public SharedPreferenceUtils saveToken(String token) {
            preferences.edit().putString(KEY_TOKEN, token).apply();
            return this;
        }
    }

//    private static class DateConverterUtils implements JsonDeserializer<Date> {
//
//        private static final String[] DATE_FORMATS = new String[]{
//                StaticValues.DATE_FORMAT,
//                StaticValues.SIMPLE_DATE_FORMAT,
//                StaticValues.DATE_FORMAT_NO_TIME,
//                StaticValues.DATE_FORMAT2
//        };
//
//        @Override
//        public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
//
//            for (String format : DATE_FORMATS) {
//                try {
//                    return new SimpleDateFormat(format, Locale.US).parse(element.getAsString());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//            throw new JsonParseException("Unparseable date: \"" + element.getAsString()
//                    + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
//        }
//    }
}
