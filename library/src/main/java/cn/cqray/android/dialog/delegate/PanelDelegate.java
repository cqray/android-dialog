package cn.cqray.android.dialog.delegate;

import android.content.res.Resources;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.smarx.notchlib.NotchScreenManager;

import cn.cqray.android.code.delegate.ViewDelegate;
import cn.cqray.android.code.lifecycle.SimpleLiveData;
import cn.cqray.android.code.util.SizeUnit;
import cn.cqray.android.code.util.SizeUtils;

import cn.cqray.android.dialog.DialogUtils;

/**
 * 对话框面板委托
 * @author Cqray
 */
public class PanelDelegate extends ViewDelegate<ViewGroup> {

    /** 面板相关尺寸, 依次为宽度dp值、宽度比例、宽度最小值, 宽度最大值、高度dp值、高度比例值、高度最小值，高度最大值。 **/
    protected Float[] mSizeArray = new Float[8];
    /** 对话框位置 **/
    protected final SimpleLiveData<Integer> mGravity = new SimpleLiveData<>();
    /** 对话框偏移 **/
    protected final SimpleLiveData<float[]> mOffset = new SimpleLiveData<>();
    /** 面板宽、高 **/
    protected final SimpleLiveData<int[]> mSize = new SimpleLiveData<>();
    /** 面板大小请求 **/
    protected final SimpleLiveData<Object> mRequestSize = new SimpleLiveData<>();

    public PanelDelegate(Fragment fragment) {
        super(fragment);
    }

    public PanelDelegate(FragmentActivity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        // 设置位置情况
        mGravity.observe(owner, aInt -> post(() -> {
            ViewGroup parent = (ViewGroup) requireView().getParent();
            if (parent instanceof FrameLayout) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) requireView().getLayoutParams();
                params.gravity = aInt;
                parent.setLayoutParams(params);
            } else if (parent instanceof LinearLayout) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) requireView().getLayoutParams();
                params.gravity = aInt;
                parent.setLayoutParams(params);
            }
        }));
        // 设置偏移位置监听
        mOffset.observe(owner, floats -> {
            requireView().setTranslationX(floats[0]);
            requireView().setTranslationY(floats[1]);
        });
        // 设置面板大小监听
        mSize.observe(owner, ints -> {
            ViewGroup.LayoutParams params = requireView().getLayoutParams();
            if (params.width != ints[0] || params.height != ints[1]) {
                params.width = ints[0];
                params.height = ints[1];
                requireView().requestLayout();
            }
        });
        // 设置请求面板大小监听
        mRequestSize.observe(owner, o -> requestNewSize());
    }

    public void setGravity(int gravity) {
        mGravity.setValue(gravity);
    }

    public void setOffset(float offsetX, float offsetY) {
        setOffset(offsetX, offsetY, SizeUnit.DP);
    }

    public void setOffset(float offsetX, float offsetY, SizeUnit unit) {
        float [] array = new float[2];
        array[0] = SizeUtils.applyDimension(offsetX, unit);
        array[1] = SizeUtils.applyDimension(offsetY, unit);
        mOffset.setValue(array);
    }

    @Override
    public void setWidth(float width) {
        setWidth(width, SizeUnit.DP);
    }

    @Override
    public void setWidth(float width, SizeUnit unit) {
        synchronized (PanelDelegate.class) {
            mSizeArray[0] = width <= 0 ? null : SizeUtils.applyDimension(width, unit);
            mSizeArray[1] = null;
        }
        mRequestSize.setValue(null);
    }

    public void setWidthScale(float scale) {
        synchronized (PanelDelegate.class) {
            mSizeArray[0] = null;
            mSizeArray[1] = scale <= 0 || scale > 1 ? null : scale;
        }
        mRequestSize.setValue(null);
    }

    public void setWidthMin(float min, SizeUnit unit) {
        synchronized (PanelDelegate.class) {
            mSizeArray[2] = min <= 0 ? null : SizeUtils.applyDimension(min, unit);
        }
        mRequestSize.setValue(null);
    }

    public void setWidthMax(float max, SizeUnit unit) {
        synchronized (PanelDelegate.class) {
            mSizeArray[3] = max <= 0 ? null : SizeUtils.applyDimension(max, unit);
        }
        mRequestSize.setValue(null);
    }

    @Override
    public void setHeight(float height) {
        setHeight(height, SizeUnit.DP);
    }

    @Override
    public void setHeight(float height, SizeUnit unit) {
        synchronized (PanelDelegate.class) {
            mSizeArray[4] = height <= 0 ? null : SizeUtils.applyDimension(height, unit);
            mSizeArray[5] = null;
        }
        mRequestSize.setValue(null);
    }

    public void setHeightScale(float scale) {
        synchronized (PanelDelegate.class) {
            mSizeArray[4] = null;
            mSizeArray[5] = scale <= 0 || scale > 1 ? null : scale;
        }
        mRequestSize.setValue(null);
    }

    public void setHeightMin(float min, SizeUnit unit) {
        synchronized (PanelDelegate.class) {
            mSizeArray[6] = min <= 0 ? null : SizeUtils.applyDimension(min, unit);
        }
        mRequestSize.setValue(null);
    }

    public void setHeightMax(float max, SizeUnit unit) {
        synchronized (PanelDelegate.class) {
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
        // 获取异形屏信息
        NotchScreenManager.getInstance().getNotchInfo(requireActivity(), notchScreenInfo -> {
            // 设置对话框大小
            int barHeight = DialogUtils.isFull(requireActivity()) ? 0 : DialogUtils.getStatusBarHeight();
            if (notchScreenInfo.hasNotch) {
                // 是异形屏，则设置状态栏高度为0
                barHeight = 0;
            }
            int usableWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int usableHeight = Resources.getSystem().getDisplayMetrics().heightPixels - barHeight;
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
