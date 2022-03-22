package cn.cqray.android.dialog;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.smarx.notchlib.INotchScreen;
import com.smarx.notchlib.NotchScreenManager;

/**
 * 对话框面板相关数据
 * @author Cqray
 */
public class PanelModule extends ViewModule<ViewGroup> {

    /** 面板相关尺寸, 依次为宽度dp值、宽度比例、宽度最小值, 宽度最大值、高度dp值、高度比例值、高度最小值，高度最大值。 **/
    private Float[] mSizeArray = new Float[8];
    /** 对话框位置 **/
    private final MutableLiveData<Integer> mGravity = new MutableLiveData<>();
    /** 对话框偏移 **/
    private final MutableLiveData<float[]> mOffset = new MutableLiveData<>();
    /** 面板宽、高 **/
    private final MutableLiveData<int[]> mSize = new MutableLiveData<>();

    public PanelModule(LifecycleOwner owner) {
        super(owner);
        setWidth(0);
        setBackgroundColor(Color.WHITE);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void observe(LifecycleOwner owner, final ViewGroup view) {
        super.observe(owner, view);
        // 设置偏移位置
        mOffset.observe(owner, new Observer<float[]>() {
            @Override
            public void onChanged(float[] floats) {
                view.setTranslationX(toPix(floats[0]));
                view.setTranslationY(toPix(floats[1]));
            }
        });
        // 设置居中位置
        mGravity.observe(owner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent instanceof FrameLayout) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                    params.gravity = integer;
                } else {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                    params.gravity = integer;
                }
            }
        });
        // 设置面板大小
        mSize.observe(owner, new Observer<int[]>() {
            @Override
            public void onChanged(int[] ints) {
                // requestWindowSize();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if (params.width != ints[0] || params.height != ints[1]) {
                    params.width = ints[0];
                    params.height = ints[1];
                    view.requestLayout();
                }
            }
        });
    }

    public void setGravity(int gravity) {
        mGravity.postValue(gravity);
    }

    public void setOffset(float offsetX, float offsetY) {
        mOffset.postValue(new float[]{offsetX, offsetY});
    }

    @Override
    public void setWidth(float width) {
        mSizeArray[0] = width <= 0 ? null : width;
        mSizeArray[1] = null;
        requestPanelSize();
    }

    public void setWidthScale(float scale) {
        mSizeArray[0] = null;
        mSizeArray[1] = scale <= 0 || scale > 1 ? null : scale;
        requestPanelSize();
    }

    public void setWidthMin(float min) {
        mSizeArray[2] = min <= 0 ? null : min;
        requestPanelSize();
    }

    public void setWidthMax(float max) {
        mSizeArray[3] = max <= 0 ? null : max;
        requestPanelSize();
    }

    @Override
    public void setHeight(float height) {
        mSizeArray[4] = height <= 0 ? null : height;
        mSizeArray[5] = null;
        requestPanelSize();
    }

    public void setHeightScale(float scale) {
        mSizeArray[4] = null;
        mSizeArray[5] = scale <= 0 || scale > 1 ? null : scale;
        requestPanelSize();
    }

    public void setHeightMin(float min) {
        mSizeArray[6] = min <= 0 ? null : min;
        requestPanelSize();
    }

    public void setHeightMax(float max) {
        mSizeArray[7] = max <= 0 ? null : max;
        requestPanelSize();
    }

    protected void requestPanelSize() {
        NotchScreenManager.getInstance().getNotchInfo(requireActivity(), new INotchScreen.NotchScreenCallback() {

            @Override
            public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
                // 设置对话框大小
                int barHeight = DialogUtils.isFull(requireActivity()) ? 0 : DialogUtils.getStatusBarHeight();
                if (notchScreenInfo.hasNotch) {
                    barHeight = 0;
                }
                int screenWidth = DialogUtils.getWidth();
                int screenHeight = DialogUtils.getHeight() - barHeight;
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
                    w = DialogUtils.dp2px(getValue(widthDp, minWidth, maxWidth));
                } else if (widthScale != null) {
                    w = DialogUtils.px2dp((int) (widthScale * screenWidth));
                    w = DialogUtils.dp2px(getValue(w, minWidth, maxWidth));
                } else {
                    w = minWidth != null ? DialogUtils.dp2px(minWidth) : -2;
                }
                size[0] = (int) w;
                // 高度计算
                if (heightDp != null) {
                    h = DialogUtils.dp2px(getValue(heightDp, minHeight, maxHeight));
                } else if (heightScale != null) {
                    h = DialogUtils.px2dp((int) (heightScale * screenHeight));
                    h = DialogUtils.dp2px(getValue(h, minHeight, maxHeight));
                } else {
                    h = minHeight != null ? DialogUtils.dp2px(minHeight) : -2;
                }
                size[1] = (int) h;
                mSize.postValue(size);
            }
        });

    }

    private float getValue(float value, Float min, Float max) {
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
}
