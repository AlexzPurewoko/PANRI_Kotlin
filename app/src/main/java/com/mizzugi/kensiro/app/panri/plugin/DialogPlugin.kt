package com.mizzugi.kensiro.app.panri.plugin

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.R
import id.apwdevs.library.text.TextSpanFormat
import kotlin.math.roundToInt

object DialogPlugin {
    fun showExitAppDialog(activity: BaseActivity) {
        var btnNo: Button

        var btnYes: Button
        val mAlert = AlertDialog.Builder(activity).apply {
            val layout = (LinearLayout.inflate(
                activity,
                R.layout.dialog_on_exit,
                null
            ) as ConstraintLayout).apply {
                // text
                findViewById<TextView>(R.id.actmain_id_dialogexit_content).apply {
                    setTextColor(Color.BLACK)
                    setTypeface(
                        Typeface.createFromAsset(
                            activity.assets,
                            "Comic_Sans_MS3.ttf"
                        ), Typeface.BOLD
                    )
                    setText(R.string.actmain_string_dialogexit_desc)
                }

                // btn yes
                btnYes = findViewById<Button>(R.id.actmain_id_dialogexit_btnyes).apply {
                    setTypeface(
                        Typeface.createFromAsset(
                            activity.assets,
                            "Comic_Sans_MS3.ttf"
                        ), Typeface.BOLD
                    )
                    setTextColor(Color.WHITE)
                    setText(R.string.actmain_string_dialogexit_btnyes)
                }

                // btn no
                btnNo = findViewById<Button>(R.id.actmain_id_dialogexit_btnno).apply {
                    setTypeface(
                        Typeface.createFromAsset(
                            activity.assets,
                            "Comic_Sans_MS3.ttf"
                        ), Typeface.BOLD
                    )
                    setTextColor(Color.WHITE)
                    setText(R.string.actmain_string_dialogexit_btnno)
                }
            }
            setView(
                layout
            )


        }.create()

        btnYes.setOnClickListener {
            mAlert.cancel()
            activity.finish()
        }
        btnNo.setOnClickListener {
            mAlert.cancel()
        }
        mAlert.show()
    }

    fun showDialogWhatsNew(activity: BaseActivity, onBtnOk: DialogInterface.OnClickListener) {
        AlertDialog.Builder(activity).apply {
            setTitle("Apa yang baru?")
            setIcon(R.mipmap.ic_launcher)
            setView(
                TextView(activity).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    val padding =
                        Math.round(activity.resources.getDimension(R.dimen.dialogshdup_margin_all))
                    setPadding(padding, padding, padding, padding)
                    text = TextSpanFormat.convertStrToSpan(
                        activity,
                        readFromAsset(activity.assets, "whats_new") ?: "",
                        0
                    )
                })
            setPositiveButton("Okay", onBtnOk)
        }.show()
    }

    fun showDialogAboutSKPP(
        activity: BaseActivity,
        path: String,
        title: String
    ): AlertDialog.Builder = AlertDialog.Builder(activity).apply {
        setTitle(title)
        setIcon(R.mipmap.ic_launcher)

        val textView = TextView(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = TextSpanFormat.convertStrToSpan(
                activity,
                readFromAsset(activity.assets, path) ?: "",
                0
            )
        }
        setView(
            ScrollView(activity).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val padding =
                    activity.resources.getDimension(R.dimen.dialogshdup_margin_all)
                        .roundToInt()
                setPadding(padding, padding, padding, padding)
                addView(textView)
            })
        setPositiveButton("Okay") { dialog, _ ->
            dialog.cancel()
        }
    }


    /*
     * showUpdateNotAvailable()
     * @param activity
     * @param conditionUpdate
     * @param updateValueIfExists -> {
     *      0 -> db Original Version
     *      1 -> latest version
     *      2 -> message OnUPdateListener
     *      3 -> onHandleClickListener (positive)
     *
     * }
     *
     */
    fun showUpdateDBDialog(
        activity: BaseActivity,
        conditionUpdate: Int,
        vararg updateValueIfExists: Any?
    ) {
        val build = AlertDialog.Builder(activity).apply {
            setTitle("Update Data Aplikasi!")
            setIcon(R.mipmap.ic_launcher)
            setMessage(
                when (conditionUpdate) {
                    PublicContract.UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING -> {
                        "Update Databases tidak tersedia karena koneksi internet tidak aktif. Silahkan aktifkan koneksi internet anda terlebih dahulu."
                    }
                    PublicContract.UPDATE_DB_NOT_AVAILABLE -> {
                        "Database anda sudah versi terbaru."
                    }
                    PublicContract.UPDATE_DB_IS_AVAILABLE -> {
                        // 0 -> original
                        // 1 latest release
                        "Update Data Aplikasi tersedia!\n\nVersi Data Aplikasi : " + updateValueIfExists[0] + "\nVersi Data App terkini : " + updateValueIfExists[1] + "\n\nApakah anda ingin memperbaharui data aplikasi?"
                    }
                    else -> return
                }
            )

            if (conditionUpdate == PublicContract.UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING || conditionUpdate == PublicContract.UPDATE_DB_NOT_AVAILABLE) {
                setPositiveButton(
                    "Okay!"
                ) { dialog, _ -> dialog.dismiss() }
            } else if (conditionUpdate == PublicContract.UPDATE_DB_IS_AVAILABLE) {
                setPositiveButton(
                    updateValueIfExists[2] as String,
                    updateValueIfExists[3] as DialogInterface.OnClickListener
                )
                setNegativeButton(
                    "Batal"
                ) { dialog, _ -> dialog.dismiss() }
            }
        }
        build.show()
    }


    fun showUpdateDBDialogMain(
        activity: BaseActivity,
        conditionUpdate: Int,
        vararg updateValueIfExists: Any
    ) {
        val build = AlertDialog.Builder(activity).apply {
            setTitle("Update Data Aplikasi!")
            setIcon(R.mipmap.ic_launcher)
            setView(
                (LinearLayout.inflate(
                    activity,
                    R.layout.dialog_show_dbupdate,
                    null
                ) as LinearLayout).apply {

                    findViewById<TextView>(R.id.dialogshdup_text).apply {
                        text =
                            "Update Data Aplikasi tersedia!\n\nVersi Data Aplikasi : " + updateValueIfExists[0] + "\nVersi Data App terkini : " + updateValueIfExists[1] + "\n\nApakah anda ingin memperbaharui data aplikasi?"
                    }
                    findViewById<CheckBox>(R.id.dialogshdup_check).apply {
                        isChecked = false
                        setOnClickListener {
                            context.getSharedPreferences(
                                PublicContract.SHARED_PREF_NAME,
                                Context.MODE_PRIVATE
                            ).edit(commit = true) {
                                putBoolean(PublicContract.KEY_AUTOCHECKUPDATE_APPDATA, isChecked)
                            }
                        }
                    }
                }
            )


            if (conditionUpdate == PublicContract.UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING || conditionUpdate == PublicContract.UPDATE_DB_NOT_AVAILABLE) {
                setPositiveButton(
                    "Okay!"
                ) { dialog, _ -> dialog.dismiss() }
            } else if (conditionUpdate == PublicContract.UPDATE_DB_IS_AVAILABLE) {
                setPositiveButton(
                    updateValueIfExists[2] as String,
                    updateValueIfExists[3] as DialogInterface.OnClickListener
                )
                setNegativeButton(
                    "Batal"
                ) { dialog, _ -> dialog.dismiss() }
            }
        }

        build.show()
    }
}