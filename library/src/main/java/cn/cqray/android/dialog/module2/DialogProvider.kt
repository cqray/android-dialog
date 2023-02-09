package cn.cqray.android.dialog.module2

interface DialogProvider {

    fun show()

    fun dismiss()

    fun quickDismiss()

    fun onBackPressed() = false
}