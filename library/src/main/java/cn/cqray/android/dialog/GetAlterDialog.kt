package cn.cqray.android.dialog

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.viewbinding.ViewBinding
import cn.cqray.android.dialog.component.TextViewComponent
import cn.cqray.android.dialog.component.ViewComponent
import cn.cqray.android.dialog.databinding.AndroidDlgAlterLayoutBinding
import cn.cqray.java.tool.SizeUnit
import kotlin.math.max

/**
 * 提示对话框
 * @author LeiJue
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
)
open class GetAlterDialog<T : GetAlterDialog<T>> (activity: Activity) : GetDialog<T>(activity) {

    /** [ViewBinding]界面 **/
    private val binding by lazy { AndroidDlgAlterLayoutBinding.inflate(activity.layoutInflater) }

    /** 标题组件 **/
    private val titleComponent by lazy { TextViewComponent(dialogLifecycleOwner, { binding.dlgTitle }) }

    /** 分割线组件 **/
    private val dividerComponent by lazy { ViewComponent(dialogLifecycleOwner, { binding.dlgDivider }) }

    /** 按钮组件集合 **/
    private val buttonComponents = mutableListOf<TextViewComponent>()

    private val contentViewLD = DialogLiveData<Any>()

    init {
//        title("77777")
//        titleGravity(Gravity.CENTER)
//        titleTextSize(20F)
//        titleVisible(false)
//        buttonTextColors(Color.BLACK, Color.BLUE)
//        buttonTextSizes(30F, 20F)
//        buttonTexts("6666   ", "   7777")
    }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        super.setContentView(binding.root)
        // 新的内容布局实现
        contentViewLD.observe(dialogLifecycleOwner) {
            with(binding.dlgContent) {
                // 添加界面
                removeAllViews()
                when(it) {
                    null -> {}
                    is Int -> View.inflate(context, it, this)
                    is View -> addView(it)
                }
                // 居中
                val child = getChildAt(0)
                val params = child.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.CENTER
            }
        }
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
        dividerComponent.setVisible(visible)
    }

    fun buttonTexts(vararg texts: CharSequence) = also {
        synchronized(buttonComponents) {
            val count = max(texts.size, buttonComponents.size)
            for (i in 0 until count) {
                val text = if (i < texts.size) texts[i] else texts[texts.size - 1]
                getButtonComponent(i).setText(text)
            }
        }
    } as T

    fun buttonTextColors(vararg colors: Int) = also {
        synchronized(buttonComponents) {
            val count = max(colors.size, buttonComponents.size)
            for (i in 0 until count) {
                val color = if (i < colors.size) colors[i] else colors[colors.size - 1]
                getButtonComponent(i).setTextColor(color)
            }
        }
    } as T

    fun buttonTextSizes(vararg sizes: Float) = buttonTextSizes(SizeUnit.SP, *sizes)

    fun buttonTextSizes(unit: SizeUnit, vararg sizes: Float) = also {
        synchronized(buttonComponents) {
            val count = max(sizes.size, buttonComponents.size)
            for (i in 0 until count) {
                val size = if (i < sizes.size) sizes[i] else sizes[sizes.size - 1]
                getButtonComponent(i).setTextSize(unit, size)
            }
        }
    } as T

    /**
     * 获取指定位置的按钮组件
     * @param position 指定位置
     */
    private fun getButtonComponent(position: Int): TextViewComponent {
        return when {
            position < buttonComponents.size -> buttonComponents[position]
            else -> {
                TextViewComponent(dialogLifecycleOwner, {
                    // 创建新的TextView
                    TextView(activity).also { binding.dlgBottom.addView(it) }
                }).also { buttonComponents.add(it) }
            }
        }
    }

    companion object {

        @JvmStatic
        @Suppress("UPPER_BOUND_VIOLATED_WARNING")
        fun builder(activity: Activity) = GetAlterDialog<GetAlterDialog<*>>(activity)

        @JvmStatic
        fun setDefault() {}
    }
}