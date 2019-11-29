package com.mizzugi.kensiro.app.panri.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.text.Spanned
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.WorkerThread
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.roomDatabase.PanriDatabase
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.ListPenyakit
import kotlinx.coroutines.*
import java.io.File

class ImageDiagnoseViewModel(application: Application) : AndroidViewModel(application) {

    val titleText: MutableLiveData<String> = MutableLiveData()
    val contentText: MutableLiveData<Spanned> = MutableLiveData()
    val finishedLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataImageID: MutableLiveData<List<String>> = MutableLiveData()
    val isOnLast: MutableLiveData<Boolean> = MutableLiveData()
    val currentIndicatorPosition: MutableLiveData<Int> = MutableLiveData()
    var timeUpdateBetweenImage: Long = 6000
    private var deferredAutoUpdate: Deferred<Boolean>? = null
    var mCurrentPos = 0

    var dataCiriDisease: ListPenyakit? = null
    var mDataSize: Int = 0
    val pathDir: String
        get() = File((getApplication() as Context).filesDir, "data/images/diagnose").absolutePath


    init {
        isOnLast.value = false
        currentIndicatorPosition.value = 0
    }

    fun play() {
        if (deferredAutoUpdate == null) {
            deferredAutoUpdate = GlobalScope.async {
                while (isActive) {
                    delay(timeUpdateBetweenImage)
                    if (!isActive) break
                    var currentPos = currentIndicatorPosition.value ?: 0
                    currentIndicatorPosition.postValue(
                        if (++currentPos >= dataImageID.value?.size ?: 0) 0
                        else currentPos
                    )
                }
                false
            }
            deferredAutoUpdate?.start()
        } else {
            Log.e(
                "AutoUpdateImageDiagnose",
                "You must stop the animation running first then play it again!"
            )
            throw IllegalAccessException("AutoUpdateImageDiagnose: The Animation is currently playing, You must stop first, then play it again")
        }
    }

    fun stop() {
        deferredAutoUpdate?.cancel()
        deferredAutoUpdate = null
    }

    fun load(keyId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            stop()
            finishedLoading.postValue(false)
            fetchDataFromDb(keyId)
            titleText.postValue(dataCiriDisease?.nama)
            contentText.postValue(dataCiriDisease?.listCiriHtmlEncoded)
            delay(1000)
            finishedLoading.postValue(true)
        }
    }

    @WorkerThread
    fun fetchDataFromDb(position: Int) {
        val panriDatabase = PanriDatabase.getInstance(getApplication())
        mDataSize = panriDatabase.listDiseaseDao().dataCount()
        dataCiriDisease = panriDatabase.listDiseaseDao().getDiseaseFromKey(position)
        val h = panriDatabase.listImageDiseaseIdDao().getImageIDFromPosition(position)
        dataImageID.postValue(getImagePath(h, pathDir))

    }

    fun getImagePath(listId: String, pathImgFiles: String): List<String> {
        val list = listId.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val nList = mutableListOf<String>()
        for (item in list) {
            nList.add("$pathImgFiles/$item.jpg")
        }
        return nList.toList()
    }

    fun setSelectedIndicator(mDot: LinearLayout) {
        mDot.setBackgroundResource(IndicatorType.SELECTED.drawableRes)
    }

    fun setUnselectedIndicator(mDot: LinearLayout) {
        mDot.setBackgroundResource(IndicatorType.UNSELECTED.drawableRes)
    }

    @SuppressLint("RtlHardcoded")
    fun initializeIndicatorDots(
        activity: FragmentActivity,
        mDotsReference: MutableList<LinearLayout>,
        indicatorHolder: LinearLayout
    ) {
        for (i in 0 until (dataImageID.value?.size ?: 0)) {
            val layouts = LinearLayout(activity).apply {
                setBackgroundResource(IndicatorType.UNSELECTED.drawableRes)
                gravity = Gravity.RIGHT or Gravity.BOTTOM
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 4, 4)
            }
            mDotsReference.add(layouts)
            indicatorHolder.addView(layouts, params)
        }
        setSelectedIndicator(mDotsReference[0])
    }

    fun click() {
        if (mDataSize != 0 && mCurrentPos + 1 > mDataSize) {
            isOnLast.postValue(true)
            return
        }
        load(++mCurrentPos)
    }

    fun clickImage() {
        if (dataImageID.value.isNullOrEmpty()) return
        GlobalScope.launch {
            stop()
            var currentPos = currentIndicatorPosition.value ?: 0
            if (++currentPos >= (dataImageID.value?.size ?: 0))
                currentPos = 0
            currentIndicatorPosition.postValue(currentPos)
            play()
        }
    }

    fun back(): Boolean {
        if (mDataSize <= 0 || mCurrentPos == 1) return false
        load(--mCurrentPos)
        return true
    }

    fun resetCounter() {
        mCurrentPos = 1
        load(mCurrentPos)
    }

    enum class IndicatorType(@DrawableRes val drawableRes: Int) {
        UNSELECTED(R.drawable.indicator_unselected_item_oval),
        SELECTED(R.drawable.indicator_selected_item_oval)
    }
}