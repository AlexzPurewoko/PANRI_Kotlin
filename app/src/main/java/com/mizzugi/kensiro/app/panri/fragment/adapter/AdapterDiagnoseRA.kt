package com.mizzugi.kensiro.app.panri.fragment.adapter

import android.graphics.Color
import android.graphics.Point
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mizzugi.kensiro.app.panri.R

class AdapterDiagnoseRA(private val data: List<String>, private val point: Point) :
    RecyclerView.Adapter<AdapterDiagnoseRA.AdapterDiagnoseVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDiagnoseVH {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.cardview_adapter,
            parent,
            false
        ) as CardView
        layout.apply {
            val linearLayout = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
            }
            layoutParams = RecyclerView.LayoutParams(
                point.x,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            cardElevation = 8f
            setContentPadding(16, 16, 16, 16)
            radius = 8f
            useCompatPadding = true
            addView(linearLayout)
        }
        return AdapterDiagnoseVH(layout)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: AdapterDiagnoseVH, position: Int) {
        val text = data[position]
        holder.textView.text = text
    }


    class AdapterDiagnoseVH(view: View) : RecyclerView.ViewHolder(view) {
        val textView = TextView(view.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setTextColor(Color.parseColor("#000000"))
            val size = context.resources.getDimension(R.dimen.size_text_incard)
            textSize = size
            gravity = Gravity.CENTER_HORIZONTAL
        }

        init {
            ((view as CardView).getChildAt(0) as LinearLayout).addView(textView)
        }

    }
}