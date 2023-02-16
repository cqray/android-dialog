package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 顶部进入
 * @author Cqray
 */
class TopIn : DialogAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.topIn()
    }
}