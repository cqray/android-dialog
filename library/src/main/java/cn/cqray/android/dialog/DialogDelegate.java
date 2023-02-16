package cn.cqray.android.dialog;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ScreenUtils;

import cn.cqray.android.dialog.module.PanelModule;
import cn.cqray.android.dialog.module.TipModule;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 对话框委托实现
 * @author Cqray
 */
@Accessors(prefix = "m")
public final class DialogDelegate {

    /** 内容布局 **/
    @Getter
    private View mContentView;
    /** 遮罩布局 **/
    private View mDimView;
    /** 根布局 **/
    @Getter
    private FrameLayout mRootView;
    /** 面板控件 **/
    private FrameLayout mPanelLayout;
    /** ButterKnife绑定实例 **/
    private Object mUnBinder;
    /** 界面是否能够取消 **/
    @Setter
    private boolean mCancelable = true;
    /** 点击外部取消 */
    @Setter
    private boolean mCancelableOutsize = true;
    /** 自定义遮罩透明度 **/
    @Setter
    private float mCustomAmountCount = 0f;
    /** 原始遮罩透明度 **/
    @Setter
    private float mNativeAmountCount = 0.15f;
    /** 遮罩动画 **/
    private final ValueAnimator mDimAnimator = new ValueAnimator();
    /** 是否使用黑色状态栏 **/
    private final DialogLiveData<Boolean> mBlackStatusBar = new DialogLiveData<>(false);
    /** 对话框实例 **/
    private final BaseDialog<?> mDialog;
    /** 提示模块实现 **/
    @Getter
    private final TipModule mTipModule;
    /** 面板实现模块 **/
    @Getter
    private PanelModule mPanelModule;


    public DialogDelegate(BaseDialog<?> dialog) {
        mDialog = dialog;
        mTipModule = new TipModule();
//        mPanelModule = new PanelModule(dialog);
    }

    public void observe(@NonNull LifecycleOwner owner) {
        owner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_CREATE) {
                onCreate();
            } else if (event == Lifecycle.Event.ON_START) {
                onStart();
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                onDestroy();
            }
        });
    }

    private void onCreate() {
        Context context = mDialog.requireContext();
        // 根布局
        mRootView = new FrameLayout(context);
        mRootView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mRootView.setClipChildren(false);
        mRootView.setClipToPadding(false);
        mRootView.setOnClickListener(v -> {
            // 可点击外部取消、可取消、且面板没有被消除
            if (mCancelableOutsize && mCancelable && !mPanelModule.isDismissing()) {
                // 消除对话框
                dismiss();
            }
        });
        // 面板布局
        mPanelLayout = new FrameLayout(context);
        mPanelLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mPanelLayout.setClickable(true);
        mPanelLayout.setFocusable(true);
        // 遮罩布局
        mDimView = new View(context);
        mDimView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        // 提示控件
        TextView tipView = new TextView(context);
        // 位置布局
        FrameLayout siteLayout = new FrameLayout(context);
        siteLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        siteLayout.addView(mPanelLayout);
        siteLayout.addView(tipView);
        // 添加布局
        mRootView.addView(mDimView);
        mRootView.addView(siteLayout);

        mTipModule.observe(mDialog, tipView);
        mPanelModule.setRootView(mRootView);
        mPanelModule.observe(mDialog, mPanelLayout);
        // 是否显示黑色状态栏监听
        mBlackStatusBar.observe(mDialog, aBoolean -> {
            Window window = mDialog.requireDialog().getWindow();
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

    private void onStart() {
//        // 执行面板动画
//        mPanelModule.show(duration -> {
//            // 执行遮罩动画
//            doDimAnimator(duration, true);
//        });
    }

    private void onDestroy() {
        // 取消绑定
        if (mUnBinder != null) {
            ButterKnifeUtils.unbind(mUnBinder);
        }
        // 销毁动画
        if (mDimAnimator.isRunning()) {
            mDimAnimator.cancel();
        }
    }

    @NonNull
    public final Dialog onCreateDialog() {
        // 初始化对话框
        Dialog dialog = new Dialog(mDialog.requireActivity()) {
            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
                // 事件是否被消费掉
                boolean dispatch = mDialog.dispatchTouchEvent(ev);
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
                boolean backPressed = mDialog.onBackPressed();
                // 如果可以被取消、且没有正在消除面板、回退事件未被拦截
                if (mCancelable && !mPanelModule.isDismissing() && !backPressed) {
                    // 消除对话框
                    DialogDelegate.this.dismiss();
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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        return dialog;
    }

    /**
     * 消除对话框
     */
    public void dismiss() {
//        // 消除面板
//        mPanelModule.dismiss(duration -> {
//            // 执行遮罩动画
//            doDimAnimator(duration, false);
//        });
    }

    public void setContentView(View view) {
        checkLifecycleState();
        mPanelLayout.removeAllViews();
        mPanelLayout.addView(view);
        mContentView = view;
        mUnBinder = ButterKnifeUtils.bind(mDialog, view);
    }

    public void setContentView(@LayoutRes int layoutResId) {
        checkLifecycleState();
        View view = LayoutInflater.from(mDialog.requireContext()).inflate(layoutResId, mPanelLayout, false);
        mPanelLayout.removeAllViews();
        mPanelLayout.addView(view);
        mContentView = view;
        mUnBinder = ButterKnifeUtils.bind(mDialog, view);
    }

    public void setBlackStatusBar(boolean black) {
        mBlackStatusBar.setValue(black);
    }

    public void showTip(String tip) {
        mTipModule.setText(tip);
        mTipModule.show();
    }

    public void showTip(String tip, int duration) {
        mTipModule.setText(tip);
        mTipModule.setDuration(duration);
        mTipModule.show();
    }

   public <V extends View> V findViewById(@IdRes int resId) {
        if (mRootView != null) {
            return mRootView.findViewById(resId);
        }
        return null;
    }

    /**
     * 检查生命周期状态
     */
    private void checkLifecycleState() {
        if (!mDialog.getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.CREATED)) {
            throw new IllegalStateException("Please call setContentView in onCreating().");
        }
    }

    /***
     * 执行遮罩动画
     * @param duration 显示时长
     * @param show true显示对话框，false关闭对话框
     */
    private void doDimAnimator(int duration, boolean show) {
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

}
