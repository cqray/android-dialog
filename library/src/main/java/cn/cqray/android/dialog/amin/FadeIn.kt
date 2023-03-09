package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 淡入
 * @author Cqray
 */
class FadeIn : GetAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.fadeIn()
    }

}