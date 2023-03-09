package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 缩放退出
 * @author Cqray
 */
class ZoomOut : GetAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.zoomOut()
    }
}