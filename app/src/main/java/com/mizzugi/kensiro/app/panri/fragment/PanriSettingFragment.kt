package com.mizzugi.kensiro.app.panri.fragment

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.plugin.PublicContract

class PanriSettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.panri_settings, rootKey)
        preferenceManager.sharedPreferencesName = PublicContract.SHARED_PREF_NAME

        val preference =
            preferenceManager.findPreference<CheckBoxPreference>(PublicContract.KEY_AUTOCHECKUPDATE_APPDATA)
        preference?.apply {
            isChecked =
                sharedPreferences.getBoolean(PublicContract.KEY_AUTOCHECKUPDATE_APPDATA, true)
            /*setOnPreferenceClickListener {
                val n = !isChecked
                isChecked = n
                sharedPreferences.edit(commit = true){
                    putBoolean(PublicContract.KEY_AUTOCHECKUPDATE_APPDATA, n)
                }
                n
            }*/
        }
    }

}