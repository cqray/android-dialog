package cn.cqray.android.dialog.amin;

import androidx.annotation.NonNull;

import cn.cqray.android.anim.AnimatorBuilder;

/**
 * 底部滑出
 * @author Cqray
 */
public class SlideBottomOut extends DialogAnimator {

    @Override
    public void onHandle(@NonNull AnimatorBuilder builder) {
        builder.slideBottomOut();
    }
}
