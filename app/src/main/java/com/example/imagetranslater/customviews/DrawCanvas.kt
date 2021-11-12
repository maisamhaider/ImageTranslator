package com.example.imagetranslater.customviews

import android.content.Context
import android.graphics.*
import android.util.DisplayMetrics
import android.view.View
import com.google.mlkit.vision.text.Text

class DrawCanvas(context: Context?, val text: Text, val bitmap: Bitmap) : View(context) {


    val bit = bitmap

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        ocr(text, bitmap, canvas!!)
    }

    private fun ocr(text: Text, bitmap: Bitmap, canvas: Canvas): Bitmap {

        val items: List<Text.TextBlock> = text.textBlocks
        var myItem: Text.TextBlock?
        val blocks: MutableList<Text.TextBlock> = ArrayList()

        for (i in items.indices) {
            myItem = items[i]
            //Add All TextBlocks to the `blocks` List
            blocks.add(myItem)
        }

        //Create the Canvas object,
        //Which ever way you do image that is ScreenShot for example, you
        //need the views Height and Width to draw recatngles
        //because the API detects the position of Text on the View
        //So Dimesnions are important for Draw method to draw at that Text
        //Location
        val tempBitmap: Bitmap =
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        tempBitmap.setHasAlpha(true)
        tempBitmap.density = DisplayMetrics.DENSITY_HIGH
//        canvas = Canvas(tempBitmap)

        // Break the text into multiple lines and draw each one according to its own bounding box.
        var counter = 1
        blocks.forEach { textBlock ->
            val textLines: MutableList<Text.Line> = textBlock.lines
            //loop Through each `Line`
            textLines.forEach { currentLine ->
                currentLine.elements
                val textPaint = Paint()
                val rectPaint = Paint()
                rectPaint.style = Paint.Style.FILL
                rectPaint.color = Color.WHITE
//                rectPaint.alpha = Color.alpha(50)
////                    val rect = RectF(currentLine.boundingBox)
                val rect = currentLine.boundingBox
                canvas.drawRect(rect!!, rectPaint)

                textPaint.color = Color.BLACK
                textPaint.typeface = Typeface.MONOSPACE
//                    textPaint.getTextBounds(it.text, 0, length, rect)
//                textPaint.measureText(translatedList[counter - 1])
                val fm = textPaint.fontMetrics
//                textPaint.textSize = 50f  //set text size
                var size = currentLine.boundingBox!!.height().toFloat() //set text size
//                textPaint.textSize = currentLine.boundingBox!!.height().toFloat() //set text size
                if (size > 80) {
                    size = 80f
                }
                textPaint.textSize = size //set text size
                textPaint.textAlign = Paint.Align.CENTER
                textPaint.getFontMetrics(fm)


                try {
//                    canvas.drawText(
////                        translatedList[counter - 1],
//                        currentLine.boundingBox!!.exactCenterX(),
//                        currentLine.boundingBox!!.exactCenterY() - (fm.ascent + fm.descent) / 2,
//                        textPaint
//                    )
                } catch (e: Exception) {

                }


                counter++
            }

        }

        return tempBitmap
    }

}