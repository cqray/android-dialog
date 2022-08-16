package cn.cqray.android.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.dialog.amin.DialogAnimator;
import cn.cqray.android.dialog.listener.OnCancelListener;
import cn.cqray.android.dialog.listener.OnDismissListener;
import cn.cqray.android.dialog.listener.OnShowListener;
import cn.cqray.android.dialog.module.PanelModule;
import cn.cqray.android.dialog.module.TipModule;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 内部实现Dialog
 * @author Cqray
 */
@SuppressWarnings("unchecked")
@Accessors(prefix = "m")
public class BaseDialog<T extends BaseDialog<T>> extends DialogFragment {

    /** 持有对话框的Fragment **/
    protected final Fragment mOwnerFragment;
    /** 持有对话框的Activity **/
    protected final FragmentActivity mOwnerActivity;
    /** 取消监听 **/
    protected final List<OnCancelListener> mCancelListeners = new ArrayList<>();
    /** 消除监听 **/
    protected final List<OnDismissListener> mDismissListeners = new ArrayList<>();
    /** 显示监听 **/
    protected final List<OnShowListener> mShowListeners = new ArrayList<>();
    /** 提示模块 **/
    protected @Getter final TipModule mTipModule;
    /** 面板模块 **/
    protected @Getter final PanelModule mPanelModule;
    /** 对话框委托 **/
    protected @Getter final DialogDelegate mDelegate;

    public BaseDialog(FragmentActivity activity) {
        mOwnerActivity = activity;
        mOwnerFragment = null;
        mDelegate = new DialogDelegate(this);
        mTipModule = mDelegate.getTipModule();
        mPanelModule = mDelegate.getPanelModule();
    }

    public BaseDialog(Fragment fragment) {
        mOwnerActivity = null;
        mOwnerFragment = fragment;
        mDelegate = new DialogDelegate(this);
        mTipModule = mDelegate.getTipModule();
        mPanelModule = mDelegate.getPanelModule();
    }

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.observe(this);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        for (OnCancelListener listener : mCancelListeners) {
            listener.onCancel();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        for (OnDismissListener listener : mDismissListeners) {
            listener.onDismiss();
        }
    }

    public void onShow(@NonNull DialogInterface dialog) {
        for (OnShowListener listener : mShowListeners) {
            listener.onShow();
        }
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onCreating(savedInstanceState);
        return mDelegate.getRootView();
    }

    public void onCreating(@Nullable Bundle savedInstanceState) {}

