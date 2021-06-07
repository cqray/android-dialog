package cn.cqray.android.dialog.amin;

import android.animation.Animator;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.anim.AnimatorBuilder;
import cn.cqray.android.anim.ViewAnimator;

/**
 * 动画框动画
 * @author Cqray
 */
public abstract class DialogAnimator {

    private ViewAnimator mAnimator;
    private List<Animator.AnimatorListener> mListeners = new ArrayList<>();

    public static void reset(@NonNull View view) {
        view.setAlpha(1);
        view.setScaleX(1);
        view.setScaleY(1);
        view.setTranslationX(0);
        view.setTranslationY(0);
        view.setRotation(0);
        view.setRotationY(0);
        view.setRotationX(0);
    }

    /**
     * 处理AnimatorBuilder
     * @param builder 动画构建器
     */
    public abstract void onHandle(@NonNull AnimatorBuilder builder);

    public void setTarget(View view) {
        mAnimator = ViewAnimator.playOn(view).convert();
    }

    public void addAnimatorListener(Animator.AnimatorListener listener) {
        mListeners.add(listener);
    }

    public void start() {
        AnimatorBuilder builder = mAnimator.getAnimationBuilders().get(0);
        onHandle(builder);
        for (Animator.AnimatorListener listener : mListeners) {
            mAnimator.addAnimatorListener(listener);
        }
        mAnimator.start();
    }

    public void cancel() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    public boolean isRunning() {
        if (mAnimator != null) {
            return mAnimator.isRunning();
        }
        return false;
    }

    public int getDuration() {
        if (mAnimator != null) {
            return mAnimator.getDuration();
        }
        return 0;
    }

}
