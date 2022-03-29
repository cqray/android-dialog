package cn.cqray.android.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import cn.cqray.android.code.graphics.RoundDrawable;
import cn.cqray.android.code.lifecycle.SimpleLiveData;
import cn.cqray.android.code.util.SizeUnit;
import cn.cqray.android.code.util.SizeUtils;

/**
 * View控件模块
 * @author Cqray
 */
public class ViewModule<T extends View> {
    /** 圆角数量 **/
    private static final int RADII_LENGTH = 8;
    /** LifeOwner **/
    private LifecycleOwner mLifecycleOwner;
    /** 圆角 **/
    protected final float[] mBackgroundRadii = new float[RADII_LENGTH];
    /** 颜色 **/
    private int mBackgroundColor = Color.WHITE;
    /** 间隔 **/
    protected final SimpleLiveData<float[]> mPadding = new SimpleLiveData<>();
    /** 显示 **/
    protected final SimpleLiveData<Integer> mVisibility = new SimpleLiveData<>();
    /** 高度 **/
    protected final SimpleLiveData<Float> mHeight = new SimpleLiveData<>();
    /** 高度 **/
    protected final SimpleLiveData<Float> mWidth = new SimpleLiveData<>();
    /** 背景 **/
    protected final SimpleLiveData<Drawable> mBackground = new SimpleLiveData<>();
    /** 背景资源 **/
    protected final SimpleLiveData<Integer> mBackgroundResource = new SimpleLiveData<>();

    public ViewModule(LifecycleOwner owner) {
        if (owner instanceof Activity || owner instanceof Fragment) {
            mLifecycleOwner = owner;
            return;
        }
        throw new IllegalArgumentException("LifecycleOwner must implements on FragmentActivity or Fragment.");
    }

    public void observe(LifecycleOwner owner, final T view) {
        observePadding(owner, new Observer<float[]>() {
            @Override
            public void onChanged(float[] ints) {
                view.setPadding(toPix(ints[0]), toPix(ints[1]), toPix(ints[2]), toPix(ints[3]));
            }
        });
        mVisibility.observe(owner, view::setVisibility);
        // 宽度变化监听
        mWidth.observe(owner, aFloat -> {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = aFloat.intValue();
            view.requestLayout();
        });
        // 高度变化监听
        mHeight.observe(owner, aFloat -> {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = aFloat.intValue();
            view.requestLayout();
        });
        mBackground.observe(owner, drawable -> ViewCompat.setBackground(view, drawable));
        mBackgroundResource.observe(owner, integer -> {
            Drawable drawable = ContextCompat.getDrawable(requireContext(), integer);
            requestBackground(drawable);
        });
    }

    public void observeVisibility(LifecycleOwner owner, Observer<Integer> observer) {
        mVisibility.removeObservers(owner);
        mVisibility.observe(owner, observer);
    }

    public void observePadding(LifecycleOwner owner, Observer<float[]> observer) {
        mPadding.removeObservers(owner);
        mPadding.observe(owner, observer);
    }

    public void setPadding(float l, float t, float r, float b) {
        mPadding.postValue(new float[] {l, t, r, b});
    }

    public void setVisibility(int visibility) {
        mVisibility.postValue(visibility);
    }

    public void setWidth(float width) {
        mWidth.postValue(width);
    }

    public void setHeight(float height) {
        mHeight.postValue(height);
    }

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * <p>默认单位DP</p>
     * @param radii 8个值的数组，4对[X，Y]半径
     */
    public void setRadii(float [] radii) {
        setRadii(radii, SizeUnit.DP);
    }

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * @param radii 8个值的数组，4对[X，Y]半径
     * @param unit  值单位
     */
    public void setRadii(float [] radii, SizeUnit unit) {
        if (radii == null || radii.length < RADII_LENGTH) {
            throw new IllegalArgumentException("Radii array length must >= " + RADII_LENGTH);
        }
        for (int i = 0; i < RADII_LENGTH; i++) {
            mBackgroundRadii[i] = SizeUtils.applyDimension(radii[i], unit);
        }
        mBackground.setValue(mBackground.getValue());
    }

    /**
     * 设置圆角大小
     * <p>默认单位DP</p>
     * @param radius 圆角半径
     */
    public void setRadius(float radius) {
        setRadius(radius, SizeUnit.DP);
    }

    /**
     * 设置圆角大小
     * @param radius 圆角半径
     * @param unit 值单位
     */
    public void setRadius(float radius, SizeUnit unit) {
        float [] radii = new float[RADII_LENGTH];
        for (int i = 0; i < RADII_LENGTH; i++) {
            radii[i] = radius;
        }
        setRadii(radii, unit);
    }

    public void setBackground(Drawable drawable) {
        requestBackground(drawable);
    }

    public void setBackgroundColor(int color) {
        requestBackground(new ColorDrawable(color));
    }

    public void setBackgroundColor(String color) {
        requestBackground(new ColorDrawable(Color.parseColor(color)));
    }

    public void setBackgroundResource(@DrawableRes int resId) {
        mBackgroundResource.setValue(resId);
    }

    protected FragmentActivity requireActivity() {
        if (mLifecycleOwner instanceof FragmentActivity) {
            return (FragmentActivity) mLifecycleOwner;
        } else {
            return ((Fragment) mLifecycleOwner).requireActivity();
        }
    }

    protected Context requireContext() {
        return requireActivity();
    }

    protected String getString(@StringRes int resId) {
        return requireContext().getString(resId);
    }

    protected int toPix(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (density * dp + 0.5f);
    }

    private synchronized void requestBackground(Drawable drawable) {
        if (drawable == null) {
            // 不设置背景
            mBackground.setValue(null);
        } else if (drawable instanceof ColorDrawable) {
            // 纯色背景设置圆角
            int color = ((ColorDrawable) drawable).getColor();
            GradientDrawable background = new GradientDrawable();
            background.setColor(color);
            background.setCornerRadii(mBackgroundRadii);
            mBackground.setValue(background);
        } else {
            // 图片背景设置圆角
            RoundDrawable background = new RoundDrawable(drawable);
            background.setRadii(mBackgroundRadii, SizeUnit.PX);
            mBackground.setValue(background);
        }
    }
}
