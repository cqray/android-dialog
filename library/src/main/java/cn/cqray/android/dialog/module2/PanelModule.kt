package cn.cqray.android.dialog.module2

import android.view.View
import android.widget.FrameLayout
import cn.cqray.android.dialog.DialogLiveData
import cn.cqray.android.dialog.DialogUtils
import cn.cqray.android.dialog.amin.DialogAnimator
import kotlin.math.max
import kotlin.math.min

class PanelModule(
    dialog: BaseDialog<*>,
    view: FrameLayout
) : ViewModule<FrameLayout>(dialog, view) {

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

    init {
        // 监听面板位置变化
        gravity.observe(lifecycleOwner) { int ->
            val parent = view.parent as? FrameLayout
            val params = parent?.layoutParams as FrameLayout.LayoutParams?
            params?.let { parent?.layoutParams = it.also { it.gravity = int } }
        }
        offset.observe(lifecycleOwner) {}

        panelSize.observe(lifecycleOwner) {
            val params = view.layoutParams
            if (params.width != it[0] || params.height != it[1]) {
                params.width = it[0];
                params.height = it[1];
                view.requestLayout();
            }
        }
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