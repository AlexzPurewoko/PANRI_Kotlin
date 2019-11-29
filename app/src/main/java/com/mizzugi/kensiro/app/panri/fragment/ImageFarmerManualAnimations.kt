package com.mizzugi.kensiro.app.panri.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.viewmodel.ImageFarmerManualAnimViewModel


class ImageFarmerManualAnimations private constructor() : Fragment() {

    private lateinit var mImageFarmer: ImageView
    private lateinit var mTextFarmerDesc: Button
    var isFinished = false

    private var mViewModel: ImageFarmerManualAnimViewModel? = null

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
        mViewModel = ViewModelProviders.of(this).get(ImageFarmerManualAnimViewModel::class.java)
        mViewModel?.apply {
            requireArguments().apply {
                animationTime = getLong(TIME_BETWEEN_IMAGE)
            }
            currentFarmerAnim.observe(this@ImageFarmerManualAnimations, Observer {
                mImageFarmer.setImageDrawable(it)
            })
            currentTextFarmer.observe(this@ImageFarmerManualAnimations, Observer {
                mTextFarmerDesc.text = it
            })
        }
        mTextFarmerDesc.apply {
            setTextColor(Color.BLACK)
            typeface = Typeface.createFromAsset(requireContext().assets, "Comic_Sans_MS3.ttf")
        }
        isFinished = true
    }

    fun update(text: String) {
        mViewModel?.stop()
        mViewModel?.update(text)
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
        private const val TIME_BETWEEN_IMAGE: String = "TIME_IMAGE"

        @JvmStatic
        fun newInstance(timeBetweenImage: Long): ImageFarmerManualAnimations {
            return ImageFarmerManualAnimations().apply {
                arguments = Bundle().also {
                    it.putLong(TIME_BETWEEN_IMAGE, timeBetweenImage)
                }
            }
        }
    }

}