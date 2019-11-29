package com.mizzugi.kensiro.app.panri.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mizzugi.kensiro.app.panri.roomDatabase.PanriDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class GalleryActivityViewModel(application: Application) : AndroidViewModel(application) {
    val dataPathImage: MutableLiveData<List<String>> = MutableLiveData()
    val finishedLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun loadImage() {
        viewModelScope.launch(Dispatchers.IO) {
            finishedLoading.postValue(false)
            val panriDatabase = PanriDatabase.getInstance(getApplication())
            val dataDao = panriDatabase.listImageDiseaseDao()
            val data = dataDao.getAllDiseaseImage()
            val listData = mutableListOf<String>()
            val defaultImageDir = File((getApplication() as Context).filesDir, "data/images/list")
            data.forEach { entity ->
                entity.pathGambar.split(",").forEach {
                    listData.add("$it.jpg")
                }
            }
            dataPathImage.postValue(listData.toList())
            delay(1000)
            finishedLoading.postValue(true)
        }
    }

}