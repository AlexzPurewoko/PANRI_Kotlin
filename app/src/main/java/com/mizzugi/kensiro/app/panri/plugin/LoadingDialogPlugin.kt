package com.mizzugi.kensiro.app.panri.plugin

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.mizzugi.kensiro.app.panri.R

class LoadingDialogPlugin(val context: Context) {
    var dialog: AlertDialog? = null

    val isAttached: Boolean
        get() {
            return dialog?.isShowing ?: false
        }

    fun stopDialog() {
        dialog?.dismiss()
    }

    fun showDialog() {
        dialog?.show()
    }


    fun buildLoadingLayout() {
        if (dialog == null) {
            val rootElement = buildAndConfigureRootelement()
            val builder = AlertDialog.Builder(context)
            builder.setView(rootElement)
            builder.setCancelable(false)
            dialog = builder.create()
        }
    }

    private fun buildAndConfigureRootelement(): LinearLayout {
        return LayoutInflater.from(context).inflate(R.layout.loading_dialog, null) as LinearLayout
    }
}