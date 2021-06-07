package cn.cqray.android.dialog.amin;

import androidx.annotation.NonNull;

import cn.cqray.android.anim.AnimatorBuilder;

/**
 * 弹入
 * @author Cqray
 */
public class BounceIn extends DialogAnimator {
    @Override
    public void onHandle(@NonNull AnimatorBuilder builder) {
        builder.bounceIn();
    }
}
