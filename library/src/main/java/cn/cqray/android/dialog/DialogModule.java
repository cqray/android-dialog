package cn.cqray.android.dialog;

import android.animation.ValueAnimator;

import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.smarx.notchlib.INotchScreen;
import com.smarx.notchlib.NotchScreenManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.dialog.amin.BounceIn;
import cn.cqray.android.dialog.amin.BounceOut;
import cn.cqray.android.dialog.amin.DialogAnimator;

/**
 * 对话框模块
 * @author Cqray
 */
public class DialogModule extends ViewModule<View> {

    public static final int CANCEL = 0;
    public static final int DISMISS = 1;
    public static final int SHOW = 2;

    @IntDef({CANCEL, DISMISS, SHOW})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DialogState {}

    /** 点击外部取消 */
    private boolean mCancelableOutsize = true;
    /** 界面是否能够取消 **/
    private boolean mCancelable = true;
    /** 对话框显示、消失动画，提示显示、消失动画 **/
    private DialogAnimator[] mAnimators = new DialogAnimator[4];
    /** 遮罩动画 **/
    private ValueAnimator mDimAnimator;
    /** 自定义遮罩透明度 **/
    private float mCustomAmountCount = 0.15f;
    /** 原始遮罩透明度 **/
    private float mNativeAmountCount = 0.0f;
    /** 窗口 **/
    private Window mWindow;
    /** 窗口大小 **/
    private MutableLiveData<Integer> mWindowSize = new MutableLiveData<>();
    /** 自定义遮罩四周间隔 **/
    private MutableLiveData<float []> mCustomDimMargin = new MutableLiveData<>();
    /** 取消监听 **/
    private List<OnCancelListener> mCancelListeners = new ArrayList<>();
    /** 关闭监听 **/
    private List<OnDismissListener> mDismissListeners = new ArrayList<>();
    /** 显示监听 **/
    private List<OnShowListener> mShowListeners = new ArrayList<>();

    public DialogModule(LifecycleOwner owner) {
        super(owner);
    }

    @Override
    public void observe(LifecycleOwner owner, final View view) {
        super.observe(owner, view);
        mWindowSize.observe(owner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (mWindow == null) {
                    return;
                }

                NotchScreenManager.getInstance().getNotchInfo(requireActivity(), new INotchScreen.NotchScreenCallback() {
                    @Override
                    public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
                        int barHeight = DialogUtils.isFull(requireActivity()) ? 0 : DialogUtils.getStatusBarHeight();
                        if (notchScreenInfo.hasNotch) {
                            barHeight = 0;
                        }
                        int screenWidth = DialogUtils.getWidth();
                        int screenHeight = DialogUtils.getHeight() - barHeight;

                        WindowManager.LayoutParams lp = mWindow.getAttributes();
                        lp.width = screenWidth;
                        lp.height = screenHeight;
                        view.getLayoutParams().width = screenWidth;
                        view.getLayoutParams().height = screenHeight;
                        mWindow.setAttributes(lp);
                        view.requestLayout();
                    }
                });
            }
        });
        mCustomDimMargin.observe(owner, new Observer<float[]>() {
            @Override
            public void onChanged(float[] floats) {
                View v = view.findViewById(R.id._dialog_dim);
                if (v == null) {
                    return;
                }
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                params.leftMargin = toPix(floats[0]);
                params.topMargin = toPix(floats[1]);
                params.rightMargin = toPix(floats[2]);
                params.bottomMargin = toPix(floats[3]);
                v.requestLayout();
            }
        });
    }

    public void setState(@DialogState int state) {
        if (state == SHOW) {
            for (OnShowListener listener : mShowListeners) {
                listener.onShow();
            }
        } else if (state == CANCEL) {
            for (OnCancelListener listener : mCancelListeners) {
                listener.onCancel();
            }
        } else if (state == DISMISS) {
            for (OnDismissListener listener : mDismissListeners) {
                listener.onDismiss();
            }
        }
    }

    public void addOnCancelListener(OnCancelListener listener) {
        mCancelListeners.add(listener);
    }

    public void addOnDismissListener(OnDismissListener listener) {
        mDismissListeners.add(listener);
    }

    public void addOnShowListener(OnShowListener listener) {
        mShowListeners.add(listener);
    }

    public void setWindow(Window window) {
        mWindow = window;
        mWindowSize.postValue(0);
    }

    public void setCancelableOutsize(boolean cancelable) {
        mCancelableOutsize = cancelable;
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    public void setDialogAnimator(DialogAnimator animator, boolean show) {
        mAnimators[show ? 0 : 1] = animator;
    }

    public void setCustomDimMargin(float l, float t, float r, float b) {
        mCustomDimMargin.postValue(new float[] {l, t, r, b});
    }

    public void setCustomAmountCount(float amount) {
        mCustomAmountCount = amount;
        if (mWindow != null && Looper.myLooper() == Looper.getMainLooper()) {
            mWindow.setDimAmount(amount);
        }
    }

    public void setNativeAmountCount(float amount) {
        mNativeAmountCount = amount;
    }

    public boolean isCancelableOutsize() {
        return mCancelableOutsize;
    }

    public boolean isCancelable() {
        return mCancelable;
    }

    public boolean isDialogAnimRunning() {
        boolean animTipsShowing = mAnimators[0] != null && mAnimators[0].isRunning();
        boolean animDismissing = mAnimators[1] != null && mAnimators[1].isRunning();
        return animTipsShowing || animDismissing;
    }

    public float getNativeAmountCount() {
        return mNativeAmountCount;
    }

    public DialogAnimator getDialogAnimators(boolean show) {
        if (show) {
            return mAnimators[0] == null ? new BounceIn() : mAnimators[0];
        }
        return mAnimators[1] == null ? new BounceOut() : mAnimators[1];
    }

    @Nullable
    public ValueAnimator getCustomDimAnimator(boolean show) {
        if (mDimAnimator != null) {
            mDimAnimator.cancel();
        }
        if (mCustomAmountCount == 0) {
            return null;
        }
        int start = show ? 0 : (int) (255 * mCustomAmountCount);
        int end = show ? (int) (255 * mCustomAmountCount) : 0;
        mDimAnimator = ValueAnimator.ofInt(start, end);
        return mDimAnimator;
    }
}
