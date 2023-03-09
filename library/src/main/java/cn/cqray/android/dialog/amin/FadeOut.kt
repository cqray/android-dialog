package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 淡出
 * @author Cqray
 */
class FadeOut : GetAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.fadeOut()
    }
}