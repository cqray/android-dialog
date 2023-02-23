package cn.cqray.android.dialog.amin

import android.view.View
import cn.cqray.android.anim.ViewAnimator
import cn.cqray.android.anim.listener.ViewAnimatorListener
import java.util.concurrent.atomic.AtomicReference

/**
 * 动画框动画
 * @author Cqray
 */
abstract class DialogAnimator {
    /** 目标视图 **/
    private val atomicView = AtomicReference<View>()

    /** 动画 **/
    private val atomicAnimator = AtomicReference<ViewAnimator>()

    /** 监听 **/
    private val listeners = mutableListOf<ViewAnimatorListener>()

    /** 动画是否正在运行 **/
    val isRunning get() = atomicAnimator.get()?.isRunning ?: false

    /** 使用的时间 **/
    val usedTime get() = atomicAnimator.get()?.getUsedTime() ?: 0

    /**
     * 当动画准备完毕
     * @param animator 动画
     */
    abstract fun onAnimatorPrepared(animator: ViewAnimator)

    fun setTarget(view: View) = atomicView.set(view)

    fun addAnimatorListener(listener: ViewAnimatorListener) = listeners.add(listener)

    fun start() {
        if (atomicView.get() == null) return
        atomicAnimator.set(ViewAnimator.with(atomicView.get()).also {
            listeners.forEach { listener -> it.addGlobalListener(listener) }
            it.duration(350L)
            onAnimatorPrepared(it)
            it.start()
        })
    }

    fun cancel() {
        atomicAnimator.get()?.cancel()
        atomicAnimator.set(null)
    }
}