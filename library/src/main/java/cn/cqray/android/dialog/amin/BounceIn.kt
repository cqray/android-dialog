package cn.cqray.android.dialog.amin

import cn.cqray.android.anim.ViewAnimator

/**
 * 弹入
 * @author Cqray
 */
class BounceIn : GetAnimator() {
    override fun onAnimatorPrepared(animator: ViewAnimator) {
        animator.bounceIn()
    }
}