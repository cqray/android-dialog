package cn.cqray.android.dialog.component

import android.animation.Animator
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.anim.listener.ViewAnimatorListener
import cn.cqray.android.dialog.DialogLiveData
import cn.cqray.android.dialog.R
import cn.cqray.android.dialog.amin.BounceIn
import cn.cqray.android.dialog.amin.BounceOut
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
class TipComponent(
    lifecycleOwner: LifecycleOwner,
    viewGet: Function0<TextView>
) : TextViewComponent(
    lifecycleOwner,
    viewGet,
) {

    private val handler = Handler(Looper.getMainLooper())

    private val atomicShown = AtomicBoolean()

    private val atomicDuration = AtomicLong(1800L)

    /** 提示显示、消失动画  */
    private val animators = arrayOf(BounceIn(), BounceOut())

    /** 提示位置信息  */
    private val layoutGravity = DialogLiveData(Gravity.CENTER)

    init {
//        val resources = Utils.getApp().resources
//        val textSize = resources.getDimensionPixelSize(R.dimen.body)
//        val sizeC = resources.getDimensionPixelOffset(R.dimen.content)
//        val sizeS = resources.getDimensionPixelOffset(R.dimen.small)
//        val unit = TypedValue.COMPLEX_UNIT_PX
//        setMargin(sizeS.toFloat(), unit)
//        setPadding(sizeC.toFloat(), sizeS.toFloat(), sizeC.toFloat(), sizeS.toFloat(), unit)

//        setPaddingLR(16F)
//        setPaddingTB(10F)
        setMargin(10F)
        setPadding(16F, 6F, 16F, 6F)
        setBackgroundColor(Color.parseColor("#484848"))
//        setLayoutGravity(Gravity.CENTER)
        setTextColor(Color.WHITE)
        setTextSize(R.dimen.body)
        setGravity(Gravity.CENTER)
        setHeight(-2F)
        setWidth(-2F)
        setGone(true)
        setBackgroundRadius(4F)
        setLayoutGravity(Gravity.CENTER)

        // 订阅位置变化监听
        layoutGravity.observe(lifecycleOwner) {
            //修改Tip位置
            (view.layoutParams as? FrameLayout.LayoutParams)?.let { p ->
                p.gravity = it
                view.requestLayout()
            }
        }

        // 释放Handler
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    handler.removeCallbacksAndMessages(null)
                } else if (event == Lifecycle.Event.ON_CREATE) {
                    show()
                }
            }
        })
    }

    fun setLayoutGravity(gravity: Int) = layoutGravity.setValue(gravity)

    fun setDuration(duration: Long) = setDuration(duration, TimeUnit.MILLISECONDS)

    fun setDuration(duration: Long, timeUnit: TimeUnit) = atomicDuration.set(timeUnit.toMillis(duration))

    fun show() {
        animators[1].cancel()
        if (atomicShown.get()) {
            // 重置取消Tip的时间
            handler.removeMessages(0)
            handler.postDelayed({ dismiss() }, atomicDuration.get())
        } else doTipAnimator(true)
    }

    fun dismiss() = doTipAnimator   (false)

    /**
     * 执行面板动画，返回动画时长
     * @param show 是否是显示动画
     */
    private fun doTipAnimator(show: Boolean) {
        // 清除延时Dismiss任务
        handler.removeMessages(0)
        // 取消相反类型的动画
        animators[if (show) 1 else 0].cancel()
        // 获取对应动画
        val animator = animators[if (show) 0 else 1]
        // 动画没有在运行，才继续操作
        if (!animator.isRunning) {
            // 设置目标对象
            animator.setTarget(view)
            // 动画监听
            animator.addAnimatorListener(object : ViewAnimatorListener {
                override fun onAnimatorStart(view: View?, animation: Animator) {
                    super.onAnimatorStart(view, animation)
                    with(this@TipComponent.view) {
                        visibility = View.VISIBLE
                        // 设置最大宽高
                        val p = parent as View
                        if ((p.width > 0) and (p.height > 0)) {
                            maxWidth = p.width
                            maxHeight = p.height
                        }
                    }
                }

                override fun onAnimatorEnd(view: View?, animation: Animator) {
                    super.onAnimatorEnd(view, animation)
                    if (show) {
                        handler.postDelayed({ dismiss() }, atomicDuration.get())
                    } else {
                        this@TipComponent.view.visibility = View.GONE
                    }
                }
            })
            // 开始面板动画
            animator.start()
        }
    }

}