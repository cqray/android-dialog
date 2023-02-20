package cn.cqray.android.ab

import android.app.Activity
import android.os.Bundle
import cn.cqray.android.dialog.databinding.AndroidDlgAlterLayoutBinding

open class AlterDialog<T : AlterDialog<T>>(activity: Activity) : BaseDialog<T>(activity) {

    val binding by lazy { AndroidDlgAlterLayoutBinding.inflate(activity.layoutInflater) }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setContentView(binding.root)
    }
}