package cn.cqray.android.dialog.component

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.dialog.internal.LiveData
import cn.cqray.android.dialog.internal.Utils

open class TextViewComponent(
    lifecycleOwner: LifecycleOwner,
    viewGet: Function0<TextView>
) : ViewComponent<TextView>(
    lifecycleOwner,
    viewGet,
) {
    /** 文本  */
    private val textLD = LiveData<Any?>()

    /** 文本颜色  */
    private val textColorLD = LiveData<ColorStateList>()

    /** 文本大小  */
    private val textSizeLD = LiveData<Any>()

    /** 文本加粗  */
    private val textStyleLD = LiveData<Int>()

    /** 文本位置  */
    private val gravityLD = LiveData<Int>()

    init {
        // 文本变化监听
        textLD.observe(lifecycleOwner) {
            when (it) {
                null -> view.text = null
                is Int -> view.setText(it)
                is CharSequence -> view.text = it
            }
        }
        // 文本颜色变化监听
        textColorLD.observe(lifecycleOwner) { view.setTextColor(it) }
        // 文本大小变化监听
        textSizeLD.observe(lifecycleOwner) {
            val size = when (it) {
                is Int -> view.resources.getDimension(it)
                else -> it as Float
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
        // 文本样式变化监听
        textStyleLD.observe(lifecycleOwner) { view.typeface = Typeface.defaultFromStyle(it) }
        // 文本位置变化监听
        gravityLD.observe(lifecycleOwner) { view.gravity = it }
    }

    fun setText(@StringRes id: Int) = textLD.setValue(id)

    fun setText(text: CharSequence?) = textLD.setValue(text)

    fun setTextColor(@ColorInt color: Int) = textColorLD.setValue(ColorStateList.valueOf(color))

    fun setTextColor(colors: ColorStateList) = textColorLD.setValue(colors)

    fun setTextSize(size: Float) = setTextSize(size, TypedValue.COMPLEX_UNIT_SP)

    fun setTextSize(size: Float, unit: Int) = textSizeLD.setValue(Utils.applyDimension(size, unit))

    fun setTextStyle(textStyle: Int) = textStyleLD.setValue(textStyle)

    fun setGravity(gravity: Int) = gravityLD.setValue(gravity)
}