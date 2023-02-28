package cn.cqray.android.dialog.internal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import cn.cqray.android.dialog.GetDialogProvider

internal class InternalFragment(
    private val provider: GetDialogProvider<*>
) : DialogFragment() {

    private val dialogDelegate by lazy { provider.dialogDelegate }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = dialogDelegate.binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!showsDialog) provider.onCreating(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = dialogDelegate.onCreateDialog(savedInstanceState)

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.e("数据", "oncreate")
//    }

    override fun onDestroy() {
        super.onDestroy()
        // DialogFragment重复展示时，会出现异常。
        // 就是因为根视图已绑定，所以需要移除根布局的绑定。
        (dialogDelegate.binding.root.parent as? ViewGroup)?.removeView(dialogDelegate.binding.root)
    }
}