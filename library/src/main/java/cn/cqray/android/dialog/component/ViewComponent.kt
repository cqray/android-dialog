package cn.cqray.android.dialog.component

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.util.TypedValue.*
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.dialog.DialogLiveData
import cn.cqray.android.dialog.RoundDrawable
import cn.cqray.android.dialog.DialogUtils.applyDimension

/**
 * 视图组件，主要实现功能：
 * 1.内部间隔设置
 * 2.外部间隔设置
 * 3.宽高设置
 * 4.显示隐藏设置
 * 5.背景设置
 * 6.背景圆角设置
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
open class ViewComponent<V : View>(
    val lifecycleOwner: LifecycleOwner,
    val viewGet: Function0<V>
) {

    val view by lazy { viewGet.invoke() }

    /** 内部间隔 **/
    private val paddingLD = DialogLiveData<IntArray>()

    /** 外部间隔 **/
    private val marginLD = DialogLiveData<IntArray>()

    /** 高度 **/
    private val heightLD = DialogLiveData<Int>()

    /** 高度 **/
    private val widthLD = DialogLiveData<Int>()

    /** 显示 **/
    private val visibilityLD = DialogLiveData<Int>()

    /** 背景 **/
    private val backgroundLD = DialogLiveData<Any?>()

    /** 圆角 **/
    private val backgroundRadii = FloatArray(8)

    /** 获取视图布局参数 **/
    private val params: ViewGroup.MarginLayoutParams by lazy {
        val params = view.layoutParams as? ViewGroup.MarginLayoutParams
        params ?: ViewGroup.MarginLayoutParams(-2, -2)
    }

    init {
        // 控件内部间隔变化监听
        paddingLD.observe(lifecycleOwner) { view.setPadding(it[0], it[1], it[2], it[3]) }
        // 控件外部间隔变化监听
        marginLD.observe(lifecycleOwner) {
            view.layoutParams = params.also { p -> p.setMargins(it[0], it[1], it[2], it[3]) }
        }
        // 宽度变化监听
        widthLD.observe(lifecycleOwner) {
            view.layoutParams = params.also { p -> p.width = it }
            view.parent?.requestLayout()
        }
        // 高度变化监听
        heightLD.observe(lifecycleOwner) {
            view.layoutParams = params.also { p -> p.height = it }
            view.parent?.requestLayout()
        }
        // 监听显示隐藏状态变化
        visibilityLD.observe(lifecycleOwner) { view.visibility = it }
        // 监听背景变化
        backgroundLD.observe(lifecycleOwner) { changeBackground(it) }
    }

    /**
     * 设置内部间隔，默认单位DIP
     * @param padding 间隔值
     */
    open fun setPadding(padding: Float) = setPadding(padding, COMPLEX_UNIT_DIP)

    /**
     * 设置内部间隔
     * @param padding 间隔值
     * @param unit 值单位[TypedValue]
     */
    open fun setPadding(padding: Float, unit: Int) = setPadding(padding, padding, padding, padding, unit)

    /**
     * 设置内部左右间隔，默认单位DIP
     * @param padding 间隔值
     */
    open fun setPaddingLR(padding: Float) = setPaddingLR(padding, COMPLEX_UNIT_DIP)

    /**
     * 设置内部左右间隔
     * @param padding 间隔值
     * @param unit 值单位[TypedValue]
     */
    open fun setPaddingLR(padding: Float, unit: Int) = setPadding(
        applyDimension(padding, unit),
        paddingLD.value?.get(1)?.toFloat() ?: 0F,
        applyDimension(padding, unit),
        paddingLD.value?.get(1)?.toFloat() ?: 0F,
        COMPLEX_UNIT_PX
    )

    /**
     * 设置内部上下间隔，默认单位DIP
     * @param padding 间隔值
     */
    open fun setPaddingTB(padding: Float) = setPaddingTB(padding, COMPLEX_UNIT_DIP)

    /**
     * 设置内部部上下间隔
     * @param padding 间隔值
     * @param unit 值单位[TypedValue]
     */
    open fun setPaddingTB(padding: Float, unit: Int) = setPadding(
        paddingLD.value?.get(1)?.toFloat() ?: 0F,
        applyDimension(padding, unit),
        paddingLD.value?.get(1)?.toFloat() ?: 0F,
        applyDimension(padding, unit),
        COMPLEX_UNIT_PX
    )

    /**
     * 设置内部部间隔，默认单位DIP
     * @param l 左间隔值
     * @param t 上间隔值
     * @param r 右间隔值
     * @param b 下间隔值
     */
    open fun setPadding(l: Float, t: Float, r: Float, b: Float) = setPadding(l, t, r, b, COMPLEX_UNIT_DIP)

    /**
     * 设置内部部间隔
     * @param l 左间隔值
     * @param t 上间隔值
     * @param r 右间隔值
     * @param b 下间隔值
     * @param unit 值单位[TypedValue]
     */
    open fun setPadding(l: Float, t: Float, r: Float, b: Float, unit: Int) {
        val array = paddingLD.value ?: IntArray(4)
        paddingLD.setValue(array.also {
            it[0] = applyDimension(l, unit).toInt()
            it[1] = applyDimension(t, unit).toInt()
            it[2] = applyDimension(r, unit).toInt()
            it[3] = applyDimension(b, unit).toInt()
        })
    }

    /**
     * 设置外部间隔，默认单位DIP
     * @param margin 间隔值
     */
    open fun setMargin(margin: Float) = setMargin(margin, COMPLEX_UNIT_DIP)

    /**
     * 设置外部间隔
     * @param margin 间隔值
     * @param unit 值单位[TypedValue]
     */
    open fun setMargin(margin: Float, unit: Int) = setMargin(margin, margin, margin, margin, unit)

    /**
     * 设置外部左右间隔，默认单位DIP
     * @param margin 间隔值
     */
    open fun setMarginLR(margin: Float) = setMarginLR(margin, COMPLEX_UNIT_DIP)

    /**
     * 设置外部左右间隔
     * @param margin 间隔值
     * @param unit 值单位[TypedValue]
     */
    open fun setMarginLR(margin: Float, unit: Int) = setMargin(
        applyDimension(margin, unit),
        paddingLD.value?.get(1)?.toFloat() ?: 0F,
        applyDimension(margin, unit),
        paddingLD.value?.get(1)?.toFloat() ?: 0F,
        COMPLEX_UNIT_PX
    )

    /**
     * 设置外部上下间隔，默认单位DIP
     * @param margin 间隔值
     */
    open fun setMarginTB(margin: Float) = setMarginTB(margin, COMPLEX_UNIT_DIP)

    /**
     * 设置外部上下间隔
     * @param margin 间隔值
     * @param unit 值单位[TypedValue]
     */
    open fun setMarginTB(margin: Float, unit: Int) = setMargin(
        paddingLD.value?.get(1)?.toFloat() ?: 0F,
        applyDimension(margin, unit),
        paddingLD.value?.get(1)?.toFloat() ?: 0F,
        applyDimension(margin, unit),
        COMPLEX_UNIT_PX
    )

    /**
     * 设置外部间隔，默认单位DIP
     * @param l 左间隔值
     * @param t 上间隔值
     * @param r 右间隔值
     * @param b 下间隔值
     */
    open fun setMargin(l: Float, t: Float, r: Float, b: Float) = setMargin(l, t, r, b, COMPLEX_UNIT_DIP)

    /**
     * 设置外部间隔
     * @param l 左间隔值
     * @param t 上间隔值
     * @param r 右间隔值
     * @param b 下间隔值
     * @param unit 值单位[TypedValue]
     */
    open fun setMargin(l: Float, t: Float, r: Float, b: Float, unit: Int) {
        val array = marginLD.value ?: IntArray(4)
        marginLD.setValue(array.also {
            it[0] = applyDimension(l, unit).toInt()
            it[1] = applyDimension(t, unit).toInt()
            it[2] = applyDimension(r, unit).toInt()
            it[3] = applyDimension(b, unit).toInt()
        })
    }

    /**
     * 设置控件宽度，默认单位DIP
     * @param width 宽度
     */
    open fun setWidth(width: Float) = setWidth(width, COMPLEX_UNIT_DIP)

    /**
     * 设置控件宽度
     * @param width 宽度
     * @param unit 值单位[TypedValue]
     */
    open fun setWidth(width: Float, unit: Int) = widthLD.setValue(applyDimension(width, unit).toInt())

    /**
     * 设置控件高度，默认单位DIP
     * @param height 高度
     */
    open fun setHeight(height: Float) = setHeight(height, COMPLEX_UNIT_DIP)

    /**
     * 设置控件高度
     * @param height 高度
     * @param unit 值单位[TypedValue]
     */
    open fun setHeight(height: Float, unit: Int) = heightLD.setValue(applyDimension(height, unit).toInt())

    /**
     * 设置视图显示[View.VISIBLE]或隐藏[View.INVISIBLE]
     * @param visible true [View.VISIBLE] false [View.INVISIBLE]
     */
    open fun setVisible(visible: Boolean) = visibilityLD.setValue(if (visible) View.VISIBLE else View.INVISIBLE)

    /**
     * 设置视图显示[View.VISIBLE]或隐藏[View.GONE]
     * @param gone true [View.GONE] false [View.VISIBLE]
     */
    open fun setGone(gone: Boolean) = visibilityLD.setValue(if (gone) View.GONE else View.VISIBLE)

    /**
     * 设置背景[Drawable]
     * @param drawable 背景
     */
    open fun setBackground(drawable: Drawable?) = backgroundLD.setValue(drawable)

    /**
     * 设置背景颜色
     * @param color 颜色
     */
    open fun setBackgroundColor(@ColorInt color: Int) = backgroundLD.setValue(ColorDrawable(color))

    /**
     * 设置背景资源
     * @param id 背景资源
     */
    open fun setBackgroundResource(@DrawableRes id: Int) = backgroundLD.setValue(id)

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * 默认单位DIP
     * @param radii 8个值的数组，4对[X，Y]半径
     */
    open fun setBackgroundRadii(radii: FloatArray) = setBackgroundRadii(radii, COMPLEX_UNIT_DIP)

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * @param radii 8个值的数组，4对[X，Y]半径
     * @param unit  值单位
     */
    open fun setBackgroundRadii(radii: FloatArray, unit: Int) {
        when {
            // 无效圆角
            radii.size < 8 -> throw IllegalArgumentException("Radii array length must >= 8.")
            // 获取对应的大小
            else -> for (i in backgroundRadii.indices) {
                backgroundRadii[i] = applyDimension(radii[i], unit)
            }
        }
        // 更新背景
        backgroundLD.notifyChanged()
    }

    /**
     * 设置圆角大小
     * 默认单位DIP
     * @param radius 圆角半径
     */
    open fun setBackgroundRadius(radius: Float) = setBackgroundRadius(radius, COMPLEX_UNIT_DIP)

    /**
     * 设置圆角大小
     * 默认单位DIP
     * @param radius 圆角半径
     * @param unit  值单位
     */
    open fun setBackgroundRadius(radius: Float, unit: Int) = setBackgroundRadii(FloatArray(8) { radius }, unit)

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