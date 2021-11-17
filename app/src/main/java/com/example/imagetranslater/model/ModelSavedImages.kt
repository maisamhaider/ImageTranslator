package com.example.imagetranslater.model


data class ModelSavedImages(
    val id: Int,
    val title: String,
    val sourceText: String,
    val text: String,
    val sourceName: String,
    val targetName: String,
    val sourceCode: String,
    val targetCODE: String,
    val imagePath: String,
    val textImagePath: String,
    val shareImagePath: String,
    val date: String,
)
