/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.params

import id.kenshiro.app.panri.R

object TutorialFirstUseActivityParams {
    const val ON_BTN_BACK_CLICKED = 0xaaf
    const val ON_BTN_NEXT_CLICKED = 0xbeef
    const val TEXT_DEFAULT_FONT = PublicConfig.Assets.COMIC_SANS_MS3_FONT
    const val TEXT_FRAGMENT1_CONTENT_SIZE: Float = 15f
    const val TEXT_FRAGMENT2_CONTENT_SIZE: Float = 13f
    const val TEXT_FRAGMENT4_CONTENT_SIZE: Float = TEXT_FRAGMENT1_CONTENT_SIZE


    const val COUNT_ALL_FRAGMENTS = 4
    const val DOT_COUNT_INDICATORS = COUNT_ALL_FRAGMENTS
    val COLOR_RES_LISTS = intArrayOf(
        R.color.frag_color_green,
        R.color.frag_color_green,
        R.color.colorPrimary,
        R.color.colorPrimary
    ) // must be same size as COUNT_ALL_FRAGMENTS
    val INDICATOR_DRAWABLE_SELECTION = intArrayOf(
        R.drawable.indicator_selected_item_oval_green,
        R.drawable.indicator_selected_item_oval_green,
        R.drawable.indicator_selected_item_oval,
        R.drawable.indicator_selected_item_oval
    ) // must be same size as COUNT_ALL_FRAGMENTS
    val LIST_STATUS_BAR_COLORS = intArrayOf(
        R.color.frag_color_green_dark,
        R.color.frag_color_green_dark,
        R.color.colorPrimaryDark,
        R.color.colorPrimaryDark
    ) // must be same size as COUNT_ALL_FRAGMENTS
    const val ON_FINALIZE_BTN_CLICKED: Int = 0xc4a
}