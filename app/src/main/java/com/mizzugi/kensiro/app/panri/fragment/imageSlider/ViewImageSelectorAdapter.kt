package com.mizzugi.kensiro.app.panri.fragment.imageSlider

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mizzugi.kensiro.app.panri.R

class ViewImageSelectorAdapter : Fragment() {

    private var imageLocation: ImageLocation? = null
    private var resImageLocation: Int = 0
    private var assetsImgLocation: String? = null
    var requestedImageSize = Point()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_item_viewpager, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = view.findViewById<ImageView>(R.id.actmain_id_fragmentimgselector)
        when (imageLocation) {
            ImageLocation.ASSETS ->
                Glide.with(requireContext()).load(assetsImgLocation).apply(
                    RequestOptions().override(
                        requestedImageSize.x,
                        requestedImageSize.y
                    )
                ).into(image)
            ImageLocation.RESOURCES ->
                Glide.with(requireContext()).load(resImageLocation).apply(
                    RequestOptions().override(
                        requestedImageSize.x,
                        requestedImageSize.y
                    )
                ).into(image)
        }
    }

    fun setWithResourceImage(@DrawableRes resId: Int) {
        imageLocation = ImageLocation.RESOURCES
        resImageLocation = resId
    }

    fun setWithAssetsImage(assetPath: String) {
        imageLocation = ImageLocation.ASSETS
        assetsImgLocation = assetPath
    }


    enum class ImageLocation {
        ASSETS,
        RESOURCES
    }
}