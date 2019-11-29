package com.mizzugi.kensiro.app.panri.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mizzugi.kensiro.app.panri.fragment.DiagnoseHelperFragment
import com.mizzugi.kensiro.app.panri.roomDatabase.PanriDatabase
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.CiriCiriEntity
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.ListPenyakit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiagnoseHelperViewModel(application: Application) : AndroidViewModel(application) {

    val finishedLoading: MutableLiveData<Boolean> = MutableLiveData()
    val viewMode: MutableLiveData<ViewMode> = MutableLiveData()
    val modeListData: MutableLiveData<List<String>> = MutableLiveData()
    val currentAskStr: MutableLiveData<String> = MutableLiveData()
    val onDiseaseSelectedStatus: MutableLiveData<Boolean> = MutableLiveData()


    private var allCiriEntity: HashMap<Int, CiriCiriEntity> = hashMapOf()
    val tempListNum: MutableList<Int> = mutableListOf()
    private var savedTempListNum: MutableList<List<Int>> = mutableListOf()
    private val viewModeSaved: MutableList<ViewMode> = mutableListOf()

    private val savedBtnYesNoModes: MutableList<DiagnoseHelperFragment.OnBtnClickType> =
        mutableListOf()
    private var savedItemDataPosition = 0
    var countPositionData: Int = 0

    private var countBtnWhenAccept = 0
    private var countBtnWhenDecline = 0

    var currKeyId = 0
    var currPercentage: Double = 0.0

    var onFirst = true
    var allDiseaseName: List<ListPenyakit>? = null


    init {
        finishedLoading.value = false
    }

    fun setup() {
        viewModelScope.launch(Dispatchers.IO) {
            finishedLoading.postValue(false)
            val panriDatabase = PanriDatabase.getInstance(getApplication())
            val panriDisease = panriDatabase.ciriCiriDao()
            val allCiri = panriDisease.getAllCiri()
            allDiseaseName = panriDatabase.listDiseaseDao().getAllDiseases()

            for ((index, ciri) in allCiri.withIndex()) {
                allCiriEntity[index + 1] = ciri
            }
            allCiriEntity.forEach {
                it.value.setup()
            }
            loadFirst()
            delay(1000)
            finishedLoading.postValue(true)
        }
    }

    fun loadFirst() {
        val listUseFirst = mutableListOf<String>()
        tempListNum.clear()
        for ((index, ciriCiri) in allCiriEntity.entries) {
            if (ciriCiri.useFirst) {
                listUseFirst.add(ciriCiri.ciri)
                tempListNum.add(index)
            }
        }
        modeListData.postValue(listUseFirst)
        savedTempListNum.clear()
        viewModeSaved.clear()
        countBtnWhenAccept = 0
        countBtnWhenDecline = 0
        onFirst = true
        viewMode.postValue(ViewMode.VIEW_MODE_LIST)
    }

    fun onItemCardTouch(cardPosition: Int) {
        viewModelScope.launch {
            viewMode.value?.apply {
                countBtnWhenAccept++
                viewModeSaved.add(this)
                savedTempListNum.add(tempListNum.toList())
                val itemPosition = tempListNum[cardPosition]
                val currentVMode =
                    ViewMode.toViewMode(allCiriEntity[itemPosition]?.listUsedModeFlags)
                viewMode.postValue(currentVMode)

                if (countPositionData >= tempListNum.size || allCiriEntity[itemPosition]?.listUsedFlags.isNullOrEmpty()) {
                    val any = allCiriEntity[itemPosition]?.listUsedFlags?.isNotEmpty()
                    if (any == false) {
                        // end and gets the penyakit type
                        val resultsPenyakit =
                            allCiriEntity[itemPosition]?.listUsedFlags?.get(0) ?: 0
                        //saved_counter = counter;
                        val indexDisease =
                            allCiriEntity[itemPosition]?.pointo?.trim()?.split(",")?.let {
                                it[it.size - 1].toInt()
                            } ?: 0
                        currPercentage =
                            (countBtnWhenAccept * 100 / viewModeSaved.size).toDouble()//counter ;
                        currKeyId = indexDisease
                        onDiseaseSelectedStatus.postValue(true)
                        return@apply
                    }
                }

                tempListNum.clear()
                allCiriEntity[itemPosition]?.listUsedFlags?.apply {
                    tempListNum.addAll(this)
                }
                if (currentVMode == ViewMode.VIEW_MODE_LIST) {

                    val newData = mutableListOf<String>()
                    for (index in tempListNum) {
                        allCiriEntity[index]?.ciri?.let { newData.add(it) }

                    }
                    modeListData.postValue(newData)

                } else if (currentVMode == ViewMode.VIEW_MODE_ASK) {
                    // load another data
                    countPositionData = 0
                    // sets the textView
                    val ciri =
                        allCiriEntity[tempListNum[countPositionData]]?.ciri
                    currentAskStr.postValue(ciri)
                    System.gc()
                }

            }
        }
    }

    fun onBtnPressed(btnCondition: DiagnoseHelperFragment.OnBtnClickType) {
        var itemPosition: Int
        viewMode.value = ViewMode.VIEW_MODE_ASK
        countPositionData++
        when (btnCondition) {
            DiagnoseHelperFragment.OnBtnClickType.BTN_YES -> {
                countBtnWhenAccept++
            }
            DiagnoseHelperFragment.OnBtnClickType.BTN_NO -> countBtnWhenDecline++

        }
        savedBtnYesNoModes.add(btnCondition)
        savedTempListNum.add(tempListNum.toMutableList())
        viewMode.value?.apply {
            viewModeSaved.add(this)
        }

        if (countPositionData >= tempListNum.size) {
            itemPosition = tempListNum[countPositionData - 1]
            val any =
                allCiriEntity[tempListNum[countPositionData - 1]]?.listUsedFlags?.isNotEmpty()
            if (any == false) {
                // end and gets the penyakit type
                val resultsPenyakit =
                    allCiriEntity[itemPosition]?.listPointToFlags?.get(0)
                val indexDisease = allCiriEntity[itemPosition]?.pointo?.trim()?.split(",")?.let {
                    it[it.size - 1].toInt()
                } ?: 0
                currPercentage =
                    (countBtnWhenAccept * 100 / viewModeSaved.size).toDouble()//counter;
                //saved_counter = counter;
                currKeyId = indexDisease
                onDiseaseSelectedStatus.postValue(true)
                return
            }
            allCiriEntity[tempListNum[countPositionData - 1]]?.listUsedFlags?.let {

                tempListNum.clear()
                tempListNum.addAll(it)
            }
            countPositionData = 0
            viewMode.postValue(ViewMode.toViewMode(allCiriEntity[itemPosition]?.listUsedModeFlags))
        }
        itemPosition = tempListNum[countPositionData]
        savedItemDataPosition = countPositionData


        if (viewMode.value == ViewMode.VIEW_MODE_ASK) {
            // handling into the next ciri - ciri if possible
            if (countPositionData >= tempListNum.size) {
                tempListNum.clear()
                allCiriEntity[tempListNum[countPositionData - 1]]?.listUsedFlags?.let {
                    tempListNum.addAll(it)
                }
                countPositionData = 0
            }
            val ciri =
                allCiriEntity[tempListNum[countPositionData]]?.ciri
            currentAskStr.postValue(ciri)

        } else if (viewMode.value == ViewMode.VIEW_MODE_LIST) {
            // next into mode bind
            // load another lists
            tempListNum.clear()
            allCiriEntity[itemPosition]?.listUsedFlags?.let {
                tempListNum.addAll(it)
            }
            // change the content of data

            val newData = mutableListOf<String>()
            for (x in tempListNum) {
                allCiriEntity[x]?.ciri?.let {
                    newData.add(it)
                }
            }
            modeListData.postValue(newData)
        }
    }

    private fun onFinal(text: String) {

    }

    fun pushBack(): Boolean {
        val mCurrPosSaved = viewModeSaved.size - 1
        if (mCurrPosSaved <= -1) return false
        if (viewModeSaved.size == 1 && viewModeSaved.get(mCurrPosSaved) !== ViewMode.VIEW_MODE_ASK) {
            /*counter = */
            countBtnWhenDecline = 0
            countBtnWhenAccept = countBtnWhenDecline
            loadFirst()
            return true
        } else {
            if (viewModeSaved.get(mCurrPosSaved) === ViewMode.VIEW_MODE_LIST) {
                // load previous lists
                //counter--;
                countBtnWhenAccept--
                tempListNum.clear()
                tempListNum.addAll(savedTempListNum[mCurrPosSaved])
                // change the content of data

                val newData = mutableListOf<String>()
                for (index in tempListNum) {
                    allCiriEntity[index]?.ciri?.let { newData.add(it) }

                }
                modeListData.postValue(newData)
                viewMode.postValue(ViewMode.VIEW_MODE_LIST)
                System.gc()
            } else if (viewModeSaved.get(mCurrPosSaved) === ViewMode.VIEW_MODE_ASK) {
                if (savedItemDataPosition > 0)
                    savedItemDataPosition = --countPositionData
                if (viewModeSaved.size == 1) {
                    /*counter = */
                    countBtnWhenDecline = 0
                    countBtnWhenAccept = countBtnWhenDecline
                    loadFirst()
                    return true

                }
                //counter--;

                if (savedBtnYesNoModes.size > 0)
                    when (savedBtnYesNoModes.removeAt(savedBtnYesNoModes.size - 1)) {
                        DiagnoseHelperFragment.OnBtnClickType.BTN_YES -> countBtnWhenAccept--
                        DiagnoseHelperFragment.OnBtnClickType.BTN_NO -> countBtnWhenDecline--
                    }
                countPositionData = savedItemDataPosition
                val ciri =
                    allCiriEntity[tempListNum[countPositionData]]?.ciri

                currentAskStr.postValue(ciri)
                viewMode.postValue(ViewMode.VIEW_MODE_ASK)

                System.gc()
            }
            viewModeSaved.removeAt(mCurrPosSaved)
            savedTempListNum.removeAt(mCurrPosSaved)
            return true
        }
    }

    enum class ViewMode {
        VIEW_MODE_ASK,
        VIEW_MODE_LIST;

        companion object {
            fun toViewMode(entity: CiriCiriEntity.ListUsedMode?): ViewMode? =
                when (entity) {
                    CiriCiriEntity.ListUsedMode.MODE_SEQUENCE ->
                        VIEW_MODE_ASK
                    CiriCiriEntity.ListUsedMode.MODE_BIND -> VIEW_MODE_LIST
                    else -> null
                }
        }
    }
}