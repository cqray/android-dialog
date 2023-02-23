package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 底部退出
 * @author Cqray
 */
class BottomOut : DialogAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.bottomOut().alpha(1F, 1F, 1F, 0F)
    }
}