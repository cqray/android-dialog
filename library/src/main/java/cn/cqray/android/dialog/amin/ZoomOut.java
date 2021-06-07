package cn.cqray.android.dialog.amin;

import androidx.annotation.NonNull;

import cn.cqray.android.anim.AnimatorBuilder;

/**
 * 缩放退出
 * @author Cqray
 */
public class ZoomOut extends DialogAnimator {

    @Override
    public void onHandle(@NonNull AnimatorBuilder builder) {
        builder.zoomOut();
    }
}
