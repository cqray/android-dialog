package cn.cqray.android.ab

import android.util.TypedValue

@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
@JvmDefaultWithoutCompatibility
interface PanelProvider<T : PanelProvider<T>> {

    val panelComponent: PanelComponent

    fun width(width: Float) = also { width(width, TypedValue.COMPLEX_UNIT_DIP) } as T

    fun width(width: Float, unit: Int) = also { panelComponent.setWidth(width, unit) } as T

    fun widthScale(scale: Float) = also { panelComponent.setWidthScale(scale) } as T

    fun widthMin(width: Float) = also { widthMin(width, TypedValue.COMPLEX_UNIT_DIP) } as T

    fun widthMin(width: Float, unit: Int) = also { panelComponent.setWidthMin(width, unit) } as T

    fun widthMax(width: Float) = also { widthMax(width, TypedValue.COMPLEX_UNIT_DIP) } as T

    fun widthMax(width: Float, unit: Int) = also { panelComponent.setWidthMax(width, unit) } as T

    fun height(width: Float) = also { height(width, TypedValue.COMPLEX_UNIT_DIP) } as T

    fun height(width: Float, unit: Int) = also { panelComponent.setHeight(width, unit) } as T

    fun heightScale(scale: Float) = also { panelComponent.setHeightScale(scale) } as T

    fun heightMin(width: Float) = also { heightMin(width, TypedValue.COMPLEX_UNIT_DIP) } as T

    fun heightMin(width: Float, unit: Int) = also { panelComponent.setHeightMin(width, unit) } as T

    fun heightMax(width: Float) = also { heightMax(width, TypedValue.COMPLEX_UNIT_DIP) } as T

    fun heightMax(width: Float, unit: Int) = also { panelComponent.setHeightMax(width, unit) } as T
}