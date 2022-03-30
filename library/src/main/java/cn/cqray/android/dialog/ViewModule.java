package cn.cqray.android.dialog;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.Utils;

/**
 * 控件相关操作模块
 * @author Cqray
 */
public class ViewModule<T extends View> {

    /** 圆角数量 **/
    protected static final int RADII_LENGTH = 8;
    /** 圆角 **/
    protected final float[] mBackgroundRadii = new float[RADII_LENGTH];
    /** 间隔 **/
    public final DialogLiveData<float[]> mPadding = new DialogLiveData<>();
    /** 显示 **/
    public final DialogLiveData<Integer> mVisibility = new DialogLiveData<>();
    /** 高度 **/
    public final DialogLiveData<Float> mHeight = new DialogLiveData<>();
    /** 高度 **/
    public final DialogLiveData<Float> mWidth = new DialogLiveData<>();
    /** 背景图 **/
    public final DialogLiveData<Drawable> mBackground = new DialogLiveData<>();
    /** 背景资源 **/
    public final DialogLiveData<Integer> mBackgroundResource = new DialogLiveData<>();

    public void observe(@NonNull LifecycleOwner owner, @NonNull T view) {
        // 控件显示状态监听
        mVisibility.observe(owner, view::setVisibility);
        // 宽度变化监听
        mWidth.observe(owner, aFloat ->  {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(aFloat.intValue(), -2);
            }
            params.width = aFloat.intValue();
            view.setLayoutParams(params);
        });
        // 高度变化监听
        mHeight.observe(owner, aFloat -> {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(-2, aFloat.intValue());
            }
            params.height = aFloat.intValue();
            view.setLayoutParams(params);
        });
        // 设置背景变化监听
        mBackground.observe(owner, drawable -> {
            if (drawable == null) {
                // 不设置背景
                ViewCompat.setBackground(view, null);
            } else if (drawable instanceof ColorDrawable) {
                // 纯色背景设置圆角
                int color = ((ColorDrawable) drawable).getColor();
                GradientDrawable background = new GradientDrawable();
                background.setColor(color);
                background.setCornerRadii(mBackgroundRadii);
                ViewCompat.setBackground(view, background);
            } else {
                // 图片背景设置圆角
                RoundDrawable background = new RoundDrawable(drawable);
                background.setRadii(mBackgroundRadii);
                ViewCompat.setBackground(view, background);
            }
        });
        // 设置背景资源变化监听
        mBackgroundResource.observe(owner, aInt -> {
            Drawable drawable = ContextCompat.getDrawable(Utils.getApp(), aInt);
            mBackground.setValue(drawable);
        });
    }

    public void setPadding(float left, float top, float right, float bottom) {
        setPadding(left, top, right, bottom, TypedValue.COMPLEX_UNIT_DIP);
    }

    public void setPadding(float left, float top, float right, float bottom, int unit) {
        float[] array = new float[4];
        array[0] = SizeUtils.applyDimension(left, unit);
        array[1] = SizeUtils.applyDimension(top, unit);
        array[2] = SizeUtils.applyDimension(right, unit);
        array[3] = SizeUtils.applyDimension(bottom, unit);
        mPadding.setValue(array);
    }

    public void setPadding(float padding) {
        setPadding(padding, TypedValue.COMPLEX_UNIT_DIP);
    }

    public void setPadding(float padding, int unit) {
        float[] array = new float[4];
        for (int i = 0; i < array.length; i++) {
            array[i] = SizeUtils.applyDimension(padding, unit);
        }
        mPadding.setValue(array);
    }

    /**
     * 设置控件宽度
     * <p>默认单位DP</p>
     * @param width 宽度
     */
    public void setWidth(float width) {
        float size = SizeUtils.applyDimension(width, TypedValue.COMPLEX_UNIT_DIP);
        mWidth.setValue(size);
    }

    /**
     * 设置控件宽度
     * @param width 宽度
     * @param unit 值单位
     */
    public void setWidth(float width, int unit) {
        float size = SizeUtils.applyDimension(width, unit);
        mWidth.setValue(size);
    }

    /**
     * 设置控件高度
     * <p>默认单位DP</p>
     * @param height 高度
     */
    public void setHeight(float height) {
        float size = SizeUtils.applyDimension(height, TypedValue.COMPLEX_UNIT_DIP);
        mHeight.setValue(size);
    }

    /**
     * 设置控件高度
     * @param height 高度
     * @param unit 值单位
     */
    public void setHeight(float height, int unit) {
        float size = SizeUtils.applyDimension(height, unit);
        mHeight.setValue(size);
    }

    /**
     * 设置控件显示
     * @param visible 是否显示
     */
    public void setVisible(boolean visible) {
        mVisibility.setValue(visible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 设置控件隐藏
     * @param gone 是否隐藏
     */
    public void setGone(boolean gone) {
        mVisibility.setValue(gone ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置控件显示状态
     * @param visibility 显示状态
     */
    public void setVisibility(int visibility) {
        mVisibility.setValue(visibility);
    }

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * <p>默认单位DP</p>
     * @param radii 8个值的数组，4对[X，Y]半径
     */
    public void setRadii(float [] radii) {
        setRadii(radii, TypedValue.COMPLEX_UNIT_DIP);
    }

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * @param radii 8个值的数组，4对[X，Y]半径
     * @param unit  值单位
     */
    public void setRadii(float [] radii, int unit) {
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
        setRadius(radius, TypedValue.COMPLEX_UNIT_DIP);
    }

    /**
     * 设置圆角大小
     * @param radius 圆角半径
     * @param unit 值单位
     */
    public void setRadius(float radius, int unit) {
        float [] radii = new float[RADII_LENGTH];
        for (int i = 0; i < RADII_LENGTH; i++) {
            radii[i] = radius;
        }
        setRadii(radii, unit);
    }

    public void setBackground(Drawable drawable) {
        mBackground.setValue(drawable);
    }

    public void setBackgroundColor(@ColorInt int color) {
        mBackground.setValue(new ColorDrawable(color));
    }

    public void setBackgroundResource(@DrawableRes int resId) {
        mBackgroundResource.setValue(resId);
    }

}
