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
    protected final DialogModule mDialogModule;
    protected final PanelDelegate mPanelDelegate;
    /** 持有对话框的Fragment **/
    protected final Fragment mOwnerFragment;
    /** 持有对话框的Activity **/
    protected final FragmentActivity mOwnerActivity;
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
            if (mDialogModule.isCancelableOutsize()
                    && mDialogModule.isCancelable()
                    && !mDismissing) {
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
        Dialog dlg = new Dialog(requireActivity(), R.style.DialogFullTheme) {
            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
                if (BaseDialog.this.dispatchTouchEvent(ev) || mDialogModule.isDialogAnimRunning()) {
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }

            @Override
            public void onBackPressed() {
                if (!BaseDialog.this.onBackPressed()
                        && mDialogModule.isCancelable()
                        && !mDismissing) {
                    BaseDialog.this.dismiss();
                }
            }
        };
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setCancelable(false);
        Window window = dlg.getWindow();
        assert window != null;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(mDialogModule.getNativeAmountCount());
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogModule.setWindow(window);
        return dlg;
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

    public void setContentView(@LayoutRes int layoutResId) {
        if (!getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.CREATED)) {
            throw new IllegalStateException("please call setContentView in onCreating().");
        }
        View view = LayoutInflater.from(requireContext())
                .inflate(layoutResId, mPanelView, false);
        mPanelView.removeAllViews();
        mPanelView.addView(view);
        mUnBinder = ButterKnifeUtils.bind(this, view);
    }

    public void setContentView(View view) {
        if (!getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.CREATED)) {
            throw new IllegalStateException("please call setContentView in onCreating().");
        }
        mPanelView.removeAllViews();
        mPanelView.addView(view);
        mUnBinder = ButterKnifeUtils.bind(this, view);
    }

    public T cancelable(boolean cancelable) {
        mDialogModule.setCancelable(cancelable);
        return (T) this;
    }

    public T cancelableOutsize(boolean cancelable) {
        mDialogModule.setCancelableOutsize(cancelable);
        return (T) this;
    }

    public T customDimMargin(float l, float t, float r, float b) {
        mDialogModule.setCustomDimMargin(l, t, r, b);
        return (T) this;
    }

    public T customDimAmount(@FloatRange(from = 0, to = 1) float amount) {
        mDialogModule.setCustomAmountCount(amount);
        return (T) this;
    }

    public T nativeDimAmount(@FloatRange(from = 0, to = 1) float amount) {
        mDialogModule.setNativeAmountCount(amount);
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
        mDialogModule.setDialogAnimator(animator, true);
        return (T) this;
    }

    public T dismissAnimator(DialogAnimator animator) {
        mDialogModule.setDialogAnimator(animator, false);
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
    private void showOrDismiss(final boolean show) {
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
    private void doDimAnimator(int duration, boolean show) {
        // 遮罩动画
        ValueAnimator dimAnimator = mDialogModule.getCustomDimAnimator(show);
        if (dimAnimator != null) {
            dimAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                String alpha = String.format("%02X", value);
                String color = "#" + alpha + "000000";
                mDimView.setBackgroundColor(Color.parseColor(color));
            });
            dimAnimator.setDuration(duration).start();
        }
    }

}
