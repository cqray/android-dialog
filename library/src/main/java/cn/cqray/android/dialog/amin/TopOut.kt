package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 顶部退出
 * @author Cqray
 */
class TopOut : DialogAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.topOut()
    }
}