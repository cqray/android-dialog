package cn.cqray.android.dialog.module2

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import cn.cqray.android.dialog.DialogUtils

class DialogDelegate3(val provider: DialogProvider) {

    /** 对话框是否能取消，依次为界面能够取消、点击外部能够取消 **/
    private val cancelables = arrayOf(true, true)

    /** 遮罩透明度，依次为原始遮罩、自定义遮罩 **/
    private val dimAmount = arrayOf(0.2F, 0F)

    fun onCreateDialog(): Dialog {
        val activity = provider.requireActivity()
        // 初始化对话框
        val dialog = object : Dialog(activity) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                Log.e("数据", "onCreate()")
            }

            override fun onStart() {
                super.onStart()
                Log.e("数据", "onStart()")
            }

            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                return super.dispatchTouchEvent(ev)
//                // 事件是否被消费掉
//                val dispatch = provider.dispatchTouchEvent(ev)
//                // 面板动画正在进行
//                val running = false//panelModule.isPanelAnimatorRunning
//                // 任意一个条件满足则不继续
//                return if (dispatch || running) {
//                    true
//                } else super.dispatchTouchEvent(ev)
            }

            override fun onBackPressed() {
                super.onBackPressed()
                // 回退事件是否被拦截
                val backPressed = provider.onBackPressed()
                // 如果可以被取消、且没有正在消除面板、回退事件未被拦截
//                if (cancelables[0] && !panelComponent.isDismissing && !backPressed) {
//                    // 消除对话框
//                    this@DialogDelegate3.dismiss()
//                }
            }
        }
//        dialog.setOnShowListener {
//            Log.e("数据", "对话框显示")
//        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.setCancelable(false)
        dialog.window?.let {
            val width = DialogUtils.getAppScreenWidth(activity)
            val height = DialogUtils.getAppScreenHeight(activity)
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
        return dialog
    }

    fun dismiss() {

    }
}