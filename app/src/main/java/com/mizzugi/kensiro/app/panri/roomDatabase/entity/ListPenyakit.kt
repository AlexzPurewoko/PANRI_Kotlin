package com.mizzugi.kensiro.app.panri.roomDatabase.entity

import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import org.xml.sax.XMLReader

@Entity(tableName = PublicContract.DatabaseContract.TABLE_LIST_DISEASE)
data class ListPenyakit(

    @PrimaryKey
    @ColumnInfo(name = "num")
    val num: Int,

    @ColumnInfo(name = "nama")
    val nama: String,
    @ColumnInfo(name = "latin")
    val latin: String?,
    @ColumnInfo(name = "umum_path")
    val umumPath: String?,
    @ColumnInfo(name = "gejala_path")
    val gejalaPath: String?,
    @ColumnInfo(name = "cara_atasi_path")
    val caraAtasiPath: String?,
    @ColumnInfo(name = "listciri")
    val listCiri: String?
) {
    val listCiriHtmlEncoded: Spanned?
        get() {
            listCiri?.apply {
                val list = split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val bufHtml = StringBuffer().apply {
                    append("<font color=\"black\" size=\"5pt\">")
                    append("<b>Ciri - ciri : </b>")
                    append("</font>")
                    append("<ul>")
                    //bufHtml.append("<li>");
                    for (ciri in list) {
                        append("<li>")
                        append(ciri)
                        append("</li>")
                    }

                    append("</ul>")
                }
                @Suppress("DEPRECATION")
                return when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(
                        bufHtml.toString(),
                        Html.FROM_HTML_MODE_LEGACY,
                        null,
                        UlTagHandler()
                    )
                    else -> Html.fromHtml(bufHtml.toString(), null, UlTagHandler())
                }
            }
            return null
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

}