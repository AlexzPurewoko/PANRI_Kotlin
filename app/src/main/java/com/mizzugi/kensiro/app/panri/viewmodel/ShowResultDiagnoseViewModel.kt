package com.mizzugi.kensiro.app.panri.viewmodel

import android.app.Application
import android.content.Context
import android.os.Parcelable
import android.text.Spanned
import androidx.annotation.IdRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mizzugi.kensiro.app.panri.plugin.loadHtml
import com.mizzugi.kensiro.app.panri.roomDatabase.PanriDatabase
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class ShowResultDiagnoseViewModel(application: Application) : AndroidViewModel(application) {
    val mKlikBawahText: MutableLiveData<String> = MutableLiveData()
    val finishedLoading: MutableLiveData<Boolean> = MutableLiveData()
    val loadedTitleText: MutableLiveData<String> = MutableLiveData()
    val loadedLatinText: MutableLiveData<String> = MutableLiveData()
    val loadedImagePath: MutableLiveData<List<String>> = MutableLiveData()
    val loadedUmumPath: MutableLiveData<Spanned> = MutableLiveData()
    val loadedGejalaPath: MutableLiveData<Spanned> = MutableLiveData()
    val loadedResolvePath: MutableLiveData<Spanned> = MutableLiveData()

    val currentBtnCount: MutableLiveData<CurrentBtnCondition> = MutableLiveData()
    val currentTextBottom: MutableLiveData<Int> = MutableLiveData()

    var bottomCardTextData: BottomCardTextData? = null

    init {
        finishedLoading.value = false
        loadedTitleText.value = null
        loadedLatinText.value = null
        loadedUmumPath.value = null
        loadedGejalaPath.value = null
        loadedResolvePath.value = null
        mKlikBawahText.value = "HELLO"
        currentBtnCount.value = CurrentBtnCondition.CASE_1
    }

    fun load(keyId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            finishedLoading.postValue(false)
            val panriDatabase = PanriDatabase.getInstance(getApplication())
            val gambarPenyakitDao = panriDatabase.listImageDiseaseDao()
            val diseaseDao = panriDatabase.listDiseaseDao()
            val pathImage = gambarPenyakitDao.getPathImageFrom(keyId).split(",")
            val list = mutableListOf<String>()
            for (imgPath in pathImage) {
                list.add("$imgPath.jpg")
            }
            loadedImagePath.postValue(list)
            //delay(100)
            val disease = diseaseDao.getDiseaseFromKey(keyId)
            loadedTitleText.postValue(disease?.nama)
            loadedLatinText.postValue(disease?.latin)

            val ctx = getApplication() as Context
            val file = File(ctx.filesDir, "data_hama_html")
            loadedUmumPath.postValue(loadFile(file, disease?.umumPath))
            loadedGejalaPath.postValue(loadFile(file, disease?.gejalaPath))
            loadedResolvePath.postValue(loadFile(file, disease?.caraAtasiPath))
            // load path

            delay(1000)
            finishedLoading.postValue(true)
        }
    }

    private fun loadFile(source: File, child: String?): Spanned? =
        child?.let {
            loadHtml(File(source, it).absolutePath)
        }

    fun increment() {
        // increment the case
        val value = currentBtnCount.value
        if (value == CurrentBtnCondition.CASE_1) setCase(CurrentBtnCondition.CASE_2)
        return
    }

    fun changeTextBottom(@IdRes resId: Int) {
        currentTextBottom.postValue(resId)
    }

    // true if success else if unsuccessfull
    fun decrement(): Boolean {
        val value = currentBtnCount.value
        if (value == CurrentBtnCondition.CASE_2) {
            setCase(CurrentBtnCondition.CASE_1)
            return true
        }
        return false
    }

    fun setCase(value: CurrentBtnCondition?) {
        value?.apply {
            currentBtnCount.postValue(this)
        }
    }

    @Parcelize
    data class BottomCardTextData(
        val btnCase1: String,
        val btnCase2: String
    ) : Parcelable

    enum class WebContentType {
        UMUM_TYPE,
        GEJALA_TYPE,
        RESOLVE_TYPE
    }

    enum class CurrentBtnCondition {
        CASE_1,
        CASE_2
    }
}