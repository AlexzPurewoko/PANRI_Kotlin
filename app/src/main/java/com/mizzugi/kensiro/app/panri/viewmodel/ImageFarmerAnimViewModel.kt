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

class ImageFarmerAnimViewModel(application: Application) : AndroidViewModel(application) {

    // Animation Time
    private val animationTime: MutableLiveData<FarmerAnimationTime> = MutableLiveData()

    // farmer gif
    private var mGifPoolCache: LruCache<FarmerType, GifDrawable>? = null

    val currentFarmerAnim: MutableLiveData<GifDrawable?> = MutableLiveData()

    // farmer desc
    private val textFarmerDesc = intArrayOf(
        R.string.actmain_string_speechfarmer_1,
        R.string.actmain_string_speechfarmer_2,
        R.string.actmain_string_speechfarmer_3,
        R.string.actmain_string_speechfarmer_4,
        R.string.actmain_string_speechfarmer_5
    )

    var currentPos: Int = 0
    val currentTextFarmer: MutableLiveData<Int> = MutableLiveData()

    // deferred for animations
    private var deferredFarmerAnimation: Deferred<Boolean>? = null

    private var deferredAutoClickAnimation: Deferred<Boolean>? = null

    fun setDeferredAutoClickAnimation() {
        deferredAutoClickAnimation = GlobalScope.async {
            while (isActive) {
                animationTime.value?.apply {
                    delay(autoTextUpdateMillis)
                }
                clickTextDesc(false)
            }
            false
        }
        deferredAutoClickAnimation?.start()
    }

    fun clearDeferredAutoClickAnimation() {
        deferredAutoClickAnimation?.cancel()
        deferredAutoClickAnimation = null
    }

    private fun setDeferredFarmerAnimation() {
        deferredFarmerAnimation = GlobalScope.async {
            animationTime.value?.apply {
                delay(timeBetweenImage)
                if (isActive)
                    mGifPoolCache?.apply {
                        setFarmerAction(FarmerType.FARMER_TALK, FarmerAction.ACTION_STOP)
                        setFarmerType(FarmerType.FARMER_BLINK)
                        setFarmerAction(FarmerType.FARMER_BLINK, FarmerAction.ACTION_START)
                        return@async true
                    }
            }
            false
        }

        deferredFarmerAnimation?.start()
    }

    private fun clearDeferredFarmerAnimation() {
        deferredFarmerAnimation?.cancel()
        deferredFarmerAnimation = null
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadImages()
            setFarmerType(FarmerType.FARMER_BLINK)
            setFarmerAction(FarmerType.FARMER_BLINK, FarmerAction.ACTION_START)
        }
        currentTextFarmer.value = textFarmerDesc[currentPos]
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

    fun play() {
        //Log.d("Hekekefdkedf", "Has Started? = ${deferredFarmerAnimation.isCompleted}")
        setDeferredFarmerAnimation()
        clickTextDesc(true)
        //deferredAutoClickAnimation.start()
        setDeferredAutoClickAnimation()
    }

    fun stop() {
        //deferredAutoClickAnimation.cancel()
        clearDeferredAutoClickAnimation()
        clearDeferredFarmerAnimation()
    }

    fun setAnimationTime(timeBetweenImage: Long, autoTextUpdateMillis: Long) {
        animationTime.value = FarmerAnimationTime(timeBetweenImage, autoTextUpdateMillis)
    }


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

    data class FarmerAnimationTime(
        val timeBetweenImage: Long,
        val autoTextUpdateMillis: Long
    )
}