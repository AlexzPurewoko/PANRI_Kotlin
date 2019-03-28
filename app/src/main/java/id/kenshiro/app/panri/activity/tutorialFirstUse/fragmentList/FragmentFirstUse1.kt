/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.activity.tutorialFirstUse.fragmentList

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import id.kenshiro.app.panri.R
import id.kenshiro.app.panri.params.TutorialFirstUseActivityParams
import org.jetbrains.anko.textColor

class FragmentFirstUse1 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.acttutor_main_fragment_firstuse_1, container, false)
        val textView = layout.findViewById<TextView>(R.id.frag1_id_textview)
        textView.typeface = Typeface.createFromAsset(context?.assets, TutorialFirstUseActivityParams.TEXT_DEFAULT_FONT)
        textView.textColor = Color.WHITE
        textView.gravity = Gravity.CENTER
        textView.textSize = TutorialFirstUseActivityParams.TEXT_FRAGMENT1_CONTENT_SIZE
        return layout
    }

    companion object {
        fun newInstance(): Fragment {
            return FragmentFirstUse1()
        }
    }
}