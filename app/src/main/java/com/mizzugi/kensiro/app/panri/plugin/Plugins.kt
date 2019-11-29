package com.mizzugi.kensiro.app.panri.plugin

import android.content.res.AssetManager
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.mizzugi.kensiro.app.panri.BaseActivity
import org.xml.sax.XMLReader
import java.io.FileInputStream
import java.io.IOException

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun readFromAsset(assets: AssetManager, pathAssets: String): String? {
    try {
        val inputStream = assets.open(pathAssets)
        val b = ByteArray(inputStream.available())
        inputStream.read(b)
        inputStream.close()
        return String(b)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return null
}

fun destroyWebView(parentOfWebVIew: ViewGroup, content: WebView?) {
    // Make sure you remove the WebView from its parent view before doing anything.
    content?.apply {
        parentOfWebVIew.removeView(this)

        clearHistory()

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        clearCache(true)

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        loadUrl("about:blank")

        onPause()
        removeAllViews()

        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        pauseTimers()

        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        destroy()

        // Null out the reference so that you don't end up re-using it.
        System.gc()
    }
}

fun loadHtml(htmlPath: String): Spanned {
    val iStream = FileInputStream(htmlPath)
    val byte = ByteArray(iStream.available())
    iStream.read(byte)
    val str = String(byte)
    iStream.close()
    @Suppress("DEPRECATION")
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(
            str,
            Html.FROM_HTML_MODE_LEGACY,
            null,
            UlTagHandler()
        )
        else -> Html.fromHtml(str, null, UlTagHandler())
    }
}

private class UlTagHandler : Html.TagHandler {
    override fun handleTag(
        opening: Boolean, tag: String, output: Editable,
        xmlReader: XMLReader
    ) {
        if (tag == "ul" && opening) output.append("\n")
        if (tag == "li" && opening) output.append("\n\t•\t")
    }
}

fun replaceFragment(activity: BaseActivity, @IdRes layoutRes: Int, fragment: Fragment) {
    val backStackName = fragment.javaClass.name
    val manager = activity.supportFragmentManager
    if (!manager.popBackStackImmediate(backStackName, 0)) {
        manager.beginTransaction().apply {
            replace(layoutRes, fragment)
            addToBackStack(backStackName)
            commit()
        }
    }
}

fun Fragment.overrideBackPressed(): Boolean {
    return false
}