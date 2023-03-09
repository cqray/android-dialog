package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 缩放进入
 * @author Cqray
 */
class ZoomIn : GetAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.zoomIn()
    }
}