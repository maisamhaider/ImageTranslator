package com.example.imagetranslater.googlevision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.android.gms.vision.CameraSource

class GraphicOverlay<T : GraphicOverlay.Graphic>(context: Context, attrs: AttributeSet) :
    View(context, attrs) {
    private val mLock = Any()
    private val mPreviewWidth = 0
    private var mWidthScaleFactor = 1.0f
    private val mPreviewHeight = 0
    private var mHeightScaleFactor = 2.0f
    private val mFacing = CameraSource.CAMERA_FACING_BACK
    private val mGraphics: MutableSet<T> = HashSet()
    var bitmap: Bitmap? = null


    abstract class Graphic(private val mOverlay: GraphicOverlay<*>) {
        /**
         * Draw the graphic on the supplied canvas.  Drawing should use the following methods to
         * convert to view coordinates for the graphics that are drawn:
         *
         *  1. [Graphic.scaleX] and [Graphic.scaleY] adjust the size of
         * the supplied value from the preview scale to the view scale.
         *  1. [Graphic.translateX] and [Graphic.translateY] adjust the
         * coordinate from the preview's coordinate system to the view coordinate system.
         *
         *
         * @param canvas drawing canvas
         */
        abstract fun draw(canvas: Canvas)


        /**
         * Returns true if the supplied coordinates are within this graphic.
         */
        abstract fun contains(x: Float, y: Float): Boolean

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view
         * scale.
         */
        fun scaleX(horizontal: Float): Float {
            return horizontal * mOverlay.mWidthScaleFactor
        }

        /**
         * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
         */
        fun scaleY(vertical: Float): Float {
            return vertical * mOverlay.mHeightScaleFactor
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        fun translateX(x: Float): Float {
            return if (mOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT) {
                mOverlay.width - scaleX(x)
            } else {
                scaleX(x)
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        fun translateY(y: Float): Float {
            return scaleY(y)
        }

        fun postInvalidate() {
            mOverlay.postInvalidate()
        }


    }

//    fun setBitMap(bitmap: Bitmap) {
//        this.bitmap = bitmap
//    }

    /**
     * Removes all graphics from the overlay.
     */
    fun clear() {
        synchronized(mLock) { mGraphics.clear() }
        postInvalidate()
    }

    /**
     * Adds a graphic to the overlay.
     */
    fun add(graphic: T) {
        synchronized(mLock) { mGraphics.add(graphic) }
        postInvalidate()
    }

    /**
     * Removes a graphic from the overlay.
     */
    fun remove(graphic: T) {
        synchronized(mLock) { mGraphics.remove(graphic) }
        postInvalidate()
    }

    /**
     * Returns the first graphic, if any, that exists at the provided absolute screen coordinates.
     * These coordinates will be offset by the relative screen position of this view.
     * @return First graphic containing the point, or null if no text is detected.
     */
    fun getGraphicAtLocation(rawX: Float, rawY: Float): T? {
        synchronized(mLock) {

            // Get the position of this View so the raw location can be offset relative to the view.
            val location = IntArray(2)
            this.getLocationOnScreen(location)
            for (graphic in mGraphics) {
                if (graphic.contains(rawX - location[0], rawY - location[1])) {
                    return graphic
                }
            }
            return null
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(mLock) {
//            if (mPreviewWidth != 0 && mPreviewHeight != 0) {
//                mWidthScaleFactor = width.toFloat() / mPreviewWidth.toFloat()
//                mHeightScaleFactor = height.toFloat() / mPreviewHeight.toFloat()
//            }

            for (graphic in mGraphics) {
                graphic.draw(canvas)
            }
        }

    }
}