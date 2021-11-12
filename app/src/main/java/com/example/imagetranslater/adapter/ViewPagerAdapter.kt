package com.example.imagetranslater.adapter


import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.imagetranslater.R
import com.example.imagetranslater.interfaces.DeleteCallBack
import com.example.imagetranslater.model.ModelSavedImages
import com.example.imagetranslater.utils.Singleton.toastLong
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
    RecyclerView.Adapter<ViewPagerAdapter.MyViewHolder>(), DeleteCallBack {

    val list = mutableListOf("Recent", "Pinned")
    var item = -1
    var showDeleteDialog = true
    lateinit var holder: MyViewHolder

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyclerView: RecyclerView =
            itemView.findViewById(com.example.imagetranslater.R.id.recyclerView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(context)
                .inflate(R.layout.layout_pager_item, parent, false)
        holder = MyViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        loadDataInRecyclerViews(position == 0, holder.recyclerView)
        item = position
//        enableSwiping(holder.recyclerView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun enableSwiping(recyclerView: RecyclerView) {
        val background = ColorDrawable(ContextCompat.getColor(context, R.color.teal_700))

        val mIth = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                // move item in `fromPos` to `toPos` in adapter.
                context.toastLong("move")
                return true // true if moved, false otherwise
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                if (direction == ACTION_STATE_SWIPE) {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = viewHolder.adapterPosition
                    // remove from adapter
                    showDeleteDialog = true
                    context.toastLong("fromPos $fromPos, toPos $toPos")
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                if (dX < 3) {
                    showDeleteDialog = true
                }
                if (dX > 150) {
                    if (showDeleteDialog) {
                        showDeleteDialog = false
                        context.toastLong("moving")
                        deleteItemDialog()
                    }
                }
            }

        })
        mIth.attachToRecyclerView(recyclerView)
    }

    fun deleteItemDialog() {
        val builder = AlertDialog.Builder(context);
        builder.setTitle("Delete")
            .setMessage("Are you sure uou want to delete?").setPositiveButton("yes")
            { dialog, _ ->
                dialog.cancel()
            }.setNegativeButton("No") { dialog, _ -> dialog.cancel() }

        val dialog = builder.create()
        dialog.show()
    }

    private fun loadDataInRecyclerViews(isRecent: Boolean, recyclerView: RecyclerView): Boolean {
        val adapter = AdapterSavedImages(context = context, this)
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

    override fun call(id: Int) {
        if (item == 0) {
            vmRecent.funDelete(id)
        } else {
            vmPinned.funDelete(id)
        }
        loadDataInRecyclerViews(item == 0, holder.recyclerView)
    }

}
