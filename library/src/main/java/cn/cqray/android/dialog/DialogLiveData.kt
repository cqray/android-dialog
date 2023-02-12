package cn.cqray.android.dialog

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * 对话框[LiveData]实现
 * 将setValue()、postValue()改为一样的实现。
 * 在主线程中，直接执行。其他线程，跳转线程再执行。
 * @author Cqray
 */
class DialogLiveData<T> : MutableLiveData<T> {

    constructor() : super()
    constructor(value: T) : super(value)

    /** 是否在主线程 **/
    private val isInMainThread get() = Looper.myLooper() == Looper.getMainLooper()

    override fun setValue(value: T) {
        if (isInMainThread) super.setValue(value)
        else super.postValue(value)
    }

    override fun postValue(value: T) {
        if (isInMainThread) super.setValue(value)
        else super.postValue(value)
    }

    fun notifyChanged() {
        if (isInMainThread) super.setValue(value)
        else super.postValue(value)
    }
}