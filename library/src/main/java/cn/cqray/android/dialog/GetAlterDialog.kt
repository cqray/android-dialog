package cn.cqray.android.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
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
import cn.cqray.java.tool.SizeUnit
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
    protected val titleComponent by lazy { TextViewComponent(this, { binding.dlgTitle }) }

    /** 分割线组件 **/
    protected val dividerTopComponent by lazy { ViewComponent(this, { binding.dlgDividerTop }) }

    /** 分割线组件 **/
    protected val dividerBottomComponent by lazy { ViewComponent(this, { binding.dlgDividerBottom }) }

    /** 按钮组件集合 **/
    private val buttonComponents = mutableListOf<TextViewComponent>()

    /** 按钮文本 **/
    private val buttonTexts = mutableListOf<CharSequence>()

    /** 按钮文本颜色 **/
    private val buttonTextColors = mutableListOf<Int?>()

    /** 按钮文本大小 **/
    private val buttonTextSizes = mutableListOf<Float?>()

    /** 按钮点击监听 **/
    private val buttonListeners = mutableListOf<View.OnClickListener?>()

    /** 按钮视图变化 **/
    private val buttonLD = DialogLiveData<Any>()

    /** 内容视图布局 **/
    private val contentViewLD = DialogLiveData<Any>()

    /** 分割线颜色 **/
    private val dividerColorLD = DialogLiveData<Int?>(null)

    /** 分割线大小 **/
    private val dividerSizeLD = DialogLiveData<Float?>()

    init {
//        title("77777")
//        titleGravity(Gravity.CENTER)
//        titleTextSize(20F)
//        titleVisible(false)
//        backgroundColor(Color.BLACK)
//        buttonTextColors(Color.BLACK, Color.BLUE)
//        buttonTextSizes(20F, 20F)
        buttonTexts("取消", "确认")
        this.widthScale(0.8F)
        this.widthMax(300F)
    }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        super.setContentView(binding.root)
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
    }

    override fun setContentView(id: Int) = also { contentViewLD.setValue(id) } as T

    override fun setContentView(view: View) = also { contentViewLD.setValue(view) } as T

    fun titleText(title: CharSequence?) = also { titleComponent.setText(title) } as T

    fun titleText(@StringRes id: Int) = also { titleComponent.setText(id) } as T

    fun titleTextColor(color: Int) = also { titleComponent.setTextColor(color) }

    fun titleTextSize(size: Float) = titleTextSize(size, SizeUnit.DIP)

    fun titleTextSize(size: Float, unit: SizeUnit) = also { titleComponent.setTextSize(size, unit) }

    fun titleGravity(gravity: Int) = also { titleComponent.setGravity(gravity) } as T

    fun titleVisible(visible: Boolean) = also {
        titleComponent.setVisible(visible)
        dividerTopComponent.setVisible(visible)
    }

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

    fun buttonTextSizes(vararg sizes: Float) = buttonTextSizes(sizes.toTypedArray())

    fun buttonTextSizes(sizes: Array<Float>) = buttonTextSizes(sizes, SizeUnit.SP)

    fun buttonTextSizes(sizes: Array<Float>, unit: SizeUnit) = also {
        synchronized(buttonTextSizes) {
            buttonTextSizes.clear()
            buttonTextSizes.addAll(mutableListOf<Float>().also { list ->
                for (size in sizes) {
                    // 转换尺寸
                    list.add(DialogUtils.applyDimension(size, unit.type))
                }
            })
            buttonLD.notifyChanged()
        }
    } as T

    fun buttonListeners(vararg listeners: View.OnClickListener?) = also {
        synchronized(buttonListeners) {
            buttonListeners.clear()
            buttonListeners.addAll(listeners.toMutableList())
            buttonLD.notifyChanged()
        }
    }

    /**
     * 改变缓存的Button组件
     */
    @SuppressLint("ResourceType")
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
            val params = buttonComponents[i].view.layoutParams as FlexboxLayout.LayoutParams
            params.flexBasisPercent = 100F / buttonComponents.size
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
                val id = if (i > 0) R.color.text else R.color.tint
                val textColor = ContextCompat.getColor(activity, id)
                if (i < size) component.setTextColor(getOrNull(i) ?: textColor)
                else component.setTextColor(getOrNull(size - 1) ?: textColor)
            }
            // 文字大小
            with(buttonTextSizes) {
                val textSize = activity.resources.getDimension(R.dimen.h3)
                if (i < size) component.setTextSize(getOrNull(i) ?: textSize, SizeUnit.PX)
                else component.setTextSize(getOrNull(size - 1) ?: textSize, SizeUnit.PX)
            }
            // 设置背景
            DialogUtils.setRippleBackground(component.view)
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
        val size = dividerSizeLD.value ?: Resources.getSystem().displayMetrics.density * 0.75 + 0.5
        // 设置颜色
        dividerTopComponent.setBackgroundColor(color)
        dividerBottomComponent.setBackgroundColor(color)
        // 设置大小
        dividerTopComponent.view.layoutParams.height = size.toInt()
        dividerBottomComponent.view.layoutParams.height = size.toInt()
        binding.dlgBottom.setDividerDrawable(GradientDrawable().also {
            it.setColor(color)
            it.setSize(size.toInt(), Int.MAX_VALUE)
        })
    }

    companion object {
        @JvmStatic
        @Suppress("UPPER_BOUND_VIOLATED_WARNING")
        fun builder(activity: Activity) = GetAlterDialog<GetAlterDialog<*>>(activity)
    }

}