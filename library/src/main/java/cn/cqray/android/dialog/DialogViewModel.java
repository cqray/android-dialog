package cn.cqray.android.dialog;

import android.os.Bundle;

import androidx.annotation.StyleRes;
import androidx.lifecycle.ViewModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(prefix = "m")
@EqualsAndHashCode(callSuper = true)
public class DialogViewModel extends ViewModel {

    @StyleRes
    private int mTheme = 0;
    private int mBackStackId = -1;
    private Bundle mDialogState;
}
