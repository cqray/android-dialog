package cn.cqray.android.ab

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import cn.cqray.android.anim.AnimatorListener
import cn.cqray.android.anim.ViewAnimator
import cn.cqray.android.dialog.ButterKnifeUtils
import cn.cqray.android.dialog.DialogLiveData
import cn.cqray.android.dialog.DialogUtils
import cn.cqray.android.dialog.amin.BounceIn
import cn.cqray.android.dialog.amin.BounceOut
import cn.cqray.android.dialog.amin.DialogAnimator
import cn.cqray.android.dialog.databinding.AndroidDlgLayoutBaseBinding
import java.util.concurrent.atomic.AtomicReference

@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
class DialogFragment(
    //val provider: DialogProvider
) : androidx.fragment.app.DialogFragment() {

    /** 内容视图 **/
    private val atomicContent = AtomicReference<View>()

    /** ButterKnife绑定实例  */
    private val atomicUnBinder = AtomicReference<Any>()

    /** 对话框是否能取消，依次为界面能够取消、点击外部能够取消 **/
    private val cancelable = arrayOf(true, true)

    /** 遮罩透明度，依次为原始遮罩、自定义遮罩 **/
    private val dimAmount = arrayOf(0.2F, 0F)

    /** 遮罩动画  */
    private val dimAnimator = ValueAnimator()

    /** 对话框显示、消除动画，提示显示、消失动画 **/
    private val animators : Array<DialogAnimator> = arrayOf(BounceIn(), BounceOut())

    /** Fragment视图 **/
    private val contentViewLD = DialogLiveData<Any>()

    /** [ViewBinding]实例 **/
    val viewBinding by lazy { AndroidDlgLayoutBaseBinding.inflate(layoutInflater) }

    /** 内容布局 **/
    val contentView: View? get() = atomicContent.get()

    /** 根布局 **/
    val rootView: FrameLayout get() = viewBinding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fragment视图变化监听
        contentViewLD.observe(this) {
            val layout = viewBinding.dlgContent.also { v -> v.removeAllViews() }
            when (it) {
                is Int -> View.inflate(requireContext(), it, layout)
                is View -> layout.addView(it)
            }
            // 缓存视图
            atomicContent.set(layout.getChildAt(0))
            // 兼容 ButterKnife
            atomicUnBinder.set(ButterKnifeUtils.bind(this, atomicContent.get()))
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        // Fragment视图变化监听
//        contentViewLD.observe(this) {
//            val layout = viewBinding.dlgContent.also { v -> v.removeAllViews() }
//            when (it) {
//                is Int -> View.inflate(requireContext(), it, layout)
//                is View -> layout.addView(it)
//            }
//            // 缓存视图
//            atomicContent.set(layout.getChildAt(0))
//            // 兼容 ButterKnife
//            atomicUnBinder.set(ButterKnifeUtils.bind(this, atomicContent.get()))
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = viewBinding.root.also {
        it.setOnClickListener {
            // 可点击外部取消、可取消、且面板没有被消除
            if (cancelable[0] && cancelable[1] && !animators[1].isRunning) {
                // 消除对话框
                dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 执行面板动画
        doPanelAnimator(true) {
            // 执行遮罩动画
            doDimAnimator(it, true)
        }
    }

    /**
     * 创建对话框
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(requireActivity()) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                // 事件是否被消费掉
                val dispatch = this@DialogFragment.dispatchTouchEvent(ev)
                // 动画是否正在运行
                val running = animators[0].isRunning || animators[1].isRunning
                // 任意一个条件满足则拦截事件
                return if (dispatch || running) true else super.dispatchTouchEvent(ev)
            }

            override fun onBackPressed() {
                // 如果可以被取消、没有正在关闭面板、回退事件未被拦截
                if (cancelable[0]
                    && !animators[1].isRunning
                    && !this@DialogFragment.onBackPressed()) {
                    // 消除对话框
                    this@DialogFragment.dismiss()
                }
            }
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.window?.let {
            val width = DialogUtils.getAppScreenWidth(requireContext())
            val height = DialogUtils.getAppScreenHeight(requireContext())
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

    override fun onDestroy() {
        super.onDestroy()
        // 移除ButterKnife绑定
        ButterKnifeUtils.unbind(atomicUnBinder.get())
        // DialogFragment重复展示时，会出现异常。
        // 就是因为根视图已绑定，所以需要移除根布局的绑定。
        (viewBinding.root.parent as? ViewGroup)?.removeView(viewBinding.root)
    }

    override fun dismiss() {
        // 执行面板动画
        doPanelAnimator(false) {
            // 执行遮罩动画
            doDimAnimator(it, false)
        }
    }

    fun quickDismiss() {
        runCatching {
            val fm = parentFragmentManager
            if (!fm.isStateSaved && !fm.isDestroyed && !isStateSaved) {
                super.dismiss()
            } else super.dismissAllowingStateLoss()
        }
    }

    fun setContentView(view: View) = contentViewLD.setValue(view)

    fun setContentView(@LayoutRes id: Int) = contentViewLD.setValue(id)

    override fun setCancelable(cancelable: Boolean) = cancelable.let { this.cancelable[0] = it }

    fun setCancelableOutsize(cancelable: Boolean) = cancelable.let { this.cancelable[1] = it }

    fun setShowAnimator(animator: DialogAnimator) = also { animators[0] = animator }

    fun setDismissAnimator(animator: DialogAnimator) = also { animators[1] = animator }

    fun onBackPressed() = false

    fun dispatchTouchEvent(ev: MotionEvent) = false

    /**
     * 执行面板动画，返回动画时长
     * @param show 是否是显示动画
     */
    private fun doPanelAnimator(show: Boolean, callback: ViewAnimator.Callback?) {
        // 获取对应动画
        val animator = when (show) {
            true -> animators[0]
            else -> animators[1]
        }
        // 动画没有在运行，才继续操作
        if (!animator.isRunning) {
            // 设置目标对象
            animator.setTarget(viewBinding.dlgContent)
            // 设置监听
            animator.addAnimatorListener(object : AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    if (!show) quickDismiss()
                }
            })
            // 开始面板动画
            animator.start()
        }
        animator.getDuration(callback)
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
                viewBinding.dlgDim.setBackgroundColor(Color.parseColor(color))
            }
            // 开始动画
            dimAnimator.setDuration(tmp.toLong()).start()
        }
    }
}