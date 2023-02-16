package cn.cqray.android.ab

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import cn.cqray.android.dialog.Utils
import cn.cqray.android.dialog.amin.BounceIn
import cn.cqray.android.dialog.amin.BounceOut
import cn.cqray.android.dialog.amin.DialogAnimator
import java.util.concurrent.atomic.AtomicReference

class DialogDelegate(val activity: Activity, val provider: DialogProvider<*>) {

    /** 对话框是否能取消，依次为界面能够取消、点击外部能够取消 **/
    private val cancelable = arrayOf(true, true)

    /** 遮罩透明度，依次为原始遮罩、自定义遮罩 **/
    private val dimAmount = arrayOf(0.2F, 0F)

    /** 对话框显示、消除动画，提示显示、消失动画 **/
    private val animators: Array<DialogAnimator> = arrayOf(BounceIn(), BounceOut())

    /** 对话框实例 **/
    private val atomicDialog = AtomicReference<Dialog>()

    /**
     * 创建对话框
     */
    fun onCreateDialog(): Dialog {
        val dialog = object : Dialog(activity) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                // 事件是否被消费掉
                val dispatch = provider.dispatchTouchEvent(ev)
                // 动画是否正在运行
                val running = animators[0].isRunning || animators[1].isRunning
                // 任意一个条件满足则拦截事件
                return if (dispatch || running) true else super.dispatchTouchEvent(ev)
            }

            override fun onBackPressed() {
                // 如果可以被取消、没有正在关闭面板、回退事件未被拦截
                if (cancelable[0]
                    && !animators[1].isRunning
                    && !provider.onBackPressed()
                ) {
                    // 消除对话框
                    // provider.dismiss()
                }
            }
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.window?.let {
            val width = Utils.getAppScreenWidth(activity)
            val height = Utils.getAppScreenHeight(activity)
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setDimAmount(dimAmount[0])
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            it.decorView.minimumWidth = width
            it.decorView.minimumHeight = height
            it.attributes.let { lp ->
                it.attributes = lp
                it.setGravity(Gravity.TOP)
            }
        }
        atomicDialog.set(dialog)
        return dialog
    }

    /**
     * 快速销毁对话框，无动画
     */
    fun quickDismiss() {
        runCatching {
            if (activity is FragmentActivity) {
//                val fm = activity.supportFragmentManager
//                if (!fm.isStateSaved && !fm.isDestroyed && !isStateSaved) {
//                    super.dismiss()
//                } else super.dismissAllowingStateLoss()
            } else {
                // 消除对话框
                atomicDialog.get()?.dismiss()
            }
        }
    }
}