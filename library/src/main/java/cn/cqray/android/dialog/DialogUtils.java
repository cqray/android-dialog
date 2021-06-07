package cn.cqray.android.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

/**
 * 对话框工具类
 * @author Cqray
 */
class DialogUtils {

    private static final String FLYME_OS_4 = "Flyme_OS_4";
    private static final String VERSION_4_4_4 = "4.4.4";

    private static Application sApplication;

    static void init(Application application) {
        if (sApplication == null) {
            sApplication = application;
        }
    }

    static Context getContext() {
        return sApplication.getApplicationContext();
    }

    static int dp2px(float dp) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp + 0.5f);
    }

    static float px2dp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    static int getWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    static int getHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    static int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = Resources.getSystem().getDimensionPixelSize(resourceId);
        }
        if (isFlymeOs4x()) {
            return 2 * statusBarHeight;
        }
        return statusBarHeight;
    }

    static int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        int resourceId = Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = Resources.getSystem().getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }

    static boolean isFull(Activity act) {
        if (act == null || act.getWindow() == null) {
            return false;
        }
        Window window = act.getWindow();
        return (window.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    static boolean isFull(View view) {
        return isFull(getActivityFromView(view));
    }

    static boolean isRtl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
        }
        return false;
    }

    static boolean isNavigationBarShow(Activity activity) {
        if (activity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !menu && !back;
        }
    }

    @Nullable
    private static Activity getActivityFromView(@NonNull View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private static boolean isFlymeOs4x() {
        String sysVersion = android.os.Build.VERSION.RELEASE;
        if (VERSION_4_4_4.equals(sysVersion)) {
            String sysIncrement = android.os.Build.VERSION.INCREMENTAL;
            String displayId = android.os.Build.DISPLAY;
            if (!TextUtils.isEmpty(sysIncrement)) {
                return sysIncrement.contains(FLYME_OS_4);
            } else {
                return displayId.contains(FLYME_OS_4.replace("_", " "));
            }
        }
        return false;
    }

    @Nullable
    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            return (Application) app;
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        } catch (ClassNotFoundException ignored) {}
        return null;
    }
}
