package cn.cqray.android.dialog.amin;

import androidx.annotation.NonNull;

import cn.cqray.android.anim.AnimatorBuilder;

/**
 * 淡出
 * @author Cqray
 */
public class FadeOut extends DialogAnimator {

    @Override
    public void onHandle(@NonNull AnimatorBuilder builder) {
        builder.fadeOut();
    }
}
