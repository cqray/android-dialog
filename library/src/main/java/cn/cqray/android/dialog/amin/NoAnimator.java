package cn.cqray.android.dialog.amin;

import androidx.annotation.NonNull;

import cn.cqray.android.anim.AnimatorBuilder;

/**
 * 无动画
 * @author Cqray
 */
public class NoAnimator extends DialogAnimator {

    @Override
    public void onHandle(@NonNull AnimatorBuilder builder) {
        builder.duration(0);
    }
}