    @NonNull
    @Override
    public final Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return mDelegate.onCreateDialog();
    }

    public void show() {
        FragmentManager fm;
        Activity act;
        if (mOwnerFragment == null) {
            assert mOwnerActivity != null;
            fm = mOwnerActivity.getSupportFragmentManager();
            act = mOwnerActivity;
        } else {
            fm = mOwnerFragment.getParentFragmentManager();
            act = mOwnerFragment.requireActivity();
        }
        act.runOnUiThread(() -> {
            try {
                super.showNow(fm, getClass().getName());
            } catch (IllegalStateException ignore) {}
        });
    }

    @Deprecated
    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {}

    @Deprecated
    @Override
    public void showNow(@NonNull FragmentManager manager, @Nullable String tag) {}

    @Deprecated
    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        return 0;
    }

    @Deprecated
    @Override
    public void setShowsDialog(boolean showsDialog) {}

    public LifecycleOwner getParentLifecycleOwner() {
        return mOwnerFragment == null ? mOwnerActivity : mOwnerFragment;
    }

    @Override
    public void dismiss() {
        mDelegate.dismiss();
    }

    /**
     * 快速消除，无视动画。本质是原始的消除方式
     */
    public void quickDismiss() {
        try {
            FragmentManager fm = getParentFragmentManager();
            if (!fm.isStateSaved() && !fm.isDestroyed() && !isStateSaved()) {
                // 保证Fragment及其父类状态存活
                super.dismiss();
            } else {
                onDestroyView();
            }
        } catch (IllegalStateException ignore) {
            onDestroyView();
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        return false;
    }

    public void setContentView(View view) {
        mDelegate.setContentView(view);
    }

    public void setContentView(@LayoutRes int layoutResId) {
        mDelegate.setContentView(layoutResId);
    }

    public T blackStatusBar(boolean black) {
        mDelegate.setBlackStatusBar(black);
        return (T) this;
    }

    public T cancelable(boolean cancelable) {
        mDelegate.setCancelable(cancelable);
        return (T) this;
    }

    public T cancelableOutsize(boolean cancelable) {
        mDelegate.setCancelableOutsize(cancelable);
        return (T) this;
    }

    public T customDimMargin(float l, float t, float r, float b) {

        return (T) this;
    }

    public T customDimAmount(@FloatRange(from = 0, to = 1) float amount) {
        mDelegate.setCustomAmountCount(amount);
        return (T) this;
    }

    public T nativeDimAmount(@FloatRange(from = 0, to = 1) float amount) {
        mDelegate.setNativeAmountCount(amount);
        return (T) this;
    }

    public T cornerRadii(float [] radii) {
        mPanelModule.setBackgroundRadii(radii);
        return (T) this;
    }

    public T cornerRadii(float [] radii, int unit) {
        mPanelModule.setBackgroundRadii(radii, unit);
        return (T) this;
    }

    public T cornerRadius(float radius) {
        mPanelModule.setBackgroundRadius(radius);
        return (T) this;
    }

    public T cornerRadius(float radius, int unit) {
        mPanelModule.setBackgroundRadius(radius, unit);
        return (T) this;
    }

    public T background(Drawable drawable) {
        mPanelModule.setBackground(drawable);
        return (T) this;
    }

    public T backgroundColor(int color) {
        mPanelModule.setBackgroundColor(color);
        return (T) this;
    }

    public T backgroundResource(@DrawableRes int resId) {
        mPanelModule.setBackgroundResource(resId);
        return (T) this;
    }

    public T gravity(int gravity) {
        mPanelModule.setGravity(gravity);
        return (T) this;
    }

    public T offset(float offsetX, float offsetY) {
        mPanelModule.setOffset(offsetX, offsetY);
        return (T) this;
    }

    public T width(float width) {
        mPanelModule.setWidth(width);
        return (T) this;
    }

    public T width(float width, int unit) {
        mPanelModule.setWidth(width, unit);
        return (T) this;
    }

    public T widthScale(@FloatRange(from = 0, to = 1) float scale) {
        mPanelModule.setWidthScale(scale);
        return (T) this;
    }

    public T widthMin(float min) {
        mPanelModule.setWidthMin(min, TypedValue.COMPLEX_UNIT_DIP);
        return (T) this;
    }

    public T widthMin(float min, int unit) {
        mPanelModule.setWidthMin(min, unit);
        return (T) this;
    }

    public T widthMax(float max) {
        mPanelModule.setWidthMax(max, TypedValue.COMPLEX_UNIT_DIP);
        return (T) this;
    }

    public T widthMax(float max, int unit) {
        mPanelModule.setWidthMax(max, unit);
        return (T) this;
    }

    public T height(float height) {
        mPanelModule.setHeight(height);
        return (T) this;
    }

    public T height(float height, int unit) {
        mPanelModule.setHeight(height, unit);
        return (T) this;
    }

    public T heightScale(@FloatRange(from = 0, to = 1) float scale) {
        mPanelModule.setHeightScale(scale);
        return (T) this;
    }

    public T heightMin(float min) {
        mPanelModule.setHeightMin(min, TypedValue.COMPLEX_UNIT_DIP);
        return (T) this;
    }

    public T heightMin(float min, int unit) {
        mPanelModule.setHeightMin(min, unit);
        return (T) this;
    }

    public T heightMax(float max) {
        mPanelModule.setHeightMax(max, TypedValue.COMPLEX_UNIT_DIP);
        return (T) this;
    }

    public T heightMax(float max, int unit) {
        mPanelModule.setHeightMax(max, unit);
        return (T) this;
    }

    public T showAnimator(DialogAnimator animator) {
        mPanelModule.setShowAnimator(animator);
        return (T) this;
    }

    public T dismissAnimator(DialogAnimator animator) {
        mPanelModule.setDismissAnimator(animator);
        return (T) this;
    }

    public T addOnCancelListener(OnCancelListener listener) {
        synchronized (BaseDialog.class) {
            mCancelListeners.add(listener);
        }
        return (T) this;
    }

    public T addOnDismissListener(OnDismissListener listener) {
        synchronized (BaseDialog.class) {
            mDismissListeners.add(listener);
        }
        return (T) this;
    }

    public T addOnShowListener(OnShowListener listener) {
        synchronized (BaseDialog.class) {
            mShowListeners.add(listener);
        }
        return (T) this;
    }

    public <V extends View> V findViewById(@IdRes int resId) {
        return mDelegate.findViewById(resId);
    }

    public void showTip(String tip) {
        mDelegate.showTip(tip);
    }

    public void showTip(String tip, int duration) {
        mDelegate.showTip(tip, duration);
    }
}
