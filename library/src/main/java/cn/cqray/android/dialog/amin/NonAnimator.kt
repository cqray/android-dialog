package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 无动画
 * @author Cqray
 */
class NonAnimator : GetAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.duration(0)
    }
}