package com.example.imagetranslater.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetranslater.R
import com.example.imagetranslater.datasource.recentlanguages.EntityRecentLanguages
import com.example.imagetranslater.interfaces.DeleteCallBack
import com.example.imagetranslater.model.ModelLanguage
import com.example.imagetranslater.utils.AppPreferences.funAddString
import com.example.imagetranslater.utils.AppPreferences.funGetString
import com.example.imagetranslater.viewmodel.ViewModelResultLanguages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*

class AdapterLanguages(
    var context: Context,
    val viewModel: ViewModelResultLanguages,
    private val list: MutableList<ModelLanguage>,
    private var recentLanguageKey: String,
    private var languageCodeKey: String?,
    private var languageNameKey: String?
) : RecyclerView.Adapter<AdapterLanguages.LanguageViewHolder>(), Filterable {

    class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewFlag: ImageView = itemView.findViewById(R.id.imageViewFlag)
        val textViewLanguageName: TextView = itemView.findViewById(R.id.textViewLanguageName)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)


    }


    private var fullList: List<ModelLanguage>? = ArrayList(list)

    private var callBack: DeleteCallBack? = null
    fun funSetCallBack(callBack: DeleteCallBack) {
        this.callBack = callBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_languages_item, parent,
            false
        )
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val model = list[position]
        val code = model.code
        holder.textViewLanguageName.text = model.name


        holder.checkbox.isChecked = code.equals(
            context.funGetString(languageCodeKey!!, "en")
        )

        holder.itemView.setOnClickListener {
            callBack!!.call(position)
            //one name contains
            context.funAddString(recentLanguageKey, list[position].name!!)
            //
            runBlocking(Dispatchers.IO) {
                if (viewModel.isLangExists(
                        list[position].code.toString(),
                        list[position].name.toString()
                    ) == 0
                ) {
                    if (viewModel.entriesCount() > 4) {
                        val modelRecentLanguages = viewModel.getFirstRow()
                        viewModel.funDelete(modelRecentLanguages)
                    }
                    val model1 = EntityRecentLanguages(
                        list[position].code.toString(),
                        list[position].name.toString()
                    )
                    viewModel.funInsert(model1)
                }
            }
            context.funAddString(languageCodeKey!!, list[position].code!!)
            context.funAddString(languageNameKey!!, list[position].name!!)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        return myFilter
    }

    private var myFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<ModelLanguage>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(fullList!!)
            } else {
                val string = constraint.toString().lowercase()
                for (i in fullList!!.indices) {
                    val app: String = fullList!![i].name!!
                    if (app.lowercase().startsWith(string)) {
                        filteredList.add(fullList!![i])
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            list.clear()
            list.addAll(results.values as Collection<ModelLanguage>)
            notifyDataSetChanged()
        }
    }
}