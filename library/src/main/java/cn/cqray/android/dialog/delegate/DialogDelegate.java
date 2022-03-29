package cn.cqray.android.dialog.delegate;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.code.lifecycle.SimpleLiveData;
import cn.cqray.android.dialog.BaseDelegate;
import cn.cqray.android.dialog.listener.OnCancelListener;
import cn.cqray.android.dialog.listener.OnDismissListener;
import cn.cqray.android.dialog.listener.OnShowListener;
import cn.cqray.android.dialog.R;
import cn.cqray.android.dialog.amin.BounceIn;
import cn.cqray.android.dialog.amin.BounceOut;
import cn.cqray.android.dialog.amin.DialogAnimator;

/**
 * 对话框委托
 * @author Cqray
 */
public class DialogDelegate extends BaseDelegate {

    protected static final int CANCEL = 0;
    protected static final int DISMISS = 1;
    protected static final int SHOW = 2;

    private FrameLayout mRootLayout;
    private FrameLayout mPanelLayout;
    private Dialog mInnerDialog;

    /** 是否因为消动作而消除对话框 **/
    protected boolean mDismissByCanceled;
    protected boolean mDismissing;
    protected BaseDialog<?> mDialog;
    protected final PanelDelegate mPanelDelegate;
    /** 对话框显示、消失动画，提示显示、消失动画 **/
    protected DialogAnimator[] mAnimators = new DialogAnimator[4];
    protected final MutableLiveData<View> mContentView = new SimpleLiveData<>();

    protected final SimpleLiveData<Integer> mContentResId = new SimpleLiveData<>();
    protected final SimpleLiveData<Boolean> mCancelable = new SimpleLiveData<>();
    protected final SimpleLiveData<Boolean> mCancelableOutsize = new SimpleLiveData<>();
    protected final SimpleLiveData<Boolean> mBlackStatusBar = new SimpleLiveData<>();
    /** 取消监听 **/
    private List<OnCancelListener> mCancelListeners = new ArrayList<>();
    /** 关闭监听 **/
    private List<OnDismissListener> mDismissListeners = new ArrayList<>();
    /** 显示监听 **/
    private List<OnShowListener> mShowListeners = new ArrayList<>();

    public DialogDelegate(Fragment fragment, BaseDialog<?> dialog) {
        super(fragment);
        mDialog = dialog;
        mPanelDelegate = new PanelDelegate(fragment);
        initialize();
        initPanelDelegate();
    }

    public DialogDelegate(FragmentActivity activity, BaseDialog<?> dialog) {
        super(activity);
        mDialog = dialog;
        mPanelDelegate = new PanelDelegate(activity);
        initialize();
        initPanelDelegate();
    }

    private void initialize() {
        mBlackStatusBar.setValue(false);
    }

    protected void initPanelDelegate() {
        mPanelDelegate.setBackgroundColor(Color.WHITE);
        mPanelDelegate.setRadius(6);
        mPanelDelegate.setGravity(Gravity.CENTER);
    }


    protected void initInternalDialog() {
        mInnerDialog = new Dialog(requireContext(), R.style.DialogFullTheme) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Window window = getWindow();
                assert window != null;
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setDimAmount(0.15f);
                setContentView(R.layout.__dialog_layout);
                initDialogView();
                initLiveData();
                mDialog.onCreating(savedInstanceState);
                showOrDismiss(true);
            }

            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
                if (mDialog.dispatchTouchEvent(ev) || isDialogAnimatorRunning()) {
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }

            @Override
            public void onBackPressed() {
                boolean cancelable = mCancelable.getValue() == null ? true : mCancelable.getValue();
                if (!mDialog.onBackPressed()
                        && cancelable
                        && !mDismissing) {
                    mDismissByCanceled = true;
                    showOrDismiss(false);
                }
            }

            @Override
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                mContentView.removeObservers(getLifecycleOwner());
            }
        };
        mInnerDialog.setOwnerActivity((Activity) requireContext());
    }

    protected void initDialogView() {
        mRootLayout = mInnerDialog.findViewById(R.id.__dialog_root);
        mPanelLayout = mInnerDialog.findViewById(R.id.__dialog_panel);
        mPanelLayout.setVisibility(View.GONE);
        mPanelLayout.postDelayed(() -> mPanelLayout.setVisibility(View.VISIBLE), 50);
        mPanelDelegate.setView(mPanelLayout);
        mRootLayout.setOnClickListener(v -> {
            mDismissByCanceled = true;
            showOrDismiss(false);
        });
    }

    public void setContentView(@LayoutRes int layoutRes) {
        mContentResId.postValue(layoutRes);
    }

    public void setContentView(View view) {
        mContentView.postValue(view);
    }

    public void setCancelable(boolean cancelable) {
        mCancelable.setValue(cancelable);
    }

    public void setCancelableOutsize(boolean cancelable) {
        mCancelableOutsize.setValue(cancelable);
    }

    public void setBlackStatusBar(boolean black) {
        mBlackStatusBar.setValue(black);
    }

    protected void show() {
        initInternalDialog();
        mDismissByCanceled = false;
        mDismissing = false;
        mInnerDialog.show();
    }

    protected void quickDismiss() {
        if (mInnerDialog != null && mInnerDialog.isShowing()) {
            if (mDismissByCanceled) {
                mDialog.onCancel();
                setState(CANCEL);
            }
            mInnerDialog.dismiss();
            mDialog.onDismiss();
            setState(DISMISS);
        }
    }

    protected DialogAnimator getDialogAnimator(boolean show) {
        if (show) {
            return mAnimators[0] == null ? new BounceIn() : mAnimators[0];
        }
        return mAnimators[1] == null ? new BounceOut() : mAnimators[1];
    }

    private void showOrDismiss(final boolean show) {
        //mDismissing = !show;
        //mRootView.setVisibility(View.VISIBLE);
        DialogAnimator animator = getDialogAnimator(show);
        animator.setTarget(mPanelLayout);
        if (animator.getDuration() <= 0) {
            // 无动画
            if (show) {
                DialogAnimator.reset(mPanelLayout);
                setState(SHOW);
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
                        Log.e("数据", "小时");
                        quickDismiss();
                    } else {
                        mDialog.onShow();
                        setState(SHOW);
                        //mDialogModule.setState(DialogModule.SHOW);
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
        //doDimAnimator(animator.getDuration(), show);
    }

    protected void initLiveData() {
        mContentView.observe(getLifecycleOwner(), view -> {
            mPanelLayout.removeAllViews();
            mPanelLayout.addView(view);
        });
        mContentResId.observe(getLifecycleOwner(), aInteger -> {
            View view = LayoutInflater.from(requireContext()).inflate(aInteger, mPanelLayout, false);
            mContentView.setValue(view);
        });
        mCancelable.observe(getLifecycleOwner(), aBoolean -> mInnerDialog.setCancelable(aBoolean));
        mCancelableOutsize.observe(getLifecycleOwner(), aBoolean -> mInnerDialog.setCanceledOnTouchOutside(aBoolean));
        mBlackStatusBar.observe(getLifecycleOwner(), aBoolean -> {
            Window window = mInnerDialog.getWindow();
            assert window != null;
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = -1;
            params.height = aBoolean ? -1 : -2;
            window.setAttributes(params);
        });
    }

    protected void setState(int state) {
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

    public boolean isDialogAnimatorRunning() {
        boolean animTipsShowing = mAnimators[0] != null && mAnimators[0].isRunning();
        boolean animDismissing = mAnimators[1] != null && mAnimators[1].isRunning();
        return animTipsShowing || animDismissing;
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

    public PanelDelegate getPanelDelegate() {
        return mPanelDelegate;
    }
}
