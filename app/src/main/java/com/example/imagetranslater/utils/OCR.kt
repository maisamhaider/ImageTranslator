package com.example.imagetranslater.utils

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import com.google.mlkit.vision.text.Text
import java.io.File

class OCR {

    fun runOcr(context: Context, text: Text, translatedList: MutableList<String>): Bitmap {
         val u: Uri = if (!AnNot.Image.FROM_GALLERY) {
            if (!AnNot.Image.uri.path!!.contains("file:///")) {
                Uri.fromFile(File(AnNot.Image.uri.path.toString()))
            } else {
                AnNot.Image.uri
            }
        } else {
            AnNot.Image.uri
        }
        AnNot.Image.FROM_GALLERY = false

        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(context.contentResolver, u)
            )
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, u)
        }

        val canvas: Canvas?
        val tempBitmap: Bitmap =
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        tempBitmap.setHasAlpha(true)
        tempBitmap.density = DisplayMetrics.DENSITY_HIGH
        canvas = Canvas(tempBitmap)
         // Break the text into multiple lines and draw each one according to its own bounding box.
        var counter = 1
        val textPaint = Paint()

        val rectPaint = Paint()
        textPaint.color = Color.BLACK
        textPaint.typeface = Typeface.MONOSPACE
        textPaint.textScaleX = 0.5f
        textPaint.textAlign = Paint.Align.CENTER

        rectPaint.style = Paint.Style.FILL
        rectPaint.color = Color.WHITE

        text.textBlocks.forEach { textBlock ->
            val textLines: MutableList<Text.Line> = textBlock.lines
            textLines.forEach { currentLine ->

                val rect = currentLine.boundingBox
                canvas.drawRect(rect!!, rectPaint)

                textPaint.measureText(translatedList[counter - 1])

                textPaint.textSize = currentLine.boundingBox!!.height().toFloat() //set text size
                val fm = textPaint.fontMetrics
                textPaint.getFontMetrics(fm)
                  try {
                    canvas.drawText(
                        translatedList[counter - 1],
                        currentLine.boundingBox!!.exactCenterX(),
                        currentLine.boundingBox!!.exactCenterY() - (fm.ascent + fm.descent) / 2,
                        textPaint
                    )
                } catch (e: Exception) {

                }
                counter++
            }

        }


        return tempBitmap
    }

}