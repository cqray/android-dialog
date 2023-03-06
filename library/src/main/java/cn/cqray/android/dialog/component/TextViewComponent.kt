package cn.cqray.android.dialog.component

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.dialog.DialogLiveData
import cn.cqray.android.dialog.DialogUtils
import cn.cqray.java.tool.SizeUnit

open class TextViewComponent(
    lifecycleOwner: LifecycleOwner,
    viewGet: Function0<TextView>
) : ViewComponent<TextView>(
    lifecycleOwner,
    viewGet,
) {
    /** 文本  */
    private val text = DialogLiveData<Any?>()

    /** 文本颜色  */
    private val textColor = DialogLiveData<ColorStateList>()

    /** 文本大小  */
    private val textSize = DialogLiveData<Any>()

    /** 文本加粗  */
    private val textStyle = DialogLiveData<Int>()

    private val textTypeface = DialogLiveData<Any?>()

    /** 文本位置  */
    private val gravity = DialogLiveData<Int>()

    init {
        // 文本变化监听
        text.observe(lifecycleOwner) {
            when (it) {
                null -> view.text = null
                is Int -> view.setText(it)
                is CharSequence -> view.text = it
            }
        }
        // 文本颜色变化监听
        textColor.observe(lifecycleOwner) { view.setTextColor(it) }
        // 文本大小变化监听
        textSize.observe(lifecycleOwner) {
            val size = when (it) {
                is Int -> view.resources.getDimension(it)
                else -> it as Float
            }
            view.setTextSize(SizeUnit.PX.type, size)
        }
        // 文本样式变化监听
        textTypeface.observe(lifecycleOwner) {
            when(it) {
                is Int -> view.typeface = Typeface.defaultFromStyle(it)
                is Typeface -> view.typeface = it
                else -> view.typeface = null
            }
        }
        // 文本位置变化监听
        gravity.observe(lifecycleOwner) { view.gravity = it }
    }

    fun setText(@StringRes id: Int) = text.setValue(id)

    fun setText(text: CharSequence?) = this.text.setValue(text)

    fun setTextColor(@ColorInt color: Int) = textColor.setValue(ColorStateList.valueOf(color))

    fun setTextColor(color: String) = setTextColor(Color.parseColor(color))

    fun setTextColor(colorStateList: ColorStateList) = textColor.setValue(colorStateList)

    fun setTextSize(@DimenRes id: Int) = textSize.setValue(id)

    fun setTextSize(size: Float) = setTextSize(size, SizeUnit.DIP)

    fun setTextSize(size: Float, unit: SizeUnit) = textSize.setValue(DialogUtils.applyDimension(size, unit.type))

    fun setTextSize(unit: SizeUnit, size: Float) = textSize.setValue(DialogUtils.applyDimension(size, unit.type))

    fun setTextBold(bold: Boolean) = textStyle.setValue(if (bold) Typeface.BOLD else Typeface.NORMAL)

    fun setTextTypeface(typeface: Int) = textTypeface.setValue(typeface)

    fun setTextTypeface(typeface: Typeface?) = textTypeface.setValue(typeface)

    fun setGravity(gravity: Int) = this.gravity.setValue(gravity)
}