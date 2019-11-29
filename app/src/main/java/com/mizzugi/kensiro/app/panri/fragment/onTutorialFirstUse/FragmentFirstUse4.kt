package com.mizzugi.kensiro.app.panri.fragment.onTutorialFirstUse

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mizzugi.kensiro.app.panri.R


class FragmentFirstUse4 : Fragment() {


    var onFinalRequests: OnFragmentLastFinalRequest? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.acttutor_main_fragment_firstuse_4, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.frag4_id_txtatas).apply {
            setTypeface(
                Typeface.createFromAsset(requireContext().assets, "Comic_Sans_MS3.ttf"),
                Typeface.BOLD
            )
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            textSize = 15f
        }

        view.findViewById<Button>(R.id.frag4_id_img).setOnClickListener {
            onFinalRequests?.onBtnFinalClicked(this, it)
        }
    }

    interface OnFragmentLastFinalRequest {
        fun onBtnFinalClicked(results: Fragment, btn: View)
    }
}