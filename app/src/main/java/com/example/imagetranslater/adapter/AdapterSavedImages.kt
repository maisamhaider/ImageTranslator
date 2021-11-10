package com.example.imagetranslater.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagetranslater.R
import com.example.imagetranslater.model.ModelSavedImages
import com.example.imagetranslater.ui.ActivityViewImage
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.DATE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.ID
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.IMAGE_ORIGINAL
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.IMAGE_RESULT
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SOURCE_LANGUAGE_CODE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SOURCE_LANGUAGE_NAME
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SOURCE_TEXT
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.TARGET_LANGUAGE_CODE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.TARGET_LANGUAGE_NAME
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.TEXT

class AdapterSavedImages(val context: Context) : RecyclerView.Adapter<AdapterSavedImages.Holder>() {
    private var list: MutableList<ModelSavedImages> = ArrayList()
    fun initList(list: MutableList<ModelSavedImages>) {
        this.list = list
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_saved_images_item, parent,
            false
        )
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val model = list[position]

        holder.textViewTime.text = model.date
        holder.textViewTitle.text = model.title
        Glide.with(context).load(model.imagePath).into(holder.imageView)

        holder.imageView.setOnClickListener {
            context.startActivity(Intent(context, ActivityViewImage::class.java).apply {
                putExtra(ID, model.id)
                putExtra(IMAGE_ORIGINAL, model.imagePath)
                putExtra(IMAGE_RESULT, model.textImagePath)
                putExtra(SOURCE_LANGUAGE_NAME, model.sourceName)
                putExtra(TARGET_LANGUAGE_NAME, model.targetName)
                putExtra(SOURCE_LANGUAGE_CODE, model.sourceCode)
                putExtra(TARGET_LANGUAGE_CODE, model.targetCODE)
                putExtra(DATE, model.date)
                putExtra(TEXT, model.text)
                putExtra(SOURCE_TEXT, model.sourceText)
            })


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}