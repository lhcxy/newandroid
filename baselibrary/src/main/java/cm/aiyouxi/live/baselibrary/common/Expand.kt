package cm.aiyouxi.live.baselibrary.common

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast

fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
fun Context.toast(@StringRes message: Int) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

