package cn.cqray.android.dialog.delegate;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.lifecycle.LifecycleOwner;

import cn.cqray.android.code.delegate.ViewDelegate;
import cn.cqray.android.code.lifecycle.SimpleLiveData;

public class PanelDelegate extends ViewDelegate<ViewGroup> {

    /** 面板相关尺寸, 依次为宽度dp值、宽度比例、宽度最小值, 宽度最大值、高度dp值、高度比例值、高度最小值，高度最大值。 **/
    private Float[] mSizeArray = new Float[8];
    /** 对话框位置 **/
    private final SimpleLiveData<Integer> mGravity = new SimpleLiveData<>();
    /** 对话框偏移 **/
    private final SimpleLiveData<float[]> mOffset = new SimpleLiveData<>();
    /** 面板宽、高 **/
    private final SimpleLiveData<int[]> mSize = new SimpleLiveData<>();

    @Override
    public void setView(LifecycleOwner owner, ViewGroup view) {
        super.setView(owner, view);

        mGravity.observe(owner, aInt -> post(() -> {
            ViewGroup parent = (ViewGroup) requireView().getParent();
            if (parent instanceof FrameLayout) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.gravity = aInt;
            } else if (parent instanceof LinearLayout) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                params.gravity = aInt;
            }
        }));
        // 设置偏移位置
        mOffset.observe(owner, floats -> {
            requireView().setTranslationX(floats[0]);
            requireView().setTranslationY(floats[1]);
        });
    }

    protected void requestPanelSize() {
//        NotchScreenManager.getInstance().getNotchInfo(requireActivity(), (INotchScreen.NotchScreenCallback) notchScreenInfo -> {
//            // 设置对话框大小
//            int barHeight = DialogUtils.isFull(requireActivity()) ? 0 : DialogUtils.getStatusBarHeight();
//            if (notchScreenInfo.hasNotch) {
//                barHeight = 0;
//            }
//            int screenWidth = DialogUtils.getWidth();
//            int screenHeight = DialogUtils.getHeight() - barHeight;
//            // 宽高
//            float w, h;
//            // 面板相关尺寸,
//            // 依次为宽度dp值、宽度比例、宽度最小值, 宽度最大值、
//            // 高度dp值、高度比例值、高度最小值，高度最大值。
//            Float widthDp = mSizeArray[0];
//            Float widthScale = mSizeArray[1];
//            Float minWidth = mSizeArray[2];
//            Float maxWidth = mSizeArray[3];
//            Float heightDp = mSizeArray[4];
//            Float heightScale = mSizeArray[5];
//            Float minHeight = mSizeArray[6];
//            Float maxHeight = mSizeArray[7];
//            int[] size = new int[2];
//            // 宽度计算
//            if (widthDp != null) {
//                w = DialogUtils.dp2px(getValue(widthDp, minWidth, maxWidth));
//            } else if (widthScale != null) {
//                w = DialogUtils.px2dp((int) (widthScale * screenWidth));
//                w = DialogUtils.dp2px(getValue(w, minWidth, maxWidth));
//            } else {
//                w = minWidth != null ? DialogUtils.dp2px(minWidth) : -2;
//            }
//            size[0] = (int) w;
//            // 高度计算
//            if (heightDp != null) {
//                h = DialogUtils.dp2px(getValue(heightDp, minHeight, maxHeight));
//            } else if (heightScale != null) {
//                h = DialogUtils.px2dp((int) (heightScale * screenHeight));
//                h = DialogUtils.dp2px(getValue(h, minHeight, maxHeight));
//            } else {
//                h = minHeight != null ? DialogUtils.dp2px(minHeight) : -2;
//            }
//            size[1] = (int) h;
//            mSize.postValue(size);
//        });

    }
}
