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
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import id.kenshiro.app.panri.R
import id.kenshiro.app.panri.params.TutorialFirstUseActivityParams
import org.jetbrains.anko.textColor
import org.jetbrains.annotations.NotNull

class FragmentFirstUse3 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.acttutor_main_fragment_firstuse_3, container, false)

        // section upper
        setTextAppearance(layout, R.id.frag3_id_txtatas, Typeface.BOLD, Gravity.CENTER)
        // section txt 1 section 1
        setTextAppearance(layout, R.id.frag3_id_txtim1, Typeface.BOLD, Gravity.START)
        // section txt 2 section 1
        setTextAppearance(layout, R.id.frag3_id_txtim2, Typeface.NORMAL, Gravity.START)
        // section txt 1 section 2
        setTextAppearance(layout, R.id.frag3_id_txtim21, Typeface.BOLD, Gravity.START)
        // section txt 2 section 2
        setTextAppearance(layout, R.id.frag3_id_txtim22, Typeface.NORMAL, Gravity.START)


        return layout
    }

    fun setTextAppearance(@NotNull layout: View, @IdRes resId: Int, typeOfTypeface: Int, gravity: Int) {
        val txt = layout.findViewById<TextView>(resId)
        txt.setTypeface(
            Typeface.createFromAsset(context?.assets, TutorialFirstUseActivityParams.TEXT_DEFAULT_FONT),
            typeOfTypeface
        )
        txt.textColor = Color.WHITE
        txt.gravity = gravity
    }

    companion object {
        fun newInstance(): Fragment {
            return FragmentFirstUse3()
        }
    }
}