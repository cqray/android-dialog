package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 底部进入
 * @author Cqray
 */
class BottomIn : GetAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.bottomIn().alpha(0.4F, 1F, 1F, 1F)
    }
}