package cn.cqray.android.dialog;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import cn.cqray.android.dialog.amin.DialogAnimator;

/**
 * 内部实现Dialog
 * @author Cqray
 */
@SuppressWarnings("unchecked")
public class BaseDialog<T extends BaseDialog<T>> extends DialogInner {

    private View mRootView;
    private View mDimView;
    private View mAnimView;
    private FrameLayout mPanelView;
    private Object mUnBinder;
    private boolean mCancel;
    private boolean mDismissing;
    private final LifecycleOwner mLifecycleOwner;
    protected final DialogModule mDialogModule;
    protected final PanelModule mPanelModule;

    public BaseDialog(FragmentActivity activity) {
        this((LifecycleOwner) activity);
    }

    public BaseDialog(Fragment fragment) {
        this((LifecycleOwner) fragment);
    }

    private BaseDialog(LifecycleOwner owner) {
        if (owner instanceof FragmentActivity || owner instanceof Fragment) {
            mLifecycleOwner = owner;
            mDialogModule = new DialogModule(owner);
            mPanelModule = new PanelModule(owner);
            return;
        }
        throw new IllegalArgumentException("LifecycleOwner must implements on FragmentActivity or Fragment.");
    }

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        mRootView = View.inflate(getContext(), R.layout._dlg_base_layout, null);
        mDimView = mRootView.findViewById(R.id._dialog_dim);
        mAnimView = mRootView.findViewById(R.id._dialog_anim);
        mPanelView = mRootView.findViewById(R.id._dialog_panel);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogModule.isCancelableOutsize()
                        && mDialogModule.isCancelable()
                        && !mDismissing) {
                    mCancel = true;
                    dismiss();
                }
            }
        });
        mDialogModule.observe(this, mRootView);
        mPanelModule.observe(this, mPanelView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnBinder != null) {
            ButterKnifeUtils.unbind(mUnBinder);
        }
    }

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
        Dialog dlg = new Dialog(requireActivity()) {
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
                    mCancel = true;
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

    public void onCancel() {}

    public void onDismiss() {}

    public void onShow() {}

    @Override
    final void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mCancel) {
            onCancel();
            mDialogModule.setState(DialogModule.CANCEL);
        }
        onDismiss();
        mDialogModule.setState(DialogModule.DISMISS);
    }

    public void show() {
        if (mLifecycleOwner instanceof FragmentActivity) {
            show((FragmentActivity) mLifecycleOwner);
        } else {
            show((Fragment) mLifecycleOwner);
        }
    }

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
            throw new IllegalStateException("please call setContentView int onCreating.");
        }
        View view = LayoutInflater.from(requireContext())
                .inflate(layoutResId, mPanelView, false);
        mPanelView.removeAllViews();
        mPanelView.addView(view);
        mUnBinder = ButterKnifeUtils.bind(this, mRootView);
    }

    public void setContentView(View view) {
        if (!getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.CREATED)) {
            throw new IllegalStateException("please call setContentView int onCreating.");
        }
        mPanelView.removeAllViews();
        mPanelView.addView(view);
        mUnBinder = ButterKnifeUtils.bind(this, mRootView);
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
        mPanelModule.setRadii(radii);
        return (T) this;
    }

    public T cornerRadius(@FloatRange(from = 0) float radius) {
        mPanelModule.setRadius(radius);
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

    public T backgroundColor(String color) {
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
        mPanelModule.setGravity(gravity);
        mPanelModule.setOffset(offsetX, offsetY);
        return (T) this;
    }

    public T offset(float offsetX, float offsetY) {
        mPanelModule.setOffset(offsetX, offsetY);
        return (T) this;
    }

    public T width(@FloatRange(from = 0) float width) {
        mPanelModule.setWidth(width);
        return (T) this;
    }

    public T widthScale(@FloatRange(from = 0, to = 1) float scale) {
        mPanelModule.setWidthScale(scale);
        return (T) this;
    }

    public T widthMin(@FloatRange(from = 0) float min) {
        mPanelModule.setWidthMin(min);
        return (T) this;
    }

    public T widthMax(@FloatRange(from = 0) float max) {
        mPanelModule.setWidthMax(max);
        return (T) this;
    }

    public T height(@FloatRange(from = 0) float height) {
        mPanelModule.setHeight(height);
        return (T) this;
    }

    public T heightScale(@FloatRange(from = 0, to = 1) float scale) {
        mPanelModule.setHeightScale(scale);
        return (T) this;
    }

    public T heightMin(@FloatRange(from = 0) float min) {
        mPanelModule.setHeightMin(min);
        return (T) this;
    }

    public T heightMax(@FloatRange(from = 0) float max) {
        mPanelModule.setHeightMax(max);
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

    public T addOnCancelListener(OnCancelListener listener) {
        mDialogModule.addOnCancelListener(listener);
        return (T) this;
    }

    public T addOnDismissListener(OnDismissListener listener) {
        mDialogModule.addOnDismissListener(listener);
        return (T) this;
    }

    public T addOnShowListener(OnShowListener listener) {
        mDialogModule.addOnShowListener(listener);
        return (T) this;
    }

    public <V extends View> V findViewById(@IdRes int resId) {
        if (mRootView != null) {
            return mRootView.findViewById(resId);
        }
        return null;
    }

    protected int getStatusBarHeight() {
        return DialogUtils.isFull(getActivity()) ? 0 : DialogUtils.getStatusBarHeight();
    }

    protected int getNavgationBarHeight() {
        return DialogUtils.isNavigationBarShow(getActivity()) ? DialogUtils.getNavigationBarHeight() : 0;
    }

    private void showOrDismiss(final boolean show) {
        mDismissing = !show;
        mRootView.setVisibility(View.VISIBLE);
        DialogAnimator animator = mDialogModule.getDialogAnimators(show);
        animator.setTarget(mAnimView);
        if (animator.getDuration() <= 0) {
            // 无动画
            if (show) {
                DialogAnimator.reset(mAnimView);
            } else {
                quickDismiss();
            }
        } else {
            // 面板动画
            animator.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!show) {
                        quickDismiss();
                    } else {
                        onShow();
                        mDialogModule.setState(DialogModule.SHOW);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
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
            dimAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    String alpha = String.format("%02X", value);
                    String color = "#" + alpha + "000000";
                    mDimView.setBackgroundColor(Color.parseColor(color));
                }
            });
            dimAnimator.setDuration(duration).start();
        }
    }

    protected int toPix(float dp) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp + 0.5);
    }

}
