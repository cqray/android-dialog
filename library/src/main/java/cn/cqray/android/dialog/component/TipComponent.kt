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
import cn.cqray.android.dialog.amin.DialogAnimator
import java.util.concurrent.atomic.AtomicLong

class TipComponent(
    lifecycleOwner: LifecycleOwner,
    viewGet: Function0<TextView>
) : TextViewComponent(
    lifecycleOwner,
    viewGet,
) {

    private val handler = Handler(Looper.getMainLooper())

    private val atomicDuration = AtomicLong(1500L)

    /** 提示显示、消失动画  */
    private val animators = arrayOf<DialogAnimator>(BounceIn(), BounceOut())

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
                }
            }
        })
    }

    fun setLayoutGravity(gravity: Int) = layoutGravity.setValue(gravity)

    fun show() {
        val showing = animators[0].isRunning || animators[1].isRunning
        if (showing) {
            handler.removeMessages(0)
            //handler.postDelayed({ dismiss() }, atomicDuration.get())
        } else doTipAnimator(true)
    }

    fun dismiss() = doTipAnimator(false)

    /**
     * 执行面板动画，返回动画时长
     * @param show 是否是显示动画
     */
    private fun doTipAnimator(show: Boolean) {
//        if (getView() != null) {
//            mShowing = show
//            // 清除所有延时任务
//            mHandler.removeCallbacksAndMessages(null)
        // 获取对应动画
        //val animator: DialogAnimator
        val animator = animators[if (show) 0 else 1]
        // 动画没有在运行，才继续操作
        if (!animator.isRunning) {
            // 设置目标对象
            animator.setTarget(view)
            // 动画监听
            animator.addAnimatorListener(object : ViewAnimatorListener {
                override fun onAnimatorStart(view: View?, animation: Animator) {
                    super.onAnimatorStart(view, animation)
                    this@TipComponent.view.visibility = View.VISIBLE
                }

                override fun onAnimatorEnd(view: View?, animation: Animator) {
                    super.onAnimatorEnd(view, animation)
                    if (show) {
                        handler.postDelayed({ dismiss() }, atomicDuration.get())
                    } else {
                        this@TipComponent.view.visibility = View.GONE
                    }
                }
            });
            // 开始面板动画
            animator.start()
        }
//        }
    }

}