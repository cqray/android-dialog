package cn.cqray.android.dialog.module;

import android.animation.Animator;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.Utils;

import cn.cqray.android.anim.AnimatorListener;
import cn.cqray.android.dialog.DialogLiveData;
import cn.cqray.android.dialog.R;
import cn.cqray.android.dialog.amin.BounceIn;
import cn.cqray.android.dialog.amin.BounceOut;
import cn.cqray.android.dialog.amin.DialogAnimator;

/**
 * 消息提示模块
 * @author Cqray
 */
public class TipModule extends TextViewModule {

    /** 正在显示 **/
    private boolean mShowing;
    /** 显示时长 **/
    private int mDuration = 1500;
    /** 提示显示、消失动画 **/
    private final DialogAnimator[] mAnimators = new DialogAnimator[2];
    /** Handler对象 **/
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    /** 提示位置信息 **/
    private final DialogLiveData<Integer> mLayoutGravity = new DialogLiveData<>(Gravity.CENTER);

    public TipModule() {
        Resources resources = Utils.getApp().getResources();
        int textSize = resources.getDimensionPixelSize(R.dimen.body);
        int sizeC = resources.getDimensionPixelOffset(R.dimen.content);
        int sizeS = resources.getDimensionPixelOffset(R.dimen.small);
        int unit = TypedValue.COMPLEX_UNIT_PX;
        setMargin(sizeS, unit);
        setPadding(sizeC, sizeS, sizeC, sizeS, unit);
        setBackgroundColor(Color.parseColor("#484848"));
        setLayoutGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setTextSize(textSize, unit);
        setGravity(Gravity.CENTER);
        setHeight(-2);
        setWidth(-2);
        setGone(true);
        setBackgroundRadius(sizeS / 2f, unit);
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull TextView view) {
        super.observe(owner, view);
        view.post(() -> {
            View parent = (View) view.getParent();
            view.setMaxWidth(parent.getMeasuredWidth());
            view.setMaxHeight(parent.getMeasuredHeight());
        });
        mLayoutGravity.observe(owner, aInt -> {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = aInt;
            view.requestLayout();
        });
        // 销毁资源
        owner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                for (DialogAnimator animator : mAnimators) {
                    if (animator != null && animator.isRunning()) {
                        animator.cancel();
                    }
                }
                mHandler.removeCallbacksAndMessages(null);
            }
        });
    }

    public void setLayoutGravity(int gravity) {
        mLayoutGravity.setValue(gravity);
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setShowAnimator(DialogAnimator animator) {
        mAnimators[0] = animator;
    }

    public void setDismissAnimator(DialogAnimator animator) {
        mAnimators[1] = animator;
    }

    public void show() {
        if (mShowing) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(this::dismiss, mDuration);
        } else {
            doTipAnimator(true);
        }
    }

    public void dismiss() {
        doTipAnimator(false);
    }

    /**
     * 执行面板动画，返回动画时长
     * @param show 是否是显示动画
     */
    private void doTipAnimator(boolean show) {
        if (getView() != null) {
            mShowing = show;
            // 清除所有延时任务
            mHandler.removeCallbacksAndMessages(null);
            // 获取对应动画
            DialogAnimator animator;
            if (show) {
                animator = mAnimators[0] == null ? new BounceIn() : mAnimators[0];
            } else {
                animator = mAnimators[1] == null ? new BounceOut() : mAnimators[1];
            }
            // 动画没有在运行，才继续操作
            if (!animator.isRunning()) {
                // 设置目标对象
                animator.setTarget(getView());
                // 动画监听
                animator.addAnimatorListener(new AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation, boolean isReverse) {
                        getView().setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (show) {
                            mHandler.postDelayed(() -> dismiss(), mDuration);
                        }
                    }
                });
                // 开始面板动画
                animator.start();
            }
        }
    }
}
