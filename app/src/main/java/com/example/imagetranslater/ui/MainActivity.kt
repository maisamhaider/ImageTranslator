package com.example.imagetranslater.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.imagetranslater.R
import com.example.imagetranslater.adapter.ViewPagerAdapter
import com.example.imagetranslater.ui.imageTranslator.ActivityImageTranslator
import com.example.imagetranslater.ui.imageTranslator.ActivityImageTranslatorResult
import com.example.imagetranslater.utils.AnNot.imaeg.uri
import com.example.imagetranslater.viewmodel.VMPinned
import com.example.imagetranslater.viewmodel.VMRecent
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    private lateinit var tableLayout: TabLayout
    lateinit var viewPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tableLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager2)


        findViewById<ImageView>(R.id.imageViewOpenCamera).setOnClickListener {
            startActivity(Intent(this, ActivityImageTranslator::class.java))
        }
        findViewById<ImageView>(R.id.imageFromGallery).setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            intentLauncher.launch(photoPickerIntent)
        }


    }

    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK && result.data!!.data != null) {
                val chosenImage = result.data!!.data
                uri = chosenImage!!
                startActivity(Intent(this, ActivityImageTranslatorResult::class.java))
            } else {
            }

        }

    private fun loadDataInRecyclerViews() {

        val vmRecent: VMRecent = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[VMRecent::class.java]


        val vmPinned: VMPinned = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[VMPinned::class.java]

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val adapter = ViewPagerAdapter(this, vmRecent, vmPinned)
        viewPager.adapter = adapter
        val tabTitles = arrayOf("Recent", "Pinned")
        TabLayoutMediator(tableLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }

    override fun onResume() {
        super.onResume()
        loadDataInRecyclerViews()
    }
}