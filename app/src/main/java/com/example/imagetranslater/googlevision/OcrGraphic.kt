package com.example.imagetranslater.googlevision

import android.graphics.*
import com.google.mlkit.vision.text.Text

class OcrGraphic(
    overlay: GraphicOverlay<OcrGraphic>?,
    var text: Text,
) : GraphicOverlay.Graphic(overlay!!) {
    private var mId = 0
    private var size = 14

    private val rectColor = Color.WHITE
    private val textColor = Color.BLACK
    private val textSize = 54.0f

    private var sRectPaint = Paint()
    private var sTextPaint = Paint()
    var items: List<Text.TextBlock> = ArrayList()
    var myItem: Text.TextBlock? = null
    var blocks = ArrayList<Text.TextBlock?>()


    // Redraw the overlay, as this graphic has been added.

    fun getId(): Int {
        return mId
    }

    fun setId(id: Int) {
        mId = id
    }

    fun getSize(): Int {
        return size
    }

    fun setSize(line: Text.Line?) {
        if (line != null) {
            size = line.boundingBox!!.height()
        }
    }

    fun getTextBlock(): List<Text.TextBlock> {
        return items
    }


    private fun drawText(text: String, rect: Rect, textHeight: Float?, canvas: Canvas) {
        // If the image is flipped, the left will be translated to right, and the right to left.


//        val x0 = translateX(rect.left)
//        val x1 = translateX(rect.right)
//        rect.left = min(x0, x1)
//        rect.right = max(x0, x1)
//        rect.top = translateY(rect.top)
//        rect.bottom = translateY(rect.bottom)
//    canvas.drawRect(rect, rectPaint)
        val textWidth = sTextPaint.measureText(text)
//    canvas.drawRect(
//      rect.left - STROKE_WIDTH,
//      rect.top - textHeight,
//      rect.left + textWidth + 2 * STROKE_WIDTH,
//      rect.top,
//      labelPaint
//    )


        canvas.drawText(
            text,
            rect.exactCenterX(),
            rect.exactCenterY() /*- (fm.ascent + fm.descent) / 2*/,
            sTextPaint
        )

        // Renders the text at the bottom of the box.
//        canvas.drawText(text, rect.centerX(), rect.centerY()/* - STROKE_WIDTH*/, sTextPaint)
//        canvas.drawText(text, rect.left, rect.top/* - STROKE_WIDTH*/, sTextPaint)
    }

    override fun draw(canvas: Canvas) {
        sRectPaint.color = rectColor
        sRectPaint.style = Paint.Style.FILL

        sTextPaint.color = textColor
        //            sTextPaint.setTextSize(54.0f);

        items = text.textBlocks
        for ((counter, i) in items.withIndex()) {
            myItem = items[counter]
            blocks.add(myItem)
        }
        postInvalidate()

//        val tempBitmap: Bitmap =
//            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
//        tempBitmap.density = DisplayMetrics.DENSITY_HIGH
//        canvas!!.drawBitmap(tempBitmap)

        blocks.forEach { textBlock ->
            val textLines: MutableList<Text.Line> = textBlock!!.lines
            //loop Through each `Line`
            textLines.forEach { currentLine ->
                val words: MutableList<Text.Element> = currentLine.elements
                val textPaint = Paint()
                val rectPaint = Paint()
//
                rectPaint.style = Paint.Style.FILL
                rectPaint.color = Color.WHITE
//                rectPaint.alpha = Color.alpha(50)
////                    val rect = RectF(currentLine.boundingBox)
                val rect = currentLine.boundingBox
                canvas.drawRect(rect!!, rectPaint)
//

                textPaint.color = Color.BLACK
                textPaint.typeface = Typeface.MONOSPACE
                textPaint.textAlign = Paint.Align.CENTER
//                    textPaint.getTextBounds(it.text, 0, length, rect)
                textPaint.measureText(currentLine.text)
                val fm = textPaint.fontMetrics
//                textPaint.textSize =
//                    (currentLine.boundingBox!!.height() + 2 * 2).toFloat()//set text size
//                textPaint.textSize = size.toFloat() //set text size

                textPaint.getFontMetrics(fm)

//                drawText(
//                    currentLine.text,
//                    Rect(currentLine.boundingBox),
//                    null,
//                    canvas
//                )

                canvas.drawText(
                    currentLine.text,
                    rect.exactCenterX(),
                    rect.exactCenterY() - (fm.ascent + fm.descent) / 2,
                    textPaint
                )
                //                canvas.drawText(
//                    currentLine.text,
//                    currentLine.boundingBox!!.exactCenterX(),
//                    currentLine.boundingBox!!.exactCenterY() /*- (fm.ascent + fm.descent) / 2*/,
//                    textPaint
//                )
            }

        }

    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     *
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    override fun contains(x: Float, y: Float): Boolean {


        val text = items
        var rect: RectF
        var left = 0f
        var top = 0f
        var right = 0f
        var bottom = 0f
        for (i in text) {
            rect = RectF(i.boundingBox)
            left = translateX(rect.left)
            top = translateY(rect.top)
            right = translateX(rect.right)
            bottom = translateY(rect.bottom)
        }
        return left < x && right > x && top < y && bottom > y
    }


}