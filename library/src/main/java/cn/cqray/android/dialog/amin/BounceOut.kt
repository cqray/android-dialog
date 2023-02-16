package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 弹出
 * @author Cqray
 */
class BounceOut : DialogAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.bounceOut()
    }
}