package cn.cqray.android.dialog;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
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
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.dialog.amin.BounceIn;
import cn.cqray.android.dialog.amin.BounceOut;
import cn.cqray.android.dialog.amin.DialogAnimator;
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
    protected float mCustomAmountCount = 0f;
    /** 原始遮罩透明度 **/
    protected float mNativeAmountCount = 0.15f;
    /** 自定义遮罩四周间隔 **/
    private MutableLiveData<float []> mCustomDimMargin = new MutableLiveData<>();

    protected final Handler mHandler = new Handler(Looper.getMainLooper());
    protected final PanelModule mPanelModule = new PanelModule(this);
    /** 持有对话框的Fragment **/
    protected final Fragment mOwnerFragment;
    /** 持有对话框的Activity **/
    protected final FragmentActivity mOwnerActivity;
    /** 遮罩动画 **/
    protected final ValueAnimator mDimAnimator = new ValueAnimator();
    /** 对话框显示、消除动画，提示显示、消失动画 **/
    protected final DialogAnimator[] mAnimators = new DialogAnimator[4];
    /** 取消监听 **/
    protected final List<OnCancelListener> mCancelListeners = new ArrayList<>();
    /** 消除监听 **/
    protected final List<OnDismissListener> mDismissListeners = new ArrayList<>();
    /** 是否使用黑色状态栏 **/
    protected final DialogLiveData<Boolean> mBlackStatusBar = new DialogLiveData<>(false);

    public BaseDialog(FragmentActivity activity) {
        mOwnerFragment = null;
        mOwnerActivity = activity;
    }

    public BaseDialog(Fragment fragment) {
        mOwnerFragment = fragment;
        mOwnerActivity = null;
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
            Log.e("数据", "编辑");
        });
        mPanelModule.setRootView(mRootView);
        mPanelModule.observe(this, mPanelView);
        mBlackStatusBar.observe(this, aBoolean -> {
            Window window = requireDialog().getWindow();
            assert window != null;
            boolean portrait = ScreenUtils.isPortrait();
            WindowManager.LayoutParams lp = window.getAttributes();
            // lp.width和lp.height均为-1，则会出现黑色状态栏
            // 竖屏时，宽度设置为屏幕宽度。横屏是设置为-1（考虑到刘海屏，不能直接取宽度）
            lp.width = aBoolean ? -1 : portrait ? ScreenUtils.getScreenWidth() : -1;
            // 锁屏时，高度设置为-1（考虑到刘海屏，不能直接取高度）。横屏是设置为屏幕高度
            lp.height = aBoolean ? -1 : portrait ? -1 : ScreenUtils.getScreenHeight();
            window.setAttributes(lp);
        });
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
                // 面板动画正在进行
                boolean running = mPanelModule.isPanelAnimatorRunning();
                // 任意一个条件满足则不继续
                if (dispatch || running) {
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
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        //mPanelModule.show();
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
        mHandler.post(()-> {
            try {
                super.showNow(fm, getClass().getName());
            } catch (IllegalStateException ignore) {}
        });
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
        //mPanelModule.dismiss();
        //doDimAnimator(animator.getDuration(), show);
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

    /**
     * 显示或者消除对话框
     * @param show 是否显示
     */
    protected void showOrDismiss(boolean show) {
        // 设置是否正在消除对话框
        mDismissing = !show;
        mRootView.setVisibility(View.VISIBLE);
        // 获取对应动画
        DialogAnimator animator;
        if (show) {
            animator = mAnimators[0] == null ? new BounceIn() : mAnimators[0];
        } else {
            animator = mAnimators[1] == null ? new BounceOut() : mAnimators[1];
        }
        // 设置目标对象
        animator.setTarget(mPanelView);
        // 面板动画
        animator.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if (show) {
                    onShow(requireDialog());
                } else {
                    quickDismiss();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        // 开始面板动画
        animator.start();
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
            // 取消正在进行的动画
            mDimAnimator.cancel();
        }
        if (mCustomAmountCount != 0) {
            // 开始值
            int start = show ? 0 : (int) (255 * mCustomAmountCount);
            // 结束值
            int end = show ? (int) (255 * mCustomAmountCount) : 0;
            // 临时时间值
            int tmp = duration <= 0 ? 300 : duration;
            // 设置值
            mDimAnimator.setIntValues(start, end);
            // 进度监听
            mDimAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                String alpha = String.format("%02X", value);
                String color = "#" + alpha + "000000";
                mDimView.setBackgroundColor(Color.parseColor(color));
            });
            // 开始动画
            mDimAnimator.setDuration(tmp).start();
        }
    }

    /**
     * 检查生命周期状态
     */
    protected void checkLifecycleState() {
        if (!getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.CREATED)) {
            throw new IllegalStateException("please call setContentView in onCreating().");
        }
    }

    public void setContentView(View view) {
        checkLifecycleState();
        mPanelView.removeAllViews();
        mPanelView.addView(view);
        mUnBinder = ButterKnifeUtils.bind(this, view);
    }

    public void setContentView(@LayoutRes int layoutResId) {
        checkLifecycleState();
        View view = LayoutInflater.from(requireContext()).inflate(layoutResId, mPanelView, false);
        mPanelView.removeAllViews();
        mPanelView.addView(view);
        mUnBinder = ButterKnifeUtils.bind(this, view);
    }

    public T blackStatusBar(boolean black) {
        mBlackStatusBar.setValue(black);
        return (T) this;
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
        mPanelModule.setRadii(radii);
        return (T) this;
    }

    public T cornerRadii(float [] radii, int unit) {
        mPanelModule.setRadii(radii, unit);
        return (T) this;
    }

    public T cornerRadius(float radius) {
        mPanelModule.setRadius(radius);
        return (T) this;
    }

    public T cornerRadius(float radius, int unit) {
        mPanelModule.setRadius(radius, unit);
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

    public T backgroundResource(@DrawableRes int resId) {
        mPanelModule.setBackgroundResource(resId);
        return (T) this;
    }

    public T gravity(int gravity) {
        mPanelModule.setGravity(gravity);
        return (T) this;
    }

    public T offset(float offsetX, float offsetY) {
        mPanelModule.setOffset(offsetX, offsetY);
        return (T) this;
    }

    public T width(float width) {
        mPanelModule.setWidth(width);
        return (T) this;
    }

    public T width(float width, int unit) {
        mPanelModule.setWidth(width, unit);
        return (T) this;
    }

    public T widthScale(@FloatRange(from = 0, to = 1) float scale) {
        mPanelModule.setWidthScale(scale);
        return (T) this;
    }

    public T widthMin(float min) {
        mPanelModule.setWidthMin(min, TypedValue.COMPLEX_UNIT_DIP);
        return (T) this;
    }

    public T widthMin(float min, int unit) {
        mPanelModule.setWidthMin(min, unit);
        return (T) this;
    }

    public T widthMax(float max) {
        mPanelModule.setWidthMax(max, TypedValue.COMPLEX_UNIT_DIP);
        return (T) this;
    }

    public T widthMax(float max, int unit) {
        mPanelModule.setWidthMax(max, unit);
        return (T) this;
    }

    public T height(float height) {
        mPanelModule.setHeight(height);
        return (T) this;
    }

    public T height(float height, int unit) {
        mPanelModule.setHeight(height, unit);
        return (T) this;
    }

    public T heightScale(@FloatRange(from = 0, to = 1) float scale) {
        mPanelModule.setHeightScale(scale);
        return (T) this;
    }

    public T heightMin(float min) {
        mPanelModule.setHeightMin(min, TypedValue.COMPLEX_UNIT_DIP);
        return (T) this;
    }

    public T heightMin(float min, int unit) {
        mPanelModule.setHeightMin(min, unit);
        return (T) this;
    }

    public T heightMax(float max) {
        mPanelModule.setHeightMax(max, TypedValue.COMPLEX_UNIT_DIP);
        return (T) this;
    }

    public T heightMax(float max, int unit) {
        mPanelModule.setHeightMax(max, unit);
        return (T) this;
    }

    public T showAnimator(DialogAnimator animator) {
        mPanelModule.setShowAnimator(animator);
        return (T) this;
    }

    public T dismissAnimator(DialogAnimator animator) {
        mPanelModule.setDismissAnimator(animator);
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

}
