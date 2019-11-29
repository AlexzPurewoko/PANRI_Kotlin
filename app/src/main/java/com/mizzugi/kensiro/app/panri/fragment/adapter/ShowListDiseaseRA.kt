package com.mizzugi.kensiro.app.panri.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mizzugi.kensiro.app.panri.R
import kotlin.math.roundToInt

class ShowListDiseaseRA(
    private val listDisease: List<ListDiseaseItem>
) : RecyclerView.Adapter<ShowListDiseaseRA.ShowListDiseaseRH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowListDiseaseRH =
        ShowListDiseaseRH(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_diseasename, parent, false)
        )

    override fun getItemCount(): Int = listDisease.size

    override fun onBindViewHolder(holder: ShowListDiseaseRH, position: Int) {
        holder.bind(listDisease[position])
    }

    data class ListDiseaseItem(
        val diseaseName: String,
        val diseaseImagePath: String // must be absolute path
    )

    class ShowListDiseaseRH(view: View) : RecyclerView.ViewHolder(view) {
        private val nameDisease: TextView = view.findViewById(R.id.adapter_id_namapenyakit)
        private val imageDisease: ImageView = view.findViewById(R.id.adapter_id_imgnamapenyakit)

        fun bind(disease: ListDiseaseItem) {
            val dimens =
                itemView.context.resources.getDimension(R.dimen.actmain_dimen_opimg_incard_wh)
                    .roundToInt()
            Glide.with(itemView.context).load(disease.diseaseImagePath)
                .apply(RequestOptions().override(dimens, dimens).centerCrop()).into(imageDisease)

            nameDisease.text = disease.diseaseName
        }
    }
}