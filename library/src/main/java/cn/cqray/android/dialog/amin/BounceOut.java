package cn.cqray.android.dialog.amin;

import androidx.annotation.NonNull;

import cn.cqray.android.anim.AnimatorBuilder;

/**
 * 弹出
 * @author Cqray
 */
public class BounceOut extends DialogAnimator {

    @Override
    public void onHandle(@NonNull AnimatorBuilder builder) {
        builder.bounceOut();
    }
}
