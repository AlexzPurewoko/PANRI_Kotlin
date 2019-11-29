package com.mizzugi.kensiro.app.panri.fragment.onTutorialFirstUse

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mizzugi.kensiro.app.panri.R

class FragmentFirstUse1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.acttutor_main_fragment_firstuse_1, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.frag1_id_textview).apply {
            setTypeface(
                Typeface.createFromAsset(requireContext().assets, "Comic_Sans_MS3.ttf"),
                Typeface.BOLD
            )
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            textSize = 15f
        }
    }
}