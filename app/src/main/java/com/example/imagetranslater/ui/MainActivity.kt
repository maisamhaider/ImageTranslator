package com.example.imagetranslater.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.imagetranslater.R
import com.example.imagetranslater.adapter.ViewPagerAdapter
import com.example.imagetranslater.databinding.ActivityMainBinding
import com.example.imagetranslater.ui.imageTranslator.ActivityImageTranslator
import com.example.imagetranslater.ui.imageTranslator.ActivityImageTranslatorResult
import com.example.imagetranslater.utils.AnNot.Image.FROM_GALLERY
import com.example.imagetranslater.utils.AnNot.Image.translate
import com.example.imagetranslater.utils.AnNot.Image.uri
import com.example.imagetranslater.viewmodel.VMPinned
import com.example.imagetranslater.viewmodel.VMRecent
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    private val vmRecent: VMRecent by viewModels()
    private val vmPinned: VMPinned by viewModels()
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        binding.imageViewOpenCamera.setOnClickListener {
            startActivity(Intent(this, ActivityImageTranslator::class.java))
        }
        binding.imageFromGallery.setOnClickListener {
            translate = true
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
                FROM_GALLERY = true
                startActivity(Intent(this, ActivityImageTranslatorResult::class.java))
            } else {
            }

        }

    private fun loadDataInRecyclerViews() {

        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val adapter = ViewPagerAdapter(this, vmRecent, vmPinned)
        binding.viewPager.adapter = adapter
        val tabTitles = arrayOf("Recent", "Pinned")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }

    override fun onResume() {
        super.onResume()
        loadDataInRecyclerViews()

    }
}