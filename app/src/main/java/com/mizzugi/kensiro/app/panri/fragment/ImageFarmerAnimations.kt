package com.mizzugi.kensiro.app.panri.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.viewmodel.ImageFarmerAnimViewModel

class ImageFarmerAnimations private constructor() : Fragment() {

    private lateinit var mImageFarmer: ImageView
    private lateinit var mTextFarmerDesc: Button
    var isFinished = false

    private var mViewModel: ImageFarmerAnimViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.petani_desc_content_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mImageFarmer = view.findViewById(R.id.fragment_id_gifFarmerTalk)
        mTextFarmerDesc = view.findViewById(R.id.fragment_id_sectionFarmerDesc)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(ImageFarmerAnimViewModel::class.java)
        mViewModel?.apply {
            requireArguments().apply {
                setAnimationTime(
                    getLong(TIME_BETWEEN_IMAGE),
                    getLong(TIME_AUTO_UPDATE_TEXT_MILLIS)
                )
            }
            currentFarmerAnim.observe(this@ImageFarmerAnimations, Observer {
                mImageFarmer.setImageDrawable(it)
            })
            currentTextFarmer.observe(this@ImageFarmerAnimations, Observer {
                mTextFarmerDesc.setText(it)
            })
        }
        mTextFarmerDesc.apply {
            setTextColor(Color.BLACK)
            typeface = Typeface.createFromAsset(requireContext().assets, "Comic_Sans_MS3.ttf")
            setOnClickListener {
                mViewModel?.stop()
                mViewModel?.clickTextDesc(false)
            }
        }
        mImageFarmer.setOnClickListener {
            mViewModel?.stop()
            mViewModel?.clickTextDesc(false)
        }
        isFinished = true
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume", "Playing.....")
        mViewModel?.play()
    }

    override fun onStop() {
        super.onStop()
        mViewModel?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel?.stop()
    }

    companion object {
        private const val TIME_AUTO_UPDATE_TEXT_MILLIS: String = "TIME_AUTO_UPDATE"
        private const val TIME_BETWEEN_IMAGE: String = "TIME_IMAGE"

        @JvmStatic
        fun newInstance(autoUpdateTextMillis: Long, timeBetweenImage: Long): ImageFarmerAnimations {
            return ImageFarmerAnimations().apply {
                arguments = Bundle().also {
                    it.putLong(TIME_BETWEEN_IMAGE, timeBetweenImage)
                    it.putLong(TIME_AUTO_UPDATE_TEXT_MILLIS, autoUpdateTextMillis)
                }
            }
        }
    }

}