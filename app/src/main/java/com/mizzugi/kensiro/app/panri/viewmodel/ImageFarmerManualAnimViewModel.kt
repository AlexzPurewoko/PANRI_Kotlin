package com.mizzugi.kensiro.app.panri.viewmodel

import android.app.Application
import android.content.Context
import android.util.LruCache
import androidx.annotation.RawRes
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mizzugi.kensiro.app.panri.R
import kotlinx.coroutines.*
import pl.droidsonroids.gif.GifDrawable

class ImageFarmerManualAnimViewModel(application: Application) : AndroidViewModel(application) {

    // Animation Time
    var animationTime: Long = 3000

    // farmer gif
    private var mGifPoolCache: LruCache<FarmerType, GifDrawable>? = null

    val currentFarmerAnim: MutableLiveData<GifDrawable?> = MutableLiveData()

    val currentTextFarmer: MutableLiveData<String> = MutableLiveData()

    // deferred for animations
    private var deferredFarmerAnimation: Deferred<Boolean>? = null

    private fun setDeferredFarmerAnimation(text: String) {
        deferredFarmerAnimation = GlobalScope.async {
            /*animationTime.value?.apply {
                delay(timeBetweenImage)
                if (isActive)
                    mGifPoolCache?.apply {
                        setFarmerAction(FarmerType.FARMER_TALK, FarmerAction.ACTION_STOP)
                        setFarmerType(FarmerType.FARMER_BLINK)
                        setFarmerAction(FarmerType.FARMER_BLINK, FarmerAction.ACTION_START)
                        return@async true
                    }
            }*/

            mGifPoolCache?.apply {
                currentTextFarmer.postValue(text)
                setFarmerAction(FarmerType.FARMER_BLINK, FarmerAction.ACTION_STOP)
                setFarmerType(FarmerType.FARMER_TALK)
                setFarmerAction(FarmerType.FARMER_TALK, FarmerAction.ACTION_START)
                delay(animationTime)
                if (isActive) {
                    setFarmerAction(FarmerType.FARMER_TALK, FarmerAction.ACTION_STOP)
                    setFarmerType(FarmerType.FARMER_BLINK)
                    setFarmerAction(FarmerType.FARMER_BLINK, FarmerAction.ACTION_START)
                }
            }
            false
        }

        deferredFarmerAnimation?.start()
    }

    private fun clearDeferredFarmerAnimation() {
        if (deferredFarmerAnimation?.isActive == true) {
            setFarmerAction(FarmerType.FARMER_TALK, FarmerAction.ACTION_STOP)
            setFarmerType(FarmerType.FARMER_BLINK)
            setFarmerAction(FarmerType.FARMER_BLINK, FarmerAction.ACTION_START)
            deferredFarmerAnimation?.cancel()
            deferredFarmerAnimation = null
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadImages()
            setFarmerType(FarmerType.FARMER_BLINK)
            setFarmerAction(FarmerType.FARMER_BLINK, FarmerAction.ACTION_START)
        }
    }

    @Suppress("SameParameterValue")
    private fun setFarmerAction(farmerType: FarmerType, farmerAction: FarmerAction) {
        mGifPoolCache?.get(farmerType)?.apply {
            when (farmerAction) {
                FarmerAction.ACTION_START -> start()
                FarmerAction.ACTION_STOP -> stop()
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun setFarmerType(farmerType: FarmerType) {
        currentFarmerAnim.postValue(mGifPoolCache?.get(farmerType))
    }

    fun update(text: String) {
        //Log.d("Hekekefdkedf", "Has Started? = ${deferredFarmerAnimation.isCompleted}")
        setDeferredFarmerAnimation(text)
    }

    fun stop() {
        clearDeferredFarmerAnimation()
    }

    /*
        fun clickTextDesc(isClickedOnStart: Boolean) {
            viewModelScope.launch {
                //if(autoClickUpdateStatus) {
                //if (listTextFarmerDesc.isEmpty()) return@launch
                if (!isClickedOnStart) {
                    if (++currentPos == textFarmerDesc.size)
                        currentPos = 0
                } else {
                    currentPos = 0
                }
                currentTextFarmer.postValue(textFarmerDesc[currentPos])
                //}
                setFarmerAction(FarmerType.FARMER_BLINK, FarmerAction.ACTION_STOP)
                setFarmerType(FarmerType.FARMER_TALK)
                setFarmerAction(FarmerType.FARMER_TALK, FarmerAction.ACTION_START)
                setDeferredFarmerAnimation()
                //val started = deferredFarmerAnimation.start()
                //Log.d("DeferredFarmerJob", "Start Farmer Animation $started")
            }
        }
    */
    @Suppress("ReplaceWithEnumMap")
    @WorkerThread
    private fun loadImages() {
        val resources = (getApplication() as Context).resources
        val listOfByte: HashMap<FarmerType, ByteArray> = hashMapOf()
        var countSize = 0
        FarmerType.values().forEach {
            val inputStream = resources.openRawResource(it.gifId)
            listOfByte[it] =
                ByteArray(inputStream.available()).apply {
                    countSize += size
                    inputStream.read(this)
                }
            inputStream.close()
        }

        mGifPoolCache = LruCache(countSize * listOfByte.size)
        listOfByte.forEach {
            mGifPoolCache?.put(it.key, GifDrawable(it.value).apply { stop() })
        }
        listOfByte.clear()
    }

    override fun onCleared() {
        mGifPoolCache?.apply {
            FarmerType.values().forEach {
                get(it).recycle()
            }
            evictAll()
        }
    }


    enum class FarmerType(@RawRes val gifId: Int) {
        FARMER_TALK(R.raw.petani_bicara),
        FARMER_BLINK(R.raw.petani_kedip)
    }

    enum class FarmerAction {
        ACTION_START,
        ACTION_STOP
    }

    companion object {
        const val TIME_BETWEEN_IMAGE: Long = 10000
        const val TIME_AUTO_UPDATE_TEXT_MILLIS: Long = 6000 // 5s
    }
}