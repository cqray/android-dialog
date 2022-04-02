package cn.cqray.android.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ButterKnife工具类
 * @author Cqray
 */
public class ButterKnifeUtils {

    /** ButterKnife是否可用 **/
    private static boolean sButterKnifeUsable = true;

    private ButterKnifeUtils() {}

    /**
     * ButterKnife绑定Activity实例
     * @param target Activity实例
     * @return 绑定实例
     */
    @Nullable
    public static Object bind(@NonNull Activity target) {
        if (sButterKnifeUsable) {
            try {
                return ButterKnife.bind(target);
            } catch (Throwable t) {
                checkClassNotFound(t);
                return null;
            }
        }
        return null;
    }

    /**
     * ButterKnife绑定控件
     * @param target 控件
     * @return 绑定实例
     */
    @Nullable
    public static Object bind(@NonNull View target) {
        if (sButterKnifeUsable) {
            try {
                return ButterKnife.bind(target);
            } catch (Throwable t) {
                checkClassNotFound(t);
                return null;
            }
        }
        return null;
    }

    /**
     * ButterKnife绑定对话框
     * @param target 控件
     * @return 绑定实例
     */
    @Nullable
    public static Object bind(@NonNull Dialog target) {
        if (sButterKnifeUsable) {
            try {
                return ButterKnife.bind(target);
            } catch (Throwable t) {
                checkClassNotFound(t);
                return null;
            }
        }
        return null;
    }

    /**
     * ButterKnife绑定控件
     * @param target 目标
     * @param source 来源
     * @return 绑定实例
     */
    @Nullable
    public static Object bind(Object target, @NonNull View source) {
        if (sButterKnifeUsable) {
            try {
                return ButterKnife.bind(target, source);
            } catch (Throwable t) {
                checkClassNotFound(t);
                return null;
            }
        }
        return null;
    }

    /**
     * 解除ButterKnife绑定
     * @param unBinder 绑定实例
     */
    public static void unbind(Object unBinder) {
        if (unBinder == null || !sButterKnifeUsable) {
            return;
        }
        try {
            if (unBinder instanceof Unbinder) {
                ((Unbinder) unBinder).unbind();
            }
        } catch (Throwable t) {
            checkClassNotFound(t);
        }
    }

    /**
     * 检查是否是类没有找到的异常
     * @param t 异常
     */
    private static void checkClassNotFound(Throwable t) {
        if (t instanceof NoClassDefFoundError) {
            sButterKnifeUsable = false;
        }
    }
}
