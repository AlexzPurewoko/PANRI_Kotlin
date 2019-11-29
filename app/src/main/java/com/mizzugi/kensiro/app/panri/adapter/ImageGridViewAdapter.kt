package com.mizzugi.kensiro.app.panri.adapter

import android.content.Context
import android.graphics.Point
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.Px
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import kotlin.math.roundToInt

class ImageGridViewAdapter(
    context: Context,
    private val reqSize: Point,
    private val imageDirPath: File,
    private val rootElement: LinearLayout
) {
    private var listLocationResImages: List<Int> = mutableListOf()
    private var listLocationAssetsImages: List<String> = mutableListOf()
    private var imageItemSize: Point = Point(0, 0)
    private var ctx: Context = context
    var columnCount = 2
    private var rowCount: Int = 0
    var onItemClickListener: OnItemClickListener? = null

    private var marginTop = 20
    private var marginBottom = 20
    private var marginLeft = 15
    private var marginRight = 15
    private var marginCenter = 15
    private var mode = 0
    private var idSuffix: String? = null
    private var contentPadding = 0

    private val itemCount: Int
        get() {
            return if (mode == 0)
                listLocationResImages.size
            else
                listLocationAssetsImages.size
        }

    fun setMargin(@Px marginTop: Int, @Px marginBottom: Int, @Px marginLeft: Int, @Px marginRight: Int, @Px marginCenter: Int, @Px contentPadding: Int) {
        this.marginBottom = marginBottom
        this.marginTop = marginTop
        this.marginLeft = marginLeft
        this.marginRight = marginRight
        this.marginCenter = marginCenter
        this.contentPadding = contentPadding
    }

    fun setListLocationFileImages(listLocationFileImages: List<String>, idSuffix: String) {
        //rootElement.removeAllViews()
        this.listLocationAssetsImages = listLocationFileImages
        this.idSuffix = idSuffix
        mode = 2
        System.gc()
    }

    fun setListLocationAssetsImages(listLocationAssetsImages: List<String>, idSuffix: String) {
        this.listLocationAssetsImages = listLocationAssetsImages
        this.idSuffix = idSuffix
        mode = 1
    }

    fun setListLocationResImages(listLocationResImages: List<Int>, idSuffix: String) {
        this.listLocationResImages = listLocationResImages
        this.idSuffix = idSuffix
        mode = 0
    }

    fun setImagePerItemHeight(newHeight: Int) {
        imageItemSize.y = newHeight
    }

    fun buildAndShow() {
        prepare()
        buildContentLayout()
        //new TaskLoadingBitmap(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private fun prepare() {
        imageItemSize.x =
            reqSize.x / columnCount - (marginLeft + marginRight / columnCount) - contentPadding
        imageItemSize.y =
            if (imageItemSize.y == 0) imageItemSize.x
            else imageItemSize.y

    }

    private fun buildContentLayout() {
        // gets the row count
        rowCount = (itemCount / columnCount).toFloat().roundToInt()

        var items = 0
        for (x in 0 until rowCount) {
            val element = LinearLayout(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    if (x + 1 == rowCount)
                        it.setMargins(0, marginTop, 0, marginBottom)
                    else
                        it.setMargins(0, marginTop, 0, 0)
                }
                gravity = Gravity.CENTER
            }

            for (y in 0 until columnCount) {
                val img = ImageView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        imageItemSize.x,
                        imageItemSize.y
                    ).also {
                        when {
                            columnCount > 1 && y + 1 == columnCount -> {
                                it.rightMargin = marginRight
                                it.leftMargin = 0
                            }
                            columnCount > 1 && y < columnCount -> {
                                if (y == 0)
                                    it.leftMargin = marginLeft
                                else
                                    it.leftMargin = 0
                                it.rightMargin = marginCenter
                            }
                            else -> {
                                it.rightMargin = marginRight
                                it.leftMargin = marginLeft
                            }
                        }
                    }
                    maxHeight = imageItemSize.y
                    maxWidth = imageItemSize.x
                    scaleType = ImageView.ScaleType.FIT_XY
                    adjustViewBounds = false
                    applyImages(this, items)
                    val inItems = items++
                    setOnClickListener {
                        onItemClickListener?.onItemClick(it, inItems)
                    }

                }
                element.addView(img)
            }
            rootElement.addView(element)
        }
    }

    private fun applyImages(imageView: ImageView, items: Int) {
        when (mode) {
            0 -> {
            }
            1 -> {
            }
            2 -> {
                val file = File(imageDirPath, listLocationAssetsImages[items])
                Glide.with(ctx).load(file).apply(
                    RequestOptions().override(
                        imageItemSize.x,
                        imageItemSize.y
                    ).encodeQuality(90)
                ).into(imageView)
            }
        }
    }

    private fun buildRootLayout() {

    }


    interface OnItemClickListener {
        fun onItemClick(v: View, position: Int)
    }
}