package com.mizzugi.kensiro.app.panri.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mizzugi.kensiro.app.panri.fragment.adapter.ShowListDiseaseRA
import com.mizzugi.kensiro.app.panri.roomDatabase.PanriDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

class ShowListDiseaseViewModel(application: Application) : AndroidViewModel(application) {
    val listDiseaseViewModel: MutableLiveData<List<ShowListDiseaseRA.ListDiseaseItem>> =
        MutableLiveData()
    val finishedLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        finishedLoading.value = false
    }

    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            finishedLoading.postValue(false)
            val db = PanriDatabase.getInstance(getApplication())
            val imageDiseaseDao = db.listImageDiseaseDao()
            val diseaseDao = db.listDiseaseDao()

            val countImage = imageDiseaseDao.getAvailCountImage()
            val pathImage = imageDiseaseDao.getAvailImage()
            val diseasesName = diseaseDao.getAllDiseasesName()

            val dataDiseases = mutableListOf<ShowListDiseaseRA.ListDiseaseItem>()

            val ctx = getApplication() as Context
            for ((index, diseases) in diseasesName.withIndex()) {
                val lDis = pathImage[index].split(",")
                val pos = Random(countImage[index]).nextInt(countImage[index])

                val absolutePathImage =
                    File(ctx.filesDir, "data/images/list/${lDis[pos]}.jpg").absolutePath
                dataDiseases.add(ShowListDiseaseRA.ListDiseaseItem(diseases, absolutePathImage))
            }

            listDiseaseViewModel.postValue(dataDiseases)
            delay(500)
            finishedLoading.postValue(true)
        }
    }
}