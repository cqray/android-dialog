package cn.cqray.android.dialog;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.SizeUtils;

/**
 * 对话框面板委托
 * @author Cqray
 */
public class PanelModule extends ViewModule<ViewGroup> {

    /** 对话框根界面 **/
    protected View mRootView;
    /** 面板相关尺寸, 依次为宽度dp值、宽度比例、宽度最小值, 宽度最大值、高度dp值、高度比例值、高度最小值，高度最大值。 **/
    protected final Float[] mSizeArray = new Float[8];
    /** 对话框位置 **/
    public final DialogLiveData<Integer> mGravity = new DialogLiveData<>();
    /** 对话框偏移 **/
    public final DialogLiveData<float[]> mOffset = new DialogLiveData<>();
    /** 面板宽、高 **/
    public final DialogLiveData<int[]> mSize = new DialogLiveData<>();
    /** 面板大小请求 **/
    public final DialogLiveData<Object> mRequestSize = new DialogLiveData<>();

    public PanelModule() {
        setBackgroundColor(Color.WHITE);
    }

    public void setRootView(View view) {
        mRootView = view;
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull ViewGroup view) {
        super.observe(owner, view);
        // 设置位置情况
        mGravity.observe(owner, aInt -> {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent instanceof FrameLayout) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.gravity = aInt;
                parent.setLayoutParams(params);
            } else if (parent instanceof LinearLayout) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                params.gravity = aInt;
                parent.setLayoutParams(params);
            }
        });
        // 设置偏移位置监听
        mOffset.observe(owner, floats -> {
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
//            params.leftMargin = (int) floats[0];
//            params.topMargin = (int) floats[1];
            view.setTranslationX(floats[0]);
            view.setTranslationY(floats[1]);
        });
        // 设置面板大小监听
        mSize.observe(owner, ints -> {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params.width != ints[0] || params.height != ints[1]) {
                params.width = ints[0];
                params.height = ints[1];
                view.requestLayout();
            }
        });
        // 设置请求面板大小监听
        mRequestSize.observe(owner, o -> requestNewSize());
    }

    public void setGravity(int gravity) {
        mGravity.setValue(gravity);
    }

    public void setOffset(float offsetX, float offsetY) {
        setOffset(offsetX, offsetY, TypedValue.COMPLEX_UNIT_DIP);
    }

    public void setOffset(float offsetX, float offsetY, int unit) {
        float [] array = new float[2];
        array[0] = SizeUtils.applyDimension(offsetX, unit);
        array[1] = SizeUtils.applyDimension(offsetY, unit);
        mOffset.setValue(array);
    }

    @Override
    public void setWidth(float width) {
        setWidth(width, TypedValue.COMPLEX_UNIT_DIP);
    }

    @Override
    public void setWidth(float width, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[0] = width <= 0 ? null : SizeUtils.applyDimension(width, unit);
            mSizeArray[1] = null;
        }
        mRequestSize.setValue(null);
    }

    public void setWidthScale(float scale) {
        synchronized (PanelModule.class) {
            mSizeArray[0] = null;
            mSizeArray[1] = scale <= 0 || scale > 1 ? null : scale;
        }
        mRequestSize.setValue(null);
    }

    public void setWidthMin(float min, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[2] = min <= 0 ? null : SizeUtils.applyDimension(min, unit);
        }
        mRequestSize.setValue(null);
    }

    public void setWidthMax(float max, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[3] = max <= 0 ? null : SizeUtils.applyDimension(max, unit);
        }
        mRequestSize.setValue(null);
    }

    @Override
    public void setHeight(float height) {
        setHeight(height, TypedValue.COMPLEX_UNIT_DIP);
    }

    @Override
    public void setHeight(float height, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[4] = height <= 0 ? null : SizeUtils.applyDimension(height, unit);
            mSizeArray[5] = null;
        }
        mRequestSize.setValue(null);
    }

    public void setHeightScale(float scale) {
        synchronized (PanelModule.class) {
            mSizeArray[4] = null;
            mSizeArray[5] = scale <= 0 || scale > 1 ? null : scale;
        }
        mRequestSize.setValue(null);
    }

    public void setHeightMin(float min, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[6] = min <= 0 ? null : SizeUtils.applyDimension(min, unit);
        }
        mRequestSize.setValue(null);
    }

    public void setHeightMax(float max, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[7] = max <= 0 ? null : SizeUtils.applyDimension(max, unit);
        }
        mRequestSize.setValue(null);
    }

    protected float getValue(float value, Float min, Float max) {
        float result = value;
        if (min != null) {
            result = Math.max(value, min);
        }
        if (max != null) {
            result = Math.min(result, max);
        }
        if (min != null && max != null && min > max) {
            result = value;
        }
        return result;
    }

    protected void requestNewSize() {
        if (mRootView != null) {
            mRootView.post(() -> {
                // 获取根布局的宽高
                int usableWidth = mRootView.getMeasuredWidth();
                int usableHeight = mRootView.getMeasuredHeight();
                // 宽高
                float w, h;
                // 面板相关尺寸,
                // 依次为宽度dp值、宽度比例、宽度最小值, 宽度最大值、
                // 高度dp值、高度比例值、高度最小值，高度最大值。
                Float widthDp = mSizeArray[0];
                Float widthScale = mSizeArray[1];
                Float minWidth = mSizeArray[2];
                Float maxWidth = mSizeArray[3];
                Float heightDp = mSizeArray[4];
                Float heightScale = mSizeArray[5];
                Float minHeight = mSizeArray[6];
                Float maxHeight = mSizeArray[7];
                int[] size = new int[2];
                // 宽度计算
                if (widthDp != null) {
                    w = getValue(widthDp, minWidth, maxWidth);
                } else if (widthScale != null) {
                    w = widthScale * usableWidth;
                    w = getValue(w, minWidth, maxWidth);
                } else {
                    w = minWidth != null ? minWidth : -2;
                }
                size[0] = (int) w;
                // 高度计算
                if (heightDp != null) {
                    h = getValue(heightDp, minHeight, maxHeight);
                } else if (heightScale != null) {
                    h = heightScale * usableHeight;
                    h = getValue(h, minHeight, maxHeight);
                } else {
                    h = minHeight != null ? minHeight : -2;
                }
                size[1] = (int) h;
                mSize.setValue(size);
            });
        }
    }
}
