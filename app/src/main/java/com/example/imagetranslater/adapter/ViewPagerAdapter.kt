package com.example.imagetranslater.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetranslater.R
import com.example.imagetranslater.model.ModelSavedImages
import com.example.imagetranslater.viewmodel.VMPinned
import com.example.imagetranslater.viewmodel.VMRecent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ViewPagerAdapter(
    val context: Context,
    private val vmRecent: VMRecent,
    private val vmPinned: VMPinned
) :
    RecyclerView.Adapter<ViewPagerAdapter.MyViewHolder>() {

    val list = mutableListOf("Recent", "Pinned")

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_pager_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        loadDataInRecyclerViews(position == 0, holder.recyclerView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun loadDataInRecyclerViews(isRecent: Boolean, recyclerView: RecyclerView): Boolean {
        val adapter = AdapterSavedImages(context = context)
        val scope = CoroutineScope(Dispatchers.IO)
        if (isRecent) {
            scope.launch {
                val listRecent = vmRecent.funGetAll()
                val list: MutableList<ModelSavedImages> = ArrayList()
                listRecent.forEach { recent ->
                    val model =
                        ModelSavedImages(
                            recent.id,
                            recent.title!!,
                            recent.sourceText!!,
                            recent.text!!,
                            recent.sourceLanguageName!!,
                            recent.targetLanguageName!!,
                            recent.sourceLanguageCode!!,
                            recent.targetLanguageCode!!,
                            recent.imagePath!!,
                            recent.textImagePath!!,
                            recent.date!!
                        )
                    list.add(model)
                }
                adapter.initList(list)
                launch(Dispatchers.Main) {
                    recyclerView.adapter = adapter
                }
            }


        } else {
            scope.launch {
                val listPinned = vmPinned.funGetAll()
                val list: MutableList<ModelSavedImages> = ArrayList()
                listPinned.forEach { recent ->
                    val model =
                        ModelSavedImages(
                            recent.id,
                            recent.title!!,
                            recent.sourceText!!,
                            recent.text!!,
                            recent.sourceLanguageName!!,
                            recent.targetLanguageName!!,
                            recent.sourceLanguageCode!!,
                            recent.targetLanguageCode!!,
                            recent.imagePath!!,
                            recent.textImagePath!!,
                            recent.date!!
                        )
                    list.add(model)
                }
                adapter.initList(list)
                launch(Dispatchers.Main) {
                    recyclerView.adapter = adapter
                }
            }

        }


        return true

    }

}
