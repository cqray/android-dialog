package cn.cqray.android.dialog.module2

import android.animation.Animator
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import cn.cqray.android.anim.AnimatorListener
import cn.cqray.android.anim.ViewAnimator
import cn.cqray.android.dialog.DialogLiveData
import cn.cqray.android.dialog.DialogUtils
import cn.cqray.android.dialog.R
import cn.cqray.android.dialog.amin.BounceIn
import cn.cqray.android.dialog.amin.BounceOut
import cn.cqray.android.dialog.amin.DialogAnimator
import kotlin.math.max
import kotlin.math.min

/**
 * 面板组件
 */
class PanelComponent(
    val dialog: BaseDialog<*>,
    view: FrameLayout
) : ViewComponent<PanelComponent, FrameLayout>(dialog, view) {

    /** 面板相关尺寸, 依次为宽度dp值、宽度比例、宽度最小值, 宽度最大值、高度dp值、高度比例值、高度最小值，高度最大值。  */
    private val sizeArray = arrayOfNulls<Int?>(8)// { 0F }

    /** 对话框显示、消除动画，提示显示、消失动画  */
    private val animators = arrayOfNulls<DialogAnimator>(2)

    /** 对话框位置  */
    private val gravity = DialogLiveData<Int>()

    /** 对话框偏移  */
    private val offset = DialogLiveData<FloatArray>()

    /** 对话框位置  */
    private val panelSize = DialogLiveData<IntArray>()

    /**
     * 自定取消订阅
     */
    private val autoDismissObserver by lazy {
        object : DefaultLifecycleObserver {

        }
    }

    init {
        // 监听面板位置变化
        gravity.observe(lifecycleOwner) { int ->
            val parent = view.parent as? FrameLayout
            val params = parent?.layoutParams as FrameLayout.LayoutParams?
            params?.let { parent?.layoutParams = it.also { it.gravity = int } }
        }
        offset.observe(lifecycleOwner) {}

        // 面板大小监听
        panelSize.observe(lifecycleOwner) {
            val params = view.layoutParams
            if (params.width != it[0] || params.height != it[1]) {
                params.width = it[0];
                params.height = it[1];
                view.requestLayout();
            }
        }
    }

    val isAnimRunning get() = false

    val isDismissing get() = false

    /**
     * 显示面板
     */
    fun show(callback: ViewAnimator.Callback?) = doPanelAnimator(true, callback)

    /**
     * 消除面板
     */
    fun dismiss(callback: ViewAnimator.Callback?) = doPanelAnimator(false, callback)

    fun setShowAnimator(animator: DialogAnimator) = also { animators[0] = animator }

    fun setDismissAnimator(animator: DialogAnimator) = also { animators[1] = animator }

    /**
     * 执行面板动画，返回动画时长
     * @param show 是否是显示动画
     */
    private fun doPanelAnimator(show: Boolean, callback: ViewAnimator.Callback?) = also {
        // 获取对应动画
        val animator = when (show) {
            true -> animators[0] ?: BounceIn()
            else -> animators[1] ?: BounceOut()
        }
        // 动画没有在运行，才继续操作
        if (!animator.isRunning) {
            // 设置目标对象
            animator.setTarget(view.findViewById(R.id.dlg_content))
            // 设置监听
            animator.addAnimatorListener(object : AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    if (show) {
                        dialog.onShow(dialog.requireDialog())
                    } else {
//                        // 取消父级销毁监听
//                        dialog.parentLifecycleOwner.getLifecycle().removeObserver(mParentObserver)
                        // 销毁对话框
                        dialog.quickDismiss()
                    }
                }
            })
            // 开始面板动画
            animator.start()
        }
        animator.getDuration(callback)
    }

    /**
     * 更新面板尺寸
     */
    private fun changePanelSize() {
        // 对话框宽高（满屏）
        val dialogWidth = DialogUtils.getAppScreenWidth(view.context)
        val dialogHeight = DialogUtils.getAppScreenHeight(view.context)
        // 相应的尺寸信息
        val wDip = sizeArray[0]
        val wScale = sizeArray[1]
        val wMin = sizeArray[2]
        val wMax = sizeArray[3]
        val hDip = sizeArray[4]
        val hScale = sizeArray[5]
        val hMin = sizeArray[6]
        val hMax = sizeArray[7]
        // 计算新的尺寸信息
        val size = IntArray(2)
        // 宽度计算
        size[0] = when {
            wDip != null -> calculateSize(wDip, wMin, wMax)
            wScale != null -> calculateSize(wScale * dialogWidth, wMin, wMax)
            else -> wMin ?: -2
        }
        // 高度计算
        size[1] = when {
            hDip != null -> calculateSize(hDip, hMin, hMax)
            hScale != null -> calculateSize(hScale * dialogHeight, hMin, hMax)
            else -> hMin ?: -2
        }
        // 更新尺寸
        panelSize.value = size
    }

    /**
     * 从当前值、最大、最小值中计算出合适的值
     * @param value 当前值
     * @param min 最小值
     * @param max 最大值
     */
    private fun calculateSize(value: Int, min: Int?, max: Int?): Int {
        val size = arrayOf(0)
        if (min != null) size[0] = max(value, min)
        if (max != null) size[0] = min(size[0], max)
        if (min != null && max != null && min > max) size[0] = value
        return size[0]
    }
}