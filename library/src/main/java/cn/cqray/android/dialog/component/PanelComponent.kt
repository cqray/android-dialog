package cn.cqray.android.dialog.component

import android.graphics.Color
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.dialog.DialogLiveData
import cn.cqray.android.dialog.R
import cn.cqray.android.dialog.Utils
import kotlin.math.max
import kotlin.math.min

/**
 * 面板组件，继承于[ViewComponent]
 * 新增功能：面板的大小变化，（包含比例设置、最大最小设置）
 * @author Cqray
 */
class PanelComponent(
    lifecycleOwner: LifecycleOwner,
    viewGet: Function0<FrameLayout>
) : ViewComponent<FrameLayout>(
    lifecycleOwner,
    viewGet,
) {

    /** 面板相关尺寸, 依次为:
     * 宽度PX值、宽度比例值、宽度最小值, 宽度最大值，
     * 高度PX值、高度比例值、高度最小值，高度最大值。
     **/
    private val sizes = arrayOfNulls<Float?>(8)

    /** 对话框位置 **/
    private val sizeLD = DialogLiveData<FloatArray>()

    /** 对话框大小**/
    private val sizeChangeLD = DialogLiveData<Unit>()

//    val tipComponent = TipComponent(lifecycleOwner) { viewGet().findViewById(R.id.dlg_tip) }

    init {
        // 设置默认圆大小
        setBackgroundRadius(6F)
        // 设置默认背景色
        setBackgroundColor(Color.WHITE)
        // 订阅面板大小监听
        sizeLD.observe(lifecycleOwner) {
            super.setWidth(it[0], TypedValue.COMPLEX_UNIT_PX)
            super.setHeight(it[1], TypedValue.COMPLEX_UNIT_PX)
        }
        // 订阅面板大小变更监听
        sizeChangeLD.observe(lifecycleOwner) { changeSizes() }
//        // 设置TIP默认圆角
//        tipComponent.setVisible(true)
    }

    @Synchronized
    override fun setWidth(width: Float, unit: Int) {
        sizes[0] = if (width <= 0) null else Utils.applyDimension(width, unit)
        sizes[1] = null
        sizeChangeLD.notifyChanged()
    }

    @Synchronized
    fun setWidthScale(scale: Float) {
        sizes[0] = null
        sizes[1] = if ((scale <= 0) or (scale > 1)) null else scale
        sizeChangeLD.notifyChanged()
    }

    @Synchronized
    fun setWidthMin(width: Float, unit: Int) {
        sizes[2] = if (width <= 0) null else Utils.applyDimension(width, unit)
        sizeChangeLD.notifyChanged()
    }

    @Synchronized
    fun setWidthMax(width: Float, unit: Int) {
        sizes[3] = if (width <= 0) null else Utils.applyDimension(width, unit)
        sizeChangeLD.notifyChanged()
    }

    @Synchronized
    override fun setHeight(height: Float, unit: Int) {
        sizes[4] = if (height <= 0) null else Utils.applyDimension(height, unit)
        sizes[5] = null
        sizeChangeLD.notifyChanged()
    }

    @Synchronized
    fun setHeightScale(scale: Float) {
        sizes[4] = null
        sizes[5] = if ((scale <= 0) or (scale > 1)) null else scale
        sizeChangeLD.notifyChanged()
    }

    @Synchronized
    fun setHeightMin(height: Float, unit: Int) {
        sizes[6] = if (height <= 0) null else Utils.applyDimension(height, unit)
        sizeChangeLD.notifyChanged()
    }

    @Synchronized
    fun setHeightMax(height: Float, unit: Int) {
        sizes[7] = if (height <= 0) null else Utils.applyDimension(height, unit)
        sizeChangeLD.notifyChanged()
    }

    /**
     * 更改面板尺寸
     */
    private fun changeSizes() {
        // 对话框宽高（满屏）
        val dialogWidth = Utils.getAppScreenWidth(view.context)
        val dialogHeight = Utils.getAppScreenHeight(view.context)
        // 计算新的尺寸信息
        val size = FloatArray(2)
        // 宽度计算
        size[0] = when {
            // 从宽度PX值、宽度最小值, 宽度最大值中获取合适的宽度
            sizes[0] != null -> calculateSize(sizes[0]!!, sizes[2], sizes[3])
            // 从宽度比例值、宽度最小值, 宽度最大值中获取合适的宽度
            sizes[1] != null -> calculateSize(sizes[1]!! * dialogWidth, sizes[2], sizes[3])
            // 取最小值或自适应
            else -> sizes[2] ?: -2F
        }
        // 高度计算
        size[1] = when {
            // 高度PX值、高度最小值，高度最大值中获取合适的宽度
            sizes[4] != null -> calculateSize(sizes[4]!!, sizes[6], sizes[7])
            // 高度比例值、高度最小值，高度最大值中获取合适的宽度
            sizes[5] != null -> calculateSize(sizes[5]!! * dialogHeight, sizes[6], sizes[7])
            // 取最小值或自适应
            else -> sizes[6] ?: -2F
        }
        // 更新尺寸
        sizeLD.setValue(size)
    }

    /**
     * 从当前值、最大、最小值中计算出合适的值
     * @param value 当前值
     * @param min 最小值
     * @param max 最大值
     */
    private fun calculateSize(value: Float, min: Float?, max: Float?): Float {
        val size = arrayOf(value)
        if (min != null) size[0] = max(size[0], min)
        if (max != null) size[0] = min(size[0], max)
        if (min != null && max != null && min > max) size[0] = value
        return size[0]
    }
}