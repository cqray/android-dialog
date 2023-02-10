package cn.cqray.android.dialog.module2

import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.dialog.ButterKnifeUtils.bind
import cn.cqray.android.dialog.ButterKnifeUtils.unbind
import cn.cqray.android.dialog.DialogLiveData
import cn.cqray.android.dialog.DialogUtils
import cn.cqray.android.dialog.databinding.AndroidDlgLayoutBaseBinding
import java.util.concurrent.atomic.AtomicReference

/**
 * 对话框委托实现
 * @author Cqray
 */
class DialogDelegate(
    /** 对话框实例  */
    private val mDialog: BaseDialog<*>
) {

    /** 内容视图 **/
    private val atomicContent = AtomicReference<View>()

    /** ButterKnife绑定实例  */
    private val atomicUnBinder = AtomicReference<Any>()

//    /** 界面是否能够取消  */
//    @Setter
//    private val mCancelable = true
//
//    /** 点击外部取消  */
//    @Setter
//    private val mCancelableOutsize = true

    /** 对话框是否能取消，依次为界面能够取消、点击外部能够取消 **/
    private val cancelables = arrayOf(true, true)

    /** 遮罩透明度，依次为原始遮罩、自定义遮罩 **/
    private val dimAmount = arrayOf(0.2F, 0F)

    /** 遮罩动画  */
    private val dimAnimator = ValueAnimator()

    private val dlgContent = DialogLiveData<Any>()

    val context by lazy { mDialog.context!! }

    val baseBinding by lazy { AndroidDlgLayoutBaseBinding.inflate(mDialog.layoutInflater) }

    /** 面板组件 **/
    val panelComponent by lazy { PanelComponent(mDialog, baseBinding.dlgPanel) }

    val cancelable get() = cancelables[0]

    val cancelableOutsize get() = cancelables[1]

    fun observe(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(LifecycleEventObserver { source: LifecycleOwner?, event: Lifecycle.Event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                onCreate()
            } else if (event == Lifecycle.Event.ON_START) {
                onStart()
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                onDestroy()
            }
        })
    }

    init {
        dlgContent.observe(mDialog) {
            val layout = baseBinding.dlgContent.also { v -> v.removeAllViews() }
            when(it) {
                is Int -> View.inflate(context, it, layout)
                is View -> { layout.addView(it) }
            }
            // 缓存视图
            atomicContent.set(layout.getChildAt(0))
            // 兼容 ButterKnife
            atomicUnBinder.set(bind(mDialog, atomicContent.get()))
        }
    }

    private fun onCreate() {
        // 根布局点击事件
        baseBinding.dlgRoot.setOnClickListener {
            // 可点击外部取消、可取消、且面板没有被消除
            if (cancelable && cancelableOutsize && !panelComponent.isDismissing) {
                // 消除对话框
                dismiss()
            }
        }
    }

    private fun onStart() {
        // 执行面板动画
        panelComponent.show { duration: Int ->
            // 执行遮罩动画
            doDimAnimator(duration, true)
        }
    }

    private fun onDestroy() {
        // 取消绑定
        unbind(atomicUnBinder.get())
        // 销毁动画
        if (dimAnimator.isRunning) dimAnimator.cancel()
    }

    fun onCreateDialog(): Dialog {
        // 初始化对话框
        val dialog = object : Dialog(mDialog.requireActivity()) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                // 事件是否被消费掉
                val dispatch = mDialog.dispatchTouchEvent(ev)
                // 面板动画正在进行
                val running = false//panelModule.isPanelAnimatorRunning
                // 任意一个条件满足则不继续
                return if (dispatch || running) {
                    true
                } else super.dispatchTouchEvent(ev)
            }

            override fun onBackPressed() {
                // 回退事件是否被拦截
                val backPressed = mDialog.onBackPressed()
                // 如果可以被取消、且没有正在消除面板、回退事件未被拦截
                if (cancelable && !panelComponent.isDismissing && !backPressed) {
                    // 消除对话框
                    this@DialogDelegate.dismiss()
                }
            }
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.window?.let {
            val width = DialogUtils.getAppScreenWidth(context)
            val height = DialogUtils.getAppScreenHeight(context)
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

    /**
     * 消除面板，并执行遮罩动画
     */
    fun dismiss() = panelComponent.dismiss { doDimAnimator(it, false) }

    fun setContentView(view: View) {
//        checkLifecycleState()
//        // 绑定内容视图
//        baseBinding.dlgPanel.let {
//            it.removeAllViews()
//            it.addView(view)
//            // 缓存视图
//            atomicContent.set(it.getChildAt(0))
//            // 兼容 ButterKnife
//            atomicUnBinder.set(bind(mDialog, atomicContent.get()))
//        }
        dlgContent.value = view
    }

    fun setContentView(@LayoutRes layoutResId: Int) {
        dlgContent.value = layoutResId
//        checkLifecycleState()
//        // 绑定内容视图
//        baseBinding.dlgPanel.let {
//            it.removeAllViews()
//            View.inflate(context, layoutResId, it)
//            // 缓存视图
//            atomicContent.set(it.getChildAt(0))
//            // 兼容 ButterKnife
//            atomicUnBinder.set(bind(mDialog, atomicContent.get()))
//        }
    }

//    fun showTip(tip: String?) {
//        mTipModule.setText(tip)
//        mTipModule.show()
//    }
//
//    fun showTip(tip: String?, duration: Int) {
//        mTipModule.setText(tip)
//        mTipModule.setDuration(duration)
//        mTipModule.show()
//    }

    fun <V : View> findViewById(@IdRes id: Int) = atomicContent.get()?.findViewById<V>(id)!!

    /**
     * 检查生命周期状态
     */
    private fun checkLifecycleState() {
        check(mDialog.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            "Please call setContentView in onCreating()."
        }
    }

    /***
     * 执行遮罩动画
     * @param duration 显示时长
     * @param show true显示对话框，false关闭对话框
     */
    private fun doDimAnimator(duration: Int, show: Boolean) {
        if (dimAnimator.isRunning) {
            // 取消正在进行的动画
            dimAnimator.cancel()
        }
        if (dimAmount[1] > 0F) {
            // 开始值
            val start = if (show) 0 else (255 * dimAmount[1]).toInt()
            // 结束值
            val end = if (show) (255 * dimAmount[1]).toInt() else 0
            // 临时时间值
            val tmp = if (duration <= 0) 300 else duration
            // 设置值
            dimAnimator.setIntValues(start, end)
            // 进度监听
            dimAnimator.addUpdateListener { animation: ValueAnimator ->
                val value = animation.animatedValue as Int
                val alpha = String.format("%02X", value)
                val color = "#" + alpha + "000000"
                baseBinding.dlgDim.setBackgroundColor(Color.parseColor(color))
            }
            // 开始动画
            dimAnimator.setDuration(tmp.toLong()).start()
        }
    }
}