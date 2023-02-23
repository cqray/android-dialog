package cn.cqray.android.dialog

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog

import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding

import android.view.Gravity
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import cn.cqray.android.anim.listener.ViewAnimatorListener

import cn.cqray.android.dialog.amin.BounceIn
import cn.cqray.android.dialog.amin.BounceOut
import cn.cqray.android.dialog.amin.DialogAnimator

import cn.cqray.android.dialog.databinding.AndroidDlgLayoutBaseBinding
import cn.cqray.java.tool.SizeUnit

@Suppress(
    "MemberVisibilityCanBePrivate",
)
class DialogDelegate(
    val activity: Activity,
    val provider: GetDialogProvider<*>
) {

    /** ButterKnife绑定实例  */
    private var unBinder: Any? = null

    /** 对话框实例 **/
    private var dialog: Dialog? = null

    /** 对话框是否能取消，依次为界面能够取消、点击外部能够取消 **/
    private val cancelable = arrayOf(true, true)

    /** 遮罩透明度，依次为原始遮罩、自定义遮罩 **/
    private val dimAmount = arrayOf(0.2F, 0F)

    /** 遮罩动画  */
    private val dimAnimator = ValueAnimator()

    /** 对话框显示、消除动画，提示显示、消失动画 **/
    private val animators: Array<DialogAnimator> = arrayOf(BounceIn(), BounceOut())

    /** Fragment视图 **/
    private val contentViewLD = DialogLiveData<Any>()

    /** 对话框位置 **/
    private val gravityLD = DialogLiveData(Gravity.CENTER)

    /** 对话框偏移 **/
    private val offsetLD = DialogLiveData(intArrayOf(0, 0))

    /** 生命周期管理注册器 **/
    private val lifecycleRegistry by lazy { LifecycleRegistry(lifecycleOwner) }

    /** 对话框生命周期 **/
    val lifecycleOwner: LifecycleOwner by lazy { LifecycleOwner { lifecycleRegistry } }

    /** [ViewBinding]实例 **/
    val binding by lazy { AndroidDlgLayoutBaseBinding.inflate(activity.layoutInflater) }

    /** 使用的DialogFragment不为空 **/
    val fragment: DialogFragment?

    init {
        // 主线程运行，并保证在Activity.onCreate前可以初始化
        activity.runOnUiThread {
            // 注册配置变化回调
            activity.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(newConfig: Configuration) {
                    provider.onConfigurationChanged(newConfig)
                }

                override fun onLowMemory() {}
            })
        }
        // 初始化Fragment
        fragment = if (activity !is FragmentActivity) {
            // 不是FragmentActivity，则只能单纯的使用Dialog
            null
        } else {
            // FragmentActivity，则使用DialogFragment
            DialogFragment(provider)
        }
    }

    /**
     * 初始化[LiveData]数据
     */
    private fun initLDs() {
        // Fragment视图变化监听
        contentViewLD.observe(lifecycleOwner) {
            val layout = binding.dlgPanel.also { vp ->
                vp.removeAllViews()
                vp.addView(binding.dlgTip)
            }
            when (it) {
                is Int -> View.inflate(activity, it, layout)
                is View -> layout.addView(it)
            }
            // 兼容 ButterKnife
            unBinder = Utils.bindButterKnife(dialog, layout.getChildAt(1))
        }
        // 监听面板位置变化
        gravityLD.observe(lifecycleOwner) { int ->
            val params = binding.dlgLocation.layoutParams as? FrameLayout.LayoutParams
            params?.let { binding.dlgLocation.layoutParams = it.also { it.gravity = int } }
        }
        // 监听对话框位置变化
        offsetLD.observe(lifecycleOwner) {
            val params = binding.dlgLocation.layoutParams as ViewGroup.MarginLayoutParams
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.marginStart = it[0]
            } else {
                params.leftMargin = it[0]
            }
            params.topMargin = it[1]
        }
    }

    /**
     * 创建对话框
     */

    fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 初始化LiveData数据
        initLDs()
        // 创建对话框
        val dialog = object : Dialog(activity) {

            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                provider.onCreating(savedInstanceState)
            }

            override fun onDetachedFromWindow() {
                super.onDetachedFromWindow()
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            }

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
                    this@DialogDelegate.dismiss()
                }
            }
        }
        // 执行面板动画
        dialog.setOnShowListener { doDimAnimator(doPanelAnimator(true), true) }
        // 无标题栏
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 设置点击取消逻辑
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        binding.root.setOnClickListener {
            // 可点击外部取消、可取消、且面板没有被消除
            if (cancelable[0] && cancelable[1] && !animators[1].isRunning) {
                // 消除对话框
                dismiss()
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
        // 使用Dialog的情况
        if (fragment == null) {
            dialog.setContentView(binding.root)
        }
        // 返回对话框
        return dialog.also { this.dialog = it }
    }

    /**
     * 显示对话框
     */
    fun show() {
        if (fragment == null) {
            onCreateDialog(null).show()
        } else if (activity is FragmentActivity) {
            fragment.show(activity.supportFragmentManager, fragment::javaClass.name)
        }
    }

    /**
     * 销毁对话框，有动画
     */
    fun dismiss() = doDimAnimator(doPanelAnimator(false), false)


    /**
     * 快速销毁对话框，无动画
     */
    fun quickDismiss() {
        runCatching {
            if (activity is FragmentActivity && fragment != null) {
                val fm = activity.supportFragmentManager
                if (!fm.isStateSaved && !fm.isDestroyed && !fragment.isStateSaved) {
                    fragment.dismiss()
                } else fragment.dismissAllowingStateLoss()
            } else {
                dialog?.dismiss()
            }
        }
    }

    fun setContentView(view: View) = contentViewLD.setValue(view)

    fun setContentView(@LayoutRes id: Int) = contentViewLD.setValue(id)

    fun setCancelable(cancelable: Boolean) = cancelable.let { this.cancelable[0] = it }

    fun setCancelableOutsize(cancelable: Boolean) = cancelable.let { this.cancelable[1] = it }

    fun setShowAnimator(animator: DialogAnimator) = also { animators[0] = animator }

    fun setDismissAnimator(animator: DialogAnimator) = also { animators[1] = animator }

    /**
     * 设置原生遮罩透明度
     * @param account 透明度
     */
    fun setNativeDimAccount(@FloatRange(from = 0.0, to = 1.0) account: Float) {
        dimAmount[0] = account
        changeDimAccount(account, true)
    }

    /**
     * 设置自定义遮罩透明度
     * @param account 透明度
     */
    fun setCustomDimAccount(@FloatRange(from = 0.0, to = 1.0) account: Float) {
        dimAmount[1] = account
        changeDimAccount(account, false)
    }

    /**
     * 设置对话框位置
     * @param gravity 位置
     */
    fun setGravity(gravity: Int) = gravityLD.setValue(gravity)

    /**
     * 设置偏移，默认单位（DIP）
     * @param offsetX X轴偏移
     * @param offsetY Y轴偏移
     */
    fun setOffset(offsetX: Float, offsetY: Float) = setOffset(offsetX, offsetY, SizeUnit.DIP)

    /**
     * 设置偏移
     * @param offsetX X轴偏移
     * @param offsetY Y轴偏移
     * @param unit 值单位
     */
    @Synchronized
    fun setOffset(offsetX: Float, offsetY: Float, unit: SizeUnit) = also {
        val offsets = offsetLD.value!!
        offsets[0] = Utils.applyDimension(offsetX, unit.type).toInt()
        offsets[1] = Utils.applyDimension(offsetY, unit.type).toInt()
        offsetLD.setValue(offsets)
    }

    /**
     * 执行面板动画，返回动画时长
     * @param show 是否是显示动画
     */
    private fun doPanelAnimator(show: Boolean): Long {
        // 获取对应动画
        val animator = when (show) {
            true -> animators[0]
            else -> animators[1]
        }
        // 动画没有在运行，才继续操作
        if (!animator.isRunning) {
            // 设置目标对象
            animator.setTarget(binding.dlgPanel)
            // 设置监听
            animator.addAnimatorListener(object : ViewAnimatorListener {
                override fun onAnimatorEnd(view: View?, animation: Animator) {
                    if (!show) quickDismiss()
                }
            })
            // 开始面板动画
            animator.start()
        }
        return animator.usedTime
    }

    /***
     * 执行遮罩动画
     * @param duration 显示时长
     * @param show true显示对话框，false关闭对话框
     */
    private fun doDimAnimator(duration: Long, show: Boolean) {
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
                changeDimAccount(
                    animation.animatedValue as Int / 255F,
                    false
                )
            }
            // 开始动画
            dimAnimator.setDuration(tmp).start()
        }
    }

    /**
     * 改变遮罩透明度
     * @param account 透明度
     * @param native 是否是原生遮罩
     */
    private fun changeDimAccount(account: Float, native: Boolean) {
        // 设置遮罩透明度
        val setDimAccount = { b: Boolean ->
            if (b) dialog?.window?.setDimAmount(account)
            else {
                val alpha = String.format("%02X", (account * 255).toInt())
                val color = "#" + alpha + "000000"
                binding.dlgDim.setBackgroundColor(Color.parseColor(color))
            }
        }
        // 主线程运行
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setDimAccount(native)
        } else {
            activity.runOnUiThread { setDimAccount(native) }
        }
    }

}