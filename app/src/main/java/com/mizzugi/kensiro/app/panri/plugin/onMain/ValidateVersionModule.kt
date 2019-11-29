package com.mizzugi.kensiro.app.panri.plugin.onMain

import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mizzugi.kensiro.app.panri.activity.MainActivity
import com.mizzugi.kensiro.app.panri.plugin.DialogPlugin
import com.mizzugi.kensiro.app.panri.plugin.PublicContract

object ValidateVersionModule {
    fun validateVersion(activity: MainActivity) {
        activity.apply {
            val appCond = intent.extras?.getInt(PublicContract.APP_CONDITION_KEY)
            val dbCondition =
                getSharedPreferences(PublicContract.SHARED_PREF_NAME, MODE_PRIVATE).getInt(
                    PublicContract.KEY_VERSION_BOOL_NEW, PublicContract.DB_IS_SAME_VERSION
                )
            var messageIfNeeded: String? = null
            var dbErrorMesageIfNeeded: String? = null

            when (appCond) {
                PublicContract.APP_IS_FIRST_USAGE -> DialogPlugin.showDialogAboutSKPP(
                    this,
                    "sk.spanf",
                    "Syarat & Ketentuan"
                ).apply {
                    setCancelable(false)
                    setPositiveButton("Agree") { dialog, _ ->
                        dialog.dismiss()
                        DialogPlugin.showDialogAboutSKPP(
                            activity,
                            "kp.spanf",
                            "Kebijakan Privasi"
                        ).apply {
                            setCancelable(false)
                            setPositiveButton(
                                "Agree"
                            ) { dialog1, _ ->
                                dialog1.dismiss()
                                DialogPlugin.showDialogWhatsNew(activity,
                                    DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                            }
                        }.show()

                    }
                }.show()

                PublicContract.APP_IS_NEWER_VERSION -> DialogPlugin.showDialogWhatsNew(this,
                    DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                PublicContract.APP_IS_OLDER_VERSION -> messageIfNeeded =
                    "Aplikasi ini sudah usang dan tidak kompatibel dengan versi sebelumnya yang lebih baru, coba copot dan pasang lagi aplikasi ini"
                PublicContract.APP_IS_SAME_VERSION -> {
                }
            }

            when (dbCondition) {
                PublicContract.DB_REQUEST_UPDATE -> {
                    // if not accepted
                    if (isAllowedToCheckDBOnline(activity)) {
                        val dbVersion = getVersionOnShareds(activity)
                        DialogPlugin.showUpdateDBDialogMain(
                            this,
                            PublicContract.UPDATE_DB_IS_AVAILABLE,
                            dbVersion[0],
                            dbVersion[1],
                            "Update",
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                activity.taskDownloadDBUpdateDBViewModel.run(dbVersion[1])
                                //TaskDownloadDBUpdates(this@MainActivity, dbVersion[1]).execute()
                            }
                        )
                        System.gc()
                    }
                }
                PublicContract.DB_IS_FIRST_USAGE -> {
                }
                PublicContract.DB_IS_NEWER_VERSION -> TOAST(
                    Toast.LENGTH_SHORT,
                    "The data now is new version !"
                )
                PublicContract.DB_IS_OLDER_IN_APP_VERSION ->
                    dbErrorMesageIfNeeded =
                        "Current Database dengan database di aplikasi sudah usang, mohon copot dan pasang aplikasi untuk membenahi"
                PublicContract.DB_IS_SAME_VERSION -> {
                }
            }
            showDialog(activity, messageIfNeeded, dbErrorMesageIfNeeded)
        }

    }

    private fun isAllowedToCheckDBOnline(activity: MainActivity): Boolean {
        val sharedPreferences =
            activity.getSharedPreferences(PublicContract.SHARED_PREF_NAME, MODE_PRIVATE)
        return sharedPreferences.getBoolean(PublicContract.KEY_AUTOCHECKUPDATE_APPDATA, true)
    }

    private fun getVersionOnShareds(activity: MainActivity): Array<String> {
        activity.getSharedPreferences(PublicContract.SHARED_PREF_NAME, MODE_PRIVATE).apply {
            val cloudVersion = getString(PublicContract.KEY_VERSION_ON_CLOUD, null)
            val appDbVersion =
                getString(PublicContract.KEY_DATA_LIBRARY_VERSION, null)
            val apV = Integer.parseInt(appDbVersion!!)
            var clV: Int
            cloudVersion?.apply {
                if (equals("undefined") or (cloudVersion == appDbVersion)) {
                    clV = apV
                } else {
                    clV = toInt()
                }

                return arrayOf("" + apV, "" + clV)
            }

            return arrayOf("" + apV, "" + apV)
        }
    }

    private fun showDialog(
        activity: MainActivity,
        messageIfNeeded: String?,
        dbErrorMesageIfNeeded: String?
    ) {
        var selected: Int
        if (messageIfNeeded == null && dbErrorMesageIfNeeded == null) return
        val alert = AlertDialog.Builder(activity).apply {
            setCancelable(false)
            setTitle("Peringatan!")
            setIcon(android.R.drawable.ic_dialog_alert)

            if (messageIfNeeded == null) {
                selected = 0
                setMessage(dbErrorMesageIfNeeded)
            } else {
                selected = 1
                setMessage(messageIfNeeded)
            }
            val selectable = selected
            setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                    if (selectable == 0) {
                        activity.finish()
                        return@OnClickListener
                    }
                    val alert2 = AlertDialog.Builder(activity).apply {
                        setCancelable(false)
                        setTitle("Peringatan!")
                        setIcon(android.R.drawable.ic_dialog_alert)
                        setMessage(dbErrorMesageIfNeeded)
                        setPositiveButton("OK") { dialog2, _ ->
                            dialog2.cancel()
                            activity.finish()
                        }
                    }
                    alert2.show()
                })
        }

        alert.show()
    }

}