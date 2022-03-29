package cn.cqray.android.dialog;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

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
import androidx.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.code.simple.SimpleAnimatorListener;
import cn.cqray.android.code.util.ButterKnifeUtils;
import cn.cqray.android.code.util.SizeUnit;
import cn.cqray.android.dialog.amin.DialogAnimator;
import cn.cqray.android.dialog.delegate.PanelDelegate;
import cn.cqray.android.dialog.listener.OnCancelListener;
import cn.cqray.android.dialog.listener.OnDismissListener;

/**
 * 内部实现Dialog
 * @author Cqray
 */
@SuppressWarnings("unchecked")
public class BaseDialog<T extends BaseDialog<T>> extends DialogFragment {

    private View mRootView;
    private View mDimView;
    private View mAnimView;
    private FrameLayout mPanelView;
    private Object mUnBinder;
    private boolean mDismissing;

    /** 界面是否能够取消 **/
    protected boolean mCancelable = true;
    /** 点击外部取消 */
    protected boolean mCancelableOutsize = true;
    /** 自定义遮罩透明度 **/
    protected float mCustomAmountCount = 0.15f;
    /** 原始遮罩透明度 **/
    protected float mNativeAmountCount = 0.0f;

    protected final DialogModule mDialogModule;
    protected final PanelDelegate mPanelDelegate;
    /** 持有对话框的Fragment **/
    protected final Fragment mOwnerFragment;
    /** 持有对话框的Activity **/
    protected final FragmentActivity mOwnerActivity;

    /** 遮罩动画 **/
    protected final ValueAnimator mDimAnimator = new ValueAnimator();
    /** 对话框显示、消失动画，提示显示、消失动画 **/
    protected final DialogAnimator[] mAnimators = new DialogAnimator[4];
    /** 取消监听 **/
    protected List<OnCancelListener> mCancelListeners = new ArrayList<>();
    /** 消除监听 **/
    protected List<OnDismissListener> mDismissListeners = new ArrayList<>();

    public BaseDialog(FragmentActivity activity) {
        mOwnerFragment = null;
        mOwnerActivity = activity;
        mDialogModule = new DialogModule(activity);
        mPanelDelegate = new PanelDelegate(activity);
        mPanelDelegate.setBackgroundColor(Color.WHITE);
    }

    public BaseDialog(Fragment fragment) {
        mOwnerFragment = fragment;
        mOwnerActivity = null;
        mDialogModule = new DialogModule(fragment);
        mPanelDelegate = new PanelDelegate(fragment);
        mPanelDelegate.setBackgroundColor(Color.WHITE);
    }

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = View.inflate(getContext(), R.layout._dlg_base_layout, null);
        mDimView = mRootView.findViewById(R.id._dialog_dim);
        mAnimView = mRootView.findViewById(R.id._dialog_anim);
        mPanelView = mRootView.findViewById(R.id._dialog_panel);
        mRootView.setOnClickListener(v -> {
            // 可点击外部取消、可取消、且没有被消除
            if (mCancelableOutsize && mCancelable && !mDismissing) {
                // 消除对话框
                dismiss();
            }
        });
        mDialogModule.observe(this, mRootView);
        mPanelDelegate.setView(mPanelView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnBinder != null) {
            ButterKnifeUtils.unbind(mUnBinder);
        }
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

    public void onShow(@NonNull DialogInterface dialog) {}

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onCreating(savedInstanceState);
        return mRootView;
    }

    public void onCreating(@Nullable Bundle savedInstanceState) {}

    @NonNull
    @Override
    public final Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 初始化对话框
        Dialog dialog = new Dialog(requireActivity(), R.style.DialogFullTheme) {
            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
                // 事件是否被消费掉
                boolean dispatch = BaseDialog.this.dispatchTouchEvent(ev);
                // 是否正在显示（显示）动画
                boolean showing = mAnimators[0] != null && mAnimators[0].isRunning();
                // 是否正在显示（消除）动画
                boolean dismissing = mAnimators[1] != null && mAnimators[1].isRunning();
                // 任意一个条件满足则不继续
                if (dispatch || showing || dismissing) {
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }

            @Override
            public void onBackPressed() {
                // 回退事件是否被拦截
                boolean backPressed = BaseDialog.this.onBackPressed();
                // 如果可以被取消、且没有正在消除对话框、回退事件未被拦截
                if (mCancelable && !mDismissing && !backPressed) {
                    // 消除对话框
                    BaseDialog.this.dismiss();
                }
            }
        };
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        assert window != null;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(mNativeAmountCount);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogModule.setWindow(window);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        showOrDismiss(true);
    }

    public void show() {
        FragmentManager fm;
        if (mOwnerFragment == null) {
            assert mOwnerActivity != null;
            fm = mOwnerActivity.getSupportFragmentManager();
        } else {
            fm = mOwnerFragment.getParentFragmentManager();
        }
        super.show(fm, getClass().getName());
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

    @Override
    public void dismiss() {
        showOrDismiss(false);
    }

    public void quickDismiss() {
        super.dismiss();
    }

    public boolean onBackPressed() {
        return false;
    }

    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        return false;
    }

    protected void checkLifecycleState() {
        if (!getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.CREATED)) {
            throw new IllegalStateException("please call setContentView in onCreating().");
        }
    }

    public void setContentView(@LayoutRes int layoutResId) {
        checkLifecycleState();
        View view = LayoutInflater.from(requireContext())
                .inflate(layoutResId, mPanelView, false);
        mPanelView.removeAllViews();
        mPanelView.addView(view);
        mUnBinder = ButterKnifeUtils.bind(this, view);
    }

    public void setContentView(View view) {
        checkLifecycleState();
        mPanelView.removeAllViews();
        mPanelView.addView(view);
        mUnBinder = ButterKnifeUtils.bind(this, view);
    }

    public T cancelable(boolean cancelable) {
        mCancelable = cancelable;
        return (T) this;
    }

    public T cancelableOutsize(boolean cancelable) {
        mCancelableOutsize = cancelable;
        return (T) this;
    }

    public T customDimMargin(float l, float t, float r, float b) {
        mDialogModule.setCustomDimMargin(l, t, r, b);
        return (T) this;
    }

    public T customDimAmount(@FloatRange(from = 0, to = 1) float amount) {
        mCustomAmountCount = amount;
        return (T) this;
    }

    public T nativeDimAmount(@FloatRange(from = 0, to = 1) float amount) {
        mNativeAmountCount = amount;
        return (T) this;
    }

    public T cornerRadii(float [] radii) {
        mPanelDelegate.setRadii(radii);
        return (T) this;
    }

    public T cornerRadii(float [] radii, SizeUnit unit) {
        mPanelDelegate.setRadii(radii, unit);
        return (T) this;
    }

    public T cornerRadius(float radius) {
        mPanelDelegate.setRadius(radius);
        return (T) this;
    }

    public T cornerRadius(float radius, SizeUnit unit) {
        mPanelDelegate.setRadius(radius, unit);
        return (T) this;
    }

    public T background(Drawable drawable) {
        mPanelDelegate.setBackground(drawable);
        return (T) this;
    }

    public T backgroundColor(int color) {
        mPanelDelegate.setBackgroundColor(color);
        return (T) this;
    }

    public T backgroundResource(@DrawableRes int resId) {
        mPanelDelegate.setBackgroundResource(resId);
        return (T) this;
    }

    public T gravity(int gravity) {
        mPanelDelegate.setGravity(gravity);
        return (T) this;
    }

    public T gravityBottom() {
        return gravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
    }

    public T gravityCenter() {
        return gravity(Gravity.CENTER);
    }

    public T gravityTop() {
        return gravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
    }

    public T gravity(int gravity, float offsetX, float offsetY) {
        mPanelDelegate.setGravity(gravity);
        mPanelDelegate.setOffset(offsetX, offsetY);
        return (T) this;
    }

    public T offset(float offsetX, float offsetY) {
        mPanelDelegate.setOffset(offsetX, offsetY);
        return (T) this;
    }

    public T width(float width) {
        mPanelDelegate.setWidth(width);
        return (T) this;
    }

    public T width(float width, SizeUnit unit) {
        mPanelDelegate.setWidth(width, unit);
        return (T) this;
    }

    public T widthScale(@FloatRange(from = 0, to = 1) float scale) {
        mPanelDelegate.setWidthScale(scale);
        return (T) this;
    }

    public T widthMin(float min) {
        mPanelDelegate.setWidthMin(min, SizeUnit.DP);
        return (T) this;
    }

    public T widthMin(float min, SizeUnit unit) {
        mPanelDelegate.setWidthMin(min, unit);
        return (T) this;
    }

    public T widthMax(float max) {
        mPanelDelegate.setWidthMax(max, SizeUnit.DP);
        return (T) this;
    }

    public T widthMax(float max, SizeUnit unit) {
        mPanelDelegate.setWidthMax(max, unit);
        return (T) this;
    }

    public T height(float height) {
        mPanelDelegate.setHeight(height);
        return (T) this;
    }

    public T height(float height, SizeUnit unit) {
        mPanelDelegate.setHeight(height, unit);
        return (T) this;
    }

    public T heightScale(@FloatRange(from = 0, to = 1) float scale) {
        mPanelDelegate.setHeightScale(scale);
        return (T) this;
    }

    public T heightMin(float min) {
        mPanelDelegate.setHeightMin(min, SizeUnit.DP);
        return (T) this;
    }

    public T heightMin(float min, SizeUnit unit) {
        mPanelDelegate.setHeightMin(min, unit);
        return (T) this;
    }

    public T heightMax(float max) {
        mPanelDelegate.setHeightMax(max, SizeUnit.DP);
        return (T) this;
    }

    public T heightMax(float max, SizeUnit unit) {
        mPanelDelegate.setHeightMax(max, unit);
        return (T) this;
    }

    public T showAnimator(DialogAnimator animator) {
        mAnimators[0] = animator;
        return (T) this;
    }

    public T dismissAnimator(DialogAnimator animator) {
        mAnimators[1] = animator;
        return (T) this;
    }

    public T addCancelListener(OnCancelListener listener) {
        synchronized (BaseDialog.class) {
            mCancelListeners.add(listener);
        }
        return (T) this;
    }

    public T addDismissListener(OnDismissListener listener) {
        synchronized (BaseDialog.class) {
            mDismissListeners.add(listener);
        }
        return (T) this;
    }

    public <V extends View> V findViewById(@IdRes int resId) {
        if (mRootView != null) {
            return mRootView.findViewById(resId);
        }
        return null;
    }

    /**
     * 显示或者消除对话框
     * @param show 是否显示
     */
    protected void showOrDismiss(final boolean show) {
        mDismissing = !show;
        mRootView.setVisibility(View.VISIBLE);
        DialogAnimator animator = mDialogModule.getDialogAnimators(show);
        animator.setTarget(mAnimView);
        if (animator.getDuration() <= 0) {
            // 无动画
            if (show) {
                DialogAnimator.reset(mAnimView);
                onShow(requireDialog());
            } else {
                quickDismiss();
            }
        } else {
            // 面板动画
            animator.addAnimatorListener((SimpleAnimatorListener) animation -> {
                if (show) {
                    onShow(requireDialog());
                } else {
                    quickDismiss();
                }
            });
            animator.start();
        }
        // 执行遮罩动画
        doDimAnimator(animator.getDuration(), show);
    }

    /***
     * 执行遮罩动画
     * @param duration 显示时长
     * @param show true显示对话框，false关闭对话框
     */
    protected void doDimAnimator(int duration, boolean show) {
        if (mDimAnimator.isRunning()) {
            mDimAnimator.cancel();
        }
        if (mCustomAmountCount != 0) {
            int start = show ? 0 : (int) (255 * mCustomAmountCount);
            int end = show ? (int) (255 * mCustomAmountCount) : 0;
            mDimAnimator.setIntValues(start, end);
            mDimAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                String alpha = String.format("%02X", value);
                String color = "#" + alpha + "000000";
                mDimView.setBackgroundColor(Color.parseColor(color));
            });
            mDimAnimator.setDuration(duration).start();
        }
    }
}
