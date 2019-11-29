package com.mizzugi.kensiro.app.panri.fragment.onTutorialFirstUse

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.mizzugi.kensiro.app.panri.R


class FragmentFirstUse3 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.acttutor_main_fragment_firstuse_3, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setTextAppearanceLayout(view, R.id.frag3_id_txtatas, Typeface.BOLD, Gravity.CENTER)
        // section txt 1 section 1
        setTextAppearanceLayout(view, R.id.frag3_id_txtim1, Typeface.BOLD, Gravity.START)
        // section txt 2 section 1
        setTextAppearanceLayout(view, R.id.frag3_id_txtim2, Typeface.NORMAL, Gravity.START)
        // section txt 1 section 2
        setTextAppearanceLayout(view, R.id.frag3_id_txtim21, Typeface.BOLD, Gravity.START)
        // section txt 2 section 2
        setTextAppearanceLayout(view, R.id.frag3_id_txtim22, Typeface.NORMAL, Gravity.START)
    }

    private fun setTextAppearanceLayout(
        layout: View, @IdRes resId: Int, typefaceType: Int,
        gravity: Int
    ) {
        layout.findViewById<TextView>(resId).apply {
            setTypeface(
                Typeface.createFromAsset(requireContext().assets, "Comic_Sans_MS3.ttf"),
                typefaceType
            )
            setTextColor(Color.WHITE)
            setGravity(gravity)
        }
    }
}