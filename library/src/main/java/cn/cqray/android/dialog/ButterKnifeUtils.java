package cn.cqray.android.dialog;

import android.view.View;

import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ButterKnife反射调用工具，主要是为了方便
 * 全局绑定控件，需要加混淆规则。
 * @author Cqray
 */
class ButterKnifeUtils {

    /** 是否支持ButterKnife **/
    private static boolean sSupport = true;
    private static Class<?> sButterKnifeClass;
    private static Class<?> sUnbindClass;
    private static Method sBindObjectViewMethod;
    private static Method sUnbindMethod;

    private ButterKnifeUtils() {}

    @Nullable
    public static Object bind(Object obj, View source) {
        Class<?> btClass = getButterKnifeClass();
        if (btClass != null) {
            try {
                if (sBindObjectViewMethod == null) {
                    sBindObjectViewMethod = sButterKnifeClass.getMethod("bind", Object.class, View.class);
                }
                return sBindObjectViewMethod.invoke(null, obj, source);
            } catch (NoSuchMethodException e) {
                sSupport = false;
            } catch (IllegalAccessException e) {
                sSupport = false;
            } catch (InvocationTargetException e) {
                sSupport = false;
            }
        }
        return null;
    }

    public static void unbind(Object unbinder) {
        if (sSupport && unbinder != null) {
            try {
                if (sUnbindClass == null) {
                    sUnbindClass = Class.forName("butterknife.Unbinder");
                }
                if (sUnbindMethod == null) {
                    sUnbindMethod = sUnbindClass.getMethod("unbind");
                }
                sUnbindMethod.invoke(unbinder);
            } catch (ClassNotFoundException e) {
                sSupport = false;
            } catch (NoSuchMethodException e) {
                sSupport = false;
            } catch (IllegalAccessException e) {
                sSupport = false;
            } catch (InvocationTargetException e) {
                sSupport = false;
            }
        }
    }

    private static Class<?> getButterKnifeClass() {
        if (sSupport) {
            if (sButterKnifeClass == null) {
                try {
                    sButterKnifeClass = Class.forName("butterknife.ButterKnife");
                } catch (ClassNotFoundException e) {
                    sSupport = false;
                }
            }
        }
        return sButterKnifeClass;
    }
}
