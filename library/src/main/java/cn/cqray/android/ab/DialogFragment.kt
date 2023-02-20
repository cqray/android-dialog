package cn.cqray.android.ab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

internal class DialogFragment(
    private val provider: DialogProvider<*>
) : DialogFragment() {

    private val dialogDelegate by lazy { provider.dialogDelegate }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = dialogDelegate.binding.root

    override fun onCreateDialog(savedInstanceState: Bundle?) = dialogDelegate.onCreateDialog(savedInstanceState)

    override fun onDestroy() {
        super.onDestroy()
        // DialogFragment重复展示时，会出现异常。
        // 就是因为根视图已绑定，所以需要移除根布局的绑定。
        (dialogDelegate.binding.root.parent as? ViewGroup)?.removeView(dialogDelegate.binding.root)
    }
}