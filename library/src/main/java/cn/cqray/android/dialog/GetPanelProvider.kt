package cn.cqray.android.dialog

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.util.TypedValue.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import cn.cqray.android.dialog.component.PanelComponent

/**
 * 对话框面板相关功能提供器
 * @author Cqray
 */
@Suppress(
    "Deprecation",
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
@JvmDefaultWithoutCompatibility
interface GetPanelProvider<T : GetPanelProvider<T>> {

    /** 面板组件 **/
    val panelComponent: PanelComponent

    /**
     * 设置面板宽度，单位DIP
     * @param width 宽度
     */
    @JvmDefault
    fun width(width: Float) = also { width(width, COMPLEX_UNIT_DIP) } as T

    /**
     * 设置面板宽度
     * @param width 宽度
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun width(width: Float, unit: Int) = also { panelComponent.setWidth(width, unit) } as T

    /**
     * 设置面板宽度占用屏幕比例
     * @param scale 比例
     */
    @JvmDefault
    fun widthScale(@FloatRange(from = 0.0, to = 1.0) scale: Float) = also { panelComponent.setWidthScale(scale) } as T

    /**
     * 设置面板最小宽度，单位DIP
     * @param width 宽度
     */
    @JvmDefault
    fun widthMin(width: Float) = also { widthMin(width, COMPLEX_UNIT_DIP) } as T

    /**
     * 设置面板最小宽度
     * @param width 宽度
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun widthMin(width: Float, unit: Int) = also { panelComponent.setWidthMin(width, unit) } as T

    /**
     * 设置面板最大宽度
     * @param width 宽度
     */
    @JvmDefault
    fun widthMax(width: Float) = also { widthMax(width, COMPLEX_UNIT_DIP) } as T

    /**
     * 设置面板最大宽度
     * @param width 宽度
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun widthMax(width: Float, unit: Int) = also { panelComponent.setWidthMax(width, unit) } as T

    /**
     * 设置面板高度，单位DIP
     * @param height 高度
     */
    @JvmDefault
    fun height(height: Float) = also { height(height, COMPLEX_UNIT_DIP) } as T

    /**
     * 设置面板高度
     * @param height 高度
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun height(height: Float, unit: Int) = also { panelComponent.setHeight(height, unit) } as T

    /**
     * 设置面板高度占用屏幕比例
     * @param scale 比例
     */
    @JvmDefault
    fun heightScale(scale: Float) = also { panelComponent.setHeightScale(scale) } as T

    /**
     * 设置面板最小高度
     * @param height 高度
     */
    @JvmDefault
    fun heightMin(height: Float) = also { heightMin(height, COMPLEX_UNIT_DIP) } as T

    /**
     * 设置面板最小高度
     * @param height 高度
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun heightMin(height: Float, unit: Int) = also { panelComponent.setHeightMin(height, unit) } as T

    /**
     * 设置面板最大高度
     * @param height 高度
     */
    @JvmDefault
    fun heightMax(height: Float) = also { heightMax(height, COMPLEX_UNIT_DIP) } as T

    /**
     * 设置面板最大高度
     * @param height 高度
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun heightMax(height: Float, unit: Int) = also { panelComponent.setHeightMax(height, unit) } as T

    /**
     * 设置内部间隔，默认单位DIP
     * @param padding 间隔值
     */
    @JvmDefault
    fun setPadding(padding: Float) = also { panelComponent.setPadding(padding) } as T

    /**
     * 设置内部间隔
     * @param padding 间隔值
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun setPadding(padding: Float, unit: Int) = also { panelComponent.setPadding(padding, unit) } as T

    /**
     * 设置内部左右间隔，默认单位DIP
     * @param padding 间隔值
     */
    @JvmDefault
    fun setPaddingLR(padding: Float) = also { panelComponent.setPaddingLR(padding) } as T

    /**
     * 设置内部左右间隔
     * @param padding 间隔值
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun setPaddingLR(padding: Float, unit: Int) = also { panelComponent.setPaddingLR(padding, unit) } as T

    /**
     * 设置内部上下间隔，默认单位DIP
     * @param padding 间隔值
     */
    @JvmDefault
    fun setPaddingTB(padding: Float) = also { panelComponent.setPaddingTB(padding) } as T

    /**
     * 设置内部部上下间隔
     * @param padding 间隔值
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun setPaddingTB(padding: Float, unit: Int) = also { panelComponent.setPaddingTB(padding, unit) } as T

    /**
     * 设置内部部间隔，默认单位DIP
     * @param l 左间隔值
     * @param t 上间隔值
     * @param r 右间隔值
     * @param b 下间隔值
     */
    @JvmDefault
    fun setPadding(l: Float, t: Float, r: Float, b: Float) = also { panelComponent.setPadding(l, t, r, b) } as T

    /**
     * 设置内部部间隔
     * @param l 左间隔值
     * @param t 上间隔值
     * @param r 右间隔值
     * @param b 下间隔值
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun setPadding(l: Float, t: Float, r: Float, b: Float, unit: Int) = also {
        panelComponent.setPadding(l, t, r, b, unit)
    } as T

    /**
     * 设置外部间隔，默认单位DIP
     * @param margin 间隔值
     */
    @JvmDefault
    fun margin(margin: Float) = also { panelComponent.setMargin(margin) } as T

    /**
     * 设置外部间隔
     * @param margin 间隔值
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun margin(margin: Float, unit: Int) = also { panelComponent.setMargin(margin, unit) } as T

    /**
     * 设置外部左右间隔，默认单位DIP
     * @param margin 间隔值
     */
    @JvmDefault
    fun marginLR(margin: Float) = also { panelComponent.setMarginLR(margin) } as T

    /**
     * 设置外部左右间隔
     * @param margin 间隔值
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun marginLR(margin: Float, unit: Int) = also { panelComponent.setMarginLR(margin, unit) } as T

    /**
     * 设置外部上下间隔，默认单位DIP
     * @param margin 间隔值
     */
    @JvmDefault
    fun marginTB(margin: Float) = also { panelComponent.setMarginTB(margin) } as T

    /**
     * 设置外部上下间隔
     * @param margin 间隔值
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun marginTB(margin: Float, unit: Int) = also { panelComponent.setMarginTB(margin, unit) } as T

    /**
     * 设置外部间隔，默认单位DIP
     * @param l 左间隔值
     * @param t 上间隔值
     * @param r 右间隔值
     * @param b 下间隔值
     */
    @JvmDefault
    fun margin(l: Float, t: Float, r: Float, b: Float) = also { panelComponent.setMargin(l, t, r, b) } as T

    /**
     * 设置外部间隔
     * @param l 左间隔值
     * @param t 上间隔值
     * @param r 右间隔值
     * @param b 下间隔值
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun margin(l: Float, t: Float, r: Float, b: Float, unit: Int) = also {
        panelComponent.setMargin(l, t, r, b, unit)
    } as T

    /**
     * 设置背景[Drawable]
     * @param drawable 背景
     */
    @JvmDefault
    fun background(drawable: Drawable?) = also { panelComponent.setBackground(drawable) } as T

    /**
     * 设置背景颜色
     * @param color 颜色
     */
    @JvmDefault
    fun backgroundColor(@ColorInt color: Int) = also { panelComponent.setBackgroundColor(color) } as T

    /**
     * 设置背景资源
     * @param id 背景资源
     */
    @JvmDefault
    fun backgroundResource(@DrawableRes id: Int) = also { panelComponent.setBackgroundResource(id) } as T

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * 默认单位DIP
     * @param radii 8个值的数组，4对[X，Y]半径
     */
    @JvmDefault
    fun backgroundRadii(radii: FloatArray) = also { panelComponent.setBackgroundRadii(radii) } as T

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * @param radii 8个值的数组，4对[X，Y]半径
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun backgroundRadii(radii: FloatArray, unit: Int) = also {
        panelComponent.setBackgroundRadii(radii, unit)
    } as T

    /**
     * 设置圆角大小
     * 默认单位DIP
     * @param radius 圆角半径
     */
    @JvmDefault
    fun backgroundRadius(radius: Float) = also { panelComponent.setBackgroundRadius(radius) } as T

    /**
     * 设置圆角大小
     * 默认单位DIP
     * @param radius 圆角半径
     * @param unit 值单位[TypedValue]
     */
    @JvmDefault
    fun backgroundRadius(radius: Float, unit: Int) = also { panelComponent.setBackgroundRadius(radius, unit) } as T
}