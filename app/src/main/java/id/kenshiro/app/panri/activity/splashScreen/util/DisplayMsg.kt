package id.kenshiro.app.panri.activity.splashScreen.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import id.kenshiro.app.panri.R
import id.kenshiro.app.panri.params.SplashScreenParams

object DisplayMsg {
    fun displayOnLoadError(ctx: Context, ex: Throwable) {
        val activity = ctx as Activity
        val alertMsg = AlertDialog.Builder(ctx)
            .setTitle(SplashScreenParams.TITLE_LOAD_ERROR_MSG)
            .setIcon(R.mipmap.ic_launcher)
            .setMessage("I'm Sorry :( \n\nError while try to load app -> $ex ")
            .setPositiveButton(SplashScreenParams.DIALOG_BTN_RESTART) { dialog, _ ->
                dialog.cancel()
                val intent = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.finish()
                ctx.startActivity(intent)
            }
            .setNegativeButton(SplashScreenParams.DIALOG_BTN_QUIT) { dialog, _ ->
                dialog.dismiss()
                activity.finish()
            }
        alertMsg.show()
    }
}