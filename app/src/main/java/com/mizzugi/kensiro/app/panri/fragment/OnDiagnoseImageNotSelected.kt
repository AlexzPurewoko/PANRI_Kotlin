package com.mizzugi.kensiro.app.panri.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.mizzugi.kensiro.app.panri.R

class OnDiagnoseImageNotSelected : Fragment() {

    var onBtnClicked: OnBtnDialogClicked? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        LayoutInflater.from(inflater.context).inflate(
            R.layout.fragment_onnotselected,
            container,
            false
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            findViewById<CardView>(R.id.layoutonselected_id_card_ya).setOnClickListener {
                onBtnClicked?.onClicked(ImageDiagnoseFragment.ImgDiagnoseBtnType.BTN_YES_CLICKED)
            }

            findViewById<CardView>(R.id.layoutonselected_id_card_tidak).setOnClickListener {
                onBtnClicked?.onClicked(ImageDiagnoseFragment.ImgDiagnoseBtnType.BTN_NO_CLICKED)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBtnClicked = context as OnBtnDialogClicked
    }

    override fun onDetach() {
        super.onDetach()
        onBtnClicked = null
    }

    interface OnBtnDialogClicked {
        fun onClicked(btnPosition: ImageDiagnoseFragment.ImgDiagnoseBtnType)
    }

}