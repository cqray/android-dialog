package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 底部进入
 * @author Cqray
 */
class BottomIn : DialogAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.bottomIn()
    }
}