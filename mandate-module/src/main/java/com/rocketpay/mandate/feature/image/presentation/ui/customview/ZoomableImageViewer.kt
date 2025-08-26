package com.rocketpay.mandate.feature.image.presentation.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView

internal class ZoomableImageViewer : AppCompatImageView {

    private var mContext: Context
    private lateinit var mAttributeSet : AttributeSet

    private lateinit var mGestureListener: GestureDetector
    private lateinit var mScaleGestureDetector: ScaleGestureDetector

    private var mScrollX: Float = 0f
    private var mScrollY: Float = 0f
    private var mFocusX: Float = 0f
    private var mFocusY: Float = 0f

    private var mScroll: Boolean = false
    private var mScaling: Boolean = false

    private var mScaleFactor: Float = MIN_SCALE_FACTOR
    private var mOnImageScaleListener : OnImageScaleListener? = null

    private var mCanvasRect: Rect = Rect()
    private var mDrawingRect : Rect = Rect()

    companion object {
        const val DOUBLE_CLICK_ZOOM_IN_SCALE_FACTOR = 2.5f
        const val MAX_SCALE_FACTOR = 5.0f
        const val MIN_SCALE_FACTOR = 1.0f
        const val THRESHOLD_ZOOM_OUT_SCALE_FACTOR = 1.3f
        const val LEFT_TOP_MIN_RECT_COORDINATE = 2
        const val RIGHT_BOTTOM_MAX_RECT_COORDINATE = 2
    }

    constructor(context : Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context,attributeSet) {
        mContext = context
        mAttributeSet = attributeSet
        init()
    }

    private fun init() {
        mScaleGestureDetector = ScaleGestureDetector(mContext,object : ScaleGestureDetector.OnScaleGestureListener {

            override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(scaleGestureDetector: ScaleGestureDetector) {
                mScaling = false
                if (mScaleFactor <= THRESHOLD_ZOOM_OUT_SCALE_FACTOR) {
                    mOnImageScaleListener?.onImageScaleEnd()
                }
            }

            override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
                mScaleFactor *= scaleGestureDetector?.scaleFactor ?: MIN_SCALE_FACTOR
                mFocusX= scaleGestureDetector?.focusX ?: 0f
                mFocusY = scaleGestureDetector?.focusY ?: 0f

                mScaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(mScaleFactor, MAX_SCALE_FACTOR))
                invalidate()
                return true
            }

        })

        mGestureListener = GestureDetector(context,object : GestureDetector.SimpleOnGestureListener() {

            override fun onDoubleTap(e: MotionEvent): Boolean {
                mFocusX = e?.x ?: 0f
                mFocusY=  e?.y ?: 0f
                if (mScaleFactor >= THRESHOLD_ZOOM_OUT_SCALE_FACTOR) {
                    mScaleFactor = MIN_SCALE_FACTOR
                    mOnImageScaleListener?.onImageScaleEnd()
                }
                else {
                    mScaleFactor = DOUBLE_CLICK_ZOOM_IN_SCALE_FACTOR
                    mOnImageScaleListener?.onImageScaleBegin()
                }
                invalidate()
                return true
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                if(!mScaling) {
                    mScroll = true
                    mScrollX = distanceX
                    mScrollY = distanceY
                    invalidate()
                    return true
                }
                return false
            }

        })

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mScaling) {
            if (event?.pointerCount ?: 0 > 1) {
                mOnImageScaleListener?.onImageScaleBegin()
            }
            else {
                if (mScaleFactor <= THRESHOLD_ZOOM_OUT_SCALE_FACTOR) {
                    mOnImageScaleListener?.onImageScaleEnd()
                }
            }
            var bool = mScaleGestureDetector.onTouchEvent(event)
            bool = mGestureListener.onTouchEvent(event) || bool
            return bool
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        if (mScroll && !mScaling) {
            canvas?.apply {
                scale(mScaleFactor, mScaleFactor,mFocusX+mScrollX,mFocusY+mScrollY)
                mFocusX += mScrollX
                mFocusY += mScrollY
                getClipBounds(mCanvasRect)
                getDrawingRect(mDrawingRect)

                if(mCanvasRect.left < LEFT_TOP_MIN_RECT_COORDINATE)
                    mFocusX -= mCanvasRect.left

                if(mCanvasRect.top < LEFT_TOP_MIN_RECT_COORDINATE)
                    mFocusY -= mCanvasRect.top

                if (mCanvasRect.bottom > mDrawingRect.bottom + RIGHT_BOTTOM_MAX_RECT_COORDINATE)
                    mFocusY -= (mCanvasRect.bottom - mDrawingRect.bottom)

                if (mCanvasRect.right > mDrawingRect.right + RIGHT_BOTTOM_MAX_RECT_COORDINATE)
                    mFocusX -= (mCanvasRect.right - mDrawingRect.right )

                super.onDraw(this)
            }
            mScroll = false
            invalidate()
        }
        else {
            canvas?.apply {
                scale(mScaleFactor, mScaleFactor,mFocusX,mFocusY)
                super.onDraw(this)
            }
        }
    }

    fun setOnZoomListener(zoomListenerImage: OnImageScaleListener) {
        mOnImageScaleListener = zoomListenerImage
    }

    interface OnImageScaleListener {

        fun onImageScaleBegin()

        fun onImageScaleEnd()
    }
}