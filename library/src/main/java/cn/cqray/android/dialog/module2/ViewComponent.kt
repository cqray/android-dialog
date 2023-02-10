package cn.cqray.android.dialog.module2

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.dialog.DialogLiveData
import cn.cqray.android.dialog.DialogUtils
import cn.cqray.android.dialog.RoundDrawable

/**
 * 基础视图组件
 * @Cqray LeiJue
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
open class ViewComponent<
        // Module泛型，主要是统一返回类型
        M : ViewComponent<M, V>,
        // 针对的视图泛型
        V : View>(
    // 生命周期管理实例
    val lifecycleOwner: LifecycleOwner,
    // 针对的视图
    val view: V
) {

    /** 内部间隔 **/
    private val padding = DialogLiveData<Array<Int>>()

    /** 外部间隔 **/
    private val margin = DialogLiveData<Array<Int>>()

    /** 高度 **/
    private val height = DialogLiveData<Int>()

    /** 高度 **/
    private val width = DialogLiveData<Int>()

    /** 显示 **/
    private val visibility = DialogLiveData<Int>()

    /** 背景 **/
    private val background = DialogLiveData<Any?>()

    /** 圆角 **/
    private val backgroundRadii = FloatArray(8)

    /** 获取视图布局参数 **/
    private val params: MarginLayoutParams by lazy {
        val params = view.layoutParams as? MarginLayoutParams
        params ?: MarginLayoutParams(-2, -2)
    }

    init {
        // 控件内部间隔变化监听
        padding.observe(lifecycleOwner) { view.setPadding(it[0], it[1], it[2], it[3]) }
        // 控件外部间隔变化监听
        margin.observe(lifecycleOwner) {
            view.layoutParams = params.also { p -> p.setMargins(it[0], it[1], it[2], it[3]) }
        }
        // 宽度变化监听
        width.observe(lifecycleOwner) { view.layoutParams = params.also { p -> p.width = it } }
        // 高度变化监听
        height.observe(lifecycleOwner) { view.layoutParams = params.also { p -> p.height = it } }
        // 监听显示隐藏状态变化
        visibility.observe(lifecycleOwner) { view.visibility = it }
        // 监听背景变化
        background.observe(lifecycleOwner) { changeBackground(it) }
    }

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * 默认单位DP
     * @param radii 8个值的数组，4对[X，Y]半径
     */
    fun setBackgroundRadii(radii: FloatArray?) = also { setBackgroundRadii(radii, TypedValue.COMPLEX_UNIT_DIP) } as M

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * @param radii 8个值的数组，4对[X，Y]半径
     * @param unit  值单位
     */
    fun setBackgroundRadii(radii: FloatArray?, unit: Int) = also {
        when {
            // 清空圆角
            radii == null -> for (i in backgroundRadii.indices) backgroundRadii[i] = 0F
            // 无效圆角
            radii.size < 8 -> throw IllegalArgumentException("Radii array length must >= 8.")
            // 获取对应的大小
            else -> for (i in backgroundRadii.indices) {
                backgroundRadii[i] = DialogUtils.applyDimension(radii[i], unit)
            }
        }
        // 更新背景
        background.value = background.value
    } as M

    /**
     * 设置圆角大小
     * 默认单位DP
     * @param radius 圆角半径
     */
    fun setBackgroundRadius(radius: Float?) = setBackgroundRadius(radius, TypedValue.COMPLEX_UNIT_DIP)

    /**
     * 设置圆角大小
     * 默认单位DP
     * @param radius 圆角半径
     * @param unit  值单位
     */
    fun setBackgroundRadius(radius: Float?, unit: Int) = setBackgroundRadii(FloatArray(8) { radius ?: 0F }, unit)

    /**
     * 改变视图背景
     */
    private fun changeBackground(any: Any?) {
        // 获取Drawable对象
        val drawable = when (any) {
            is Int -> ContextCompat.getDrawable(view.context, any)
            else -> any
        }
        when (drawable) {
            // 纯色背景设置圆角
            is ColorDrawable -> {
                ViewCompat.setBackground(view, GradientDrawable().also {
                    it.setColor(drawable.color)
                    it.cornerRadii = backgroundRadii
                })
            }
            // 图片背景设置圆角
            is Drawable -> ViewCompat.setBackground(view, RoundDrawable(drawable).also { it.setRadii(backgroundRadii) })
            // 无背景
            null -> ViewCompat.setBackground(view, null)
        }
    }
}