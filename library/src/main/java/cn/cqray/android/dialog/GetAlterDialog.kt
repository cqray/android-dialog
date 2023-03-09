package cn.cqray.android.dialog

import android.app.Activity
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import cn.cqray.android.dialog.component.TextViewComponent
import cn.cqray.android.dialog.component.ViewComponent
import cn.cqray.android.dialog.databinding.AndroidAlterDialogLayoutBinding
import cn.cqray.android.dialog.internal.LiveData
import cn.cqray.android.dialog.internal.Utils
import com.google.android.flexbox.FlexboxLayout

/**
 * 提示对话框
 * @author LeiJue
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
)
open class GetAlterDialog<T : GetAlterDialog<T>>(activity: Activity) : GetDialog<T>(activity) {

    /** [ViewBinding]界面 **/
    protected val binding by lazy { AndroidAlterDialogLayoutBinding.inflate(activity.layoutInflater) }

    /** 标题组件 **/
    val titleComponent by lazy { TextViewComponent(this) { binding.dlgTitle } }

    /** 内容组件 **/
    val contentComponent by lazy {
        val context = binding.root.context
        val id = R.layout.android_alter_dialog_content
        TextViewComponent(this) { View.inflate(context, id, null) as TextView }
    }

    /** 分割线组件 **/
    val dividerTopComponent by lazy { ViewComponent(this) { binding.dlgDividerTop } }

    /** 分割线组件 **/
    val dividerBottomComponent by lazy { ViewComponent(this) { binding.dlgDividerBottom } }

    /** 按钮组件集合 **/
    private val buttonComponents = mutableListOf<TextViewComponent>()

    /** 按钮文本 **/
    private val buttonTexts = mutableListOf<CharSequence>()

    /** 按钮文本颜色 **/
    private val buttonTextColors = mutableListOf<Any>()

    /** 按钮文本大小 **/
    private val buttonTextSizes = mutableListOf<Float?>()

    /** 按钮文本字体样式 **/
    private val buttonTextStyles = mutableListOf<Int?>()

    /** 按钮背景 **/
    private val buttonBackgrounds = mutableListOf<Any?>()

    /** 按钮点击监听 **/
    private val buttonListeners = mutableListOf<View.OnClickListener?>()

    /** 按钮视图变化 **/
    private val buttonLD = LiveData<Any>()

    /** 内容视图布局 **/
    private val contentViewLD = LiveData<Any>()

    /** 分割线颜色 **/
    private val dividerColorLD = LiveData<Int?>(null)

    /** 分割线大小 **/
    private val dividerSizeLD = LiveData<Float?>()

    /** 分割线是否显示 **/
    private val dividerVisibleLD = LiveData(arrayOf(true, true, true))

    init {
        buttonTexts("取消", "确认")
        this.topDividerVisible(false)
        this.widthScale(0.8F)
        this.widthMax(300F)
    }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        super.setContentView(binding.root)
        // 未设置布局，则使用默认布局
        if (contentViewLD.value == null) contentViewLD.setValue(contentComponent.view)
        // 新的内容布局实现
        contentViewLD.observe(this) {
            with(binding.dlgContent) {
                // 添加界面
                removeAllViews()
                when (it) {
                    null -> {
                    }
                    is Int -> View.inflate(context, it, this)
                    is View -> addView(it)
                }
                // 居中
                val child = getChildAt(0)
                val params = child.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.CENTER
            }
        }
        buttonLD.observe(this) {
            // 首先改变按钮的缓存
            changeButtonComponents()
            // 再改变按钮属性
            changeButtonProperties()
        }
        // 分割线
        dividerColorLD.observe(this) { changeDividerProperties() }
        dividerSizeLD.observe(this) { changeDividerProperties() }
        dividerVisibleLD.observe(this) { changeDividerProperties() }
    }

    override fun setContentView(id: Int) = also { contentViewLD.setValue(id) } as T

    override fun setContentView(view: View) = also { contentViewLD.setValue(view) } as T

    protected fun getButtonComponent(index: Int) = buttonComponents.getOrNull(index)

    fun titleHeight(height: Float) = also { titleComponent.setHeight(height) } as T

    fun titleHeight(height: Float, unit: Int) = also { titleComponent.setHeight(height, unit) } as T

    fun titleVisible(visible: Boolean) = also {
        titleComponent.setVisibility(if (visible) View.VISIBLE else View.GONE)
        if (!visible) topDividerVisible(false)
    } as T

    fun titleGravity(gravity: Int) = also { titleComponent.setGravity(gravity) } as T

    fun titleText(title: CharSequence?) = also { titleComponent.setText(title) } as T

    fun titleText(@StringRes id: Int) = also { titleComponent.setText(id) } as T

    fun titleTextColor(color: Int) = also { titleComponent.setTextColor(color) } as T

    fun titleTextColor(colors: ColorStateList) = also { titleComponent.setTextColor(colors) } as T

    fun titleTextSize(size: Float) = also { titleComponent.setTextSize(size) } as T

    fun titleTextSize(size: Float, unit: Int) = also { titleComponent.setTextSize(size, unit) } as T

    fun titleTextStyle(textStyle: Int) = also { titleComponent.setTextStyle(textStyle) } as T

    fun contentHeight(height: Float) = also { contentComponent.setHeight(height) } as T

    fun contentHeight(height: Float, unit: Int) = also { contentComponent.setHeight(height, unit) } as T

    fun contentText(content: CharSequence?) = also { contentComponent.setText(content) } as T

    fun contentText(@StringRes id: Int) = also { contentComponent.setText(id) } as T

    fun contentTextColor(color: Int) = also { contentComponent.setTextColor(color) } as T

    fun contentTextSize(size: Float) = also { contentComponent.setTextSize(size) } as T

    fun contentTextSize(size: Float, unit: Int) = also { contentComponent.setTextSize(size, unit) } as T

    fun contentTextStyle(textStyle: Int) = also { contentComponent.setTextStyle(textStyle) } as T

    fun contentPadding(padding: Float) = also { contentComponent.setPadding(padding) } as T

    fun contentPadding(padding: Float, unit: Int) = also { contentComponent.setPadding(padding, unit) } as T

    fun buttonTexts(vararg texts: CharSequence) = also {
        synchronized(buttonTexts) {
            buttonTexts.clear()
            buttonTexts.addAll(texts.toMutableList())
            buttonLD.notifyChanged()
        }
    } as T

    fun buttonTextColors(vararg colors: Int) = also {
        synchronized(buttonTextColors) {
            buttonTextColors.clear()
            buttonTextColors.addAll(colors.toMutableList())
            buttonLD.notifyChanged()
        }
    } as T

    fun buttonTextColors(vararg colors: ColorStateList) = also {
        synchronized(buttonTextColors) {
            buttonTextColors.clear()
            buttonTextColors.addAll(colors.toMutableList())
            buttonLD.notifyChanged()
        }
    } as T

    fun buttonTextSizes(vararg sizes: Float) = buttonTextSizes(sizes.toTypedArray())

    fun buttonTextSizes(sizes: Array<Float>) = buttonTextSizes(sizes, TypedValue.COMPLEX_UNIT_SP)

    fun buttonTextSizes(sizes: Array<Float>, unit: Int) = also {
        synchronized(buttonTextSizes) {
            buttonTextSizes.clear()
            buttonTextSizes.addAll(mutableListOf<Float>().also { list ->
                for (size in sizes) {
                    // 转换尺寸
                    list.add(Utils.applyDimension(size, unit))
                }
            })
            buttonLD.notifyChanged()
        }
    } as T

    fun buttonTextStyles(vararg textStyles: Int?) = also {
        synchronized(buttonTextStyles) {
            buttonTextStyles.clear()
            buttonTextStyles.addAll(textStyles.toMutableList())
            buttonLD.notifyChanged()
        }
    } as T

    fun buttonBackground(vararg drawables: Drawable?) = also {
        synchronized(buttonBackgrounds) {
            buttonBackgrounds.clear()
            buttonBackgrounds.addAll(drawables.toMutableList())
            buttonLD.notifyChanged()
        }
    } as T

    fun buttonBackgroundResources(vararg resources: Int?) = also {
        synchronized(buttonBackgrounds) {
            buttonBackgrounds.clear()
            buttonBackgrounds.addAll(resources.toMutableList())
            buttonLD.notifyChanged()
        }
    } as T

    fun buttonListeners(vararg listeners: View.OnClickListener?) = also {
        synchronized(buttonListeners) {
            buttonListeners.clear()
            buttonListeners.addAll(listeners.toMutableList())
            buttonLD.notifyChanged()
        }
    } as T

    fun buttonDividerVisible(visible: Boolean) = also {
        val visibleArray = dividerVisibleLD.value!!.also { it[2] = visible }
        dividerVisibleLD.setValue(visibleArray)
    } as T

    fun topDividerVisible(visible: Boolean) = also {
        val visibleArray = dividerVisibleLD.value!!.also { it[0] = visible }
        dividerVisibleLD.setValue(visibleArray)
    } as T

    fun bottomDividerVisible(visible: Boolean) = also {
        val visibleArray = dividerVisibleLD.value!!.also { it[1] = visible }
        dividerVisibleLD.setValue(visibleArray)
    } as T

    /**
     * 改变缓存的Button组件
     */
    private fun changeButtonComponents() {
        // 如果现有的按钮数量大于或等于文本数量
        if (buttonComponents.size >= buttonTexts.size) {
            // 让按钮数量和文本持平
            for (i in (buttonTextSizes.size - 1 until buttonComponents.size).reversed()) {
                binding.dlgBottom.removeView(buttonComponents[i].view)
                buttonComponents.removeAt(i)
            }
        } else {
            for (i in (buttonComponents.size until buttonTexts.size)) {
                // 添加新的Button组件
                TextViewComponent(this, {
                    // 创建新的TextView
                    TextView(activity).also {
                        it.layoutParams = ViewGroup.LayoutParams(-2, -1)
                        it.gravity = Gravity.CENTER
                        binding.dlgBottom.addView(it)
                    }
                }).also { buttonComponents.add(it) }
            }
        }
        // 更新位置信息
        for (i in buttonComponents.indices) {
            val params = buttonComponents[i].view.layoutParams as? FlexboxLayout.LayoutParams
            params?.flexBasisPercent = 100F / buttonComponents.size
        }
    }

    /**
     * 改变按钮属性
     */
    private fun changeButtonProperties() {
        // 更新文本属性
        for (i in buttonComponents.indices) {
            val component = buttonComponents[i]
            // 文字文本
            with(buttonTexts) {
                if (i < size) component.setText(getOrNull(i))
                else component.setText(null)
            }
            // 文字颜色
            with(buttonTextColors) {
                // 获取默认的两种颜色ID，并取值
                val defId = when {
                    buttonTexts.size == 1 -> R.color.colorPrimary
                    i > 0 -> R.color.text
                    else -> R.color.tint
                }
                val defColor = ContextCompat.getColor(activity, defId)
                // 获取用户设置的按钮颜色
                val btnColor =
                    if (i < size) getOrNull(i)
                    else getOrNull(size - 1)
                // 设置对应的颜色
                when (btnColor) {
                    is ColorStateList -> component.setTextColor(btnColor)
                    is Int -> component.setTextColor(btnColor)
                    null -> component.setTextColor(defColor)
                }
            }
            // 文字大小
            with(buttonTextSizes) {
                val textSize = activity.resources.getDimension(R.dimen.h3)
                if (i < size) component.setTextSize(getOrNull(i) ?: textSize, TypedValue.COMPLEX_UNIT_PX)
                else component.setTextSize(getOrNull(size - 1) ?: textSize, TypedValue.COMPLEX_UNIT_PX)
            }
            // 文字样式
            with(buttonTextStyles) {
                val textStyle =
                    if (i < size) getOrNull(i)
                    else getOrNull(size - 1)
                component.setTextStyle(textStyle ?: Typeface.NORMAL)
            }
            // 设置背景
            with(buttonBackgrounds) {
                val background =
                    if (i < size) getOrNull(i)
                    else getOrNull(size - 1)
                when (background) {
                    is Int -> component.setBackgroundResource(background)
                    is Drawable -> component.setBackground(background)
                    else -> Utils.setRippleBackground(component.view)
                }
            }
            // 点击事件
            component.view.setOnClickListener {
                dismiss()
                buttonListeners.getOrNull(i)?.onClick(component.view)
            }
        }
    }

    /**
     * 改变分割线属性
     */
    private fun changeDividerProperties() {
        val context = dividerTopComponent.view.context
        val color = dividerColorLD.value ?: ContextCompat.getColor(context, R.color.divider)
        val size = dividerSizeLD.value ?: (Resources.getSystem().displayMetrics.density * 0.75 + 0.5)
        val visible = dividerVisibleLD.value!!
        // 顶部分割线
        with(dividerTopComponent) {
            setBackgroundColor(color)
            view.layoutParams.height = size.toInt()
            view.visibility = if (visible[0]) View.VISIBLE else View.INVISIBLE
        }
        // 底部分割线
        with(dividerBottomComponent) {
            setBackgroundColor(color)
            view.layoutParams.height = size.toInt()
            view.visibility = if (visible[1]) View.VISIBLE else View.INVISIBLE
        }
        // 按钮容器
        with(binding.dlgBottom) {
            setDividerDrawable(GradientDrawable().also {
                it.setColor(color)
                it.setSize(size.toInt(), Int.MAX_VALUE)
            })
            setShowDivider(
                if (visible[2]) FlexboxLayout.SHOW_DIVIDER_MIDDLE
                else FlexboxLayout.SHOW_DIVIDER_NONE
            )
        }
    }

    companion object {
        @JvmStatic
        @Suppress("UPPER_BOUND_VIOLATED_WARNING")
        fun builder(activity: Activity) = GetAlterDialog<GetAlterDialog<*>>(activity)
    }
}