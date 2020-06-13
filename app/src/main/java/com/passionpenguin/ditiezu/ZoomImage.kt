package com.passionpenguin.ditiezu

import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlin.math.sqrt

class ZoomImage : AppCompatActivity(), View.OnTouchListener {
    private val tag = "Touch"

    // These matrices will be used to scale points of the image
    private var matrix: Matrix = Matrix()
    private var savedMatrix: Matrix = Matrix()

    // The 3 states (events) which the user is trying to perform
    private val none = 0
    private val drag = 1
    private val zoom = 2
    private var mode = none

    // these PointF objects are used to record the point(s) the user is touching
    var start = PointF()
    private var mid = PointF()
    private var oldDist = 1f

    /** Called when the activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_image)
        val e = intent.extras
        val path = e?.getString("filePath", "-1") as String
        Log.i("", path)
//        if (path == "-1" || path.isEmpty()) onBackPressed()
        val view: ImageView = findViewById(R.id.imageView)
        try {
            Glide.with(applicationContext).load(path).into(view)
        } catch (ignored: Exception) {
            Log.e("", ignored.toString())
            onBackPressed()
        }
        view.setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        val view: ImageView = v as ImageView
        view.scaleType = ImageView.ScaleType.MATRIX
        val scale: Float
//        dumpEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start[event.x] = event.y
                Log.d(tag, "mode=DRAG") // write to LogCat
                mode = drag
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = none
                Log.d(tag, "mode=NONE")
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                Log.d(tag, "oldDist=$oldDist")
                if (oldDist > 5f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = zoom
                    Log.d(tag, "mode=ZOOM")
                }
            }
            MotionEvent.ACTION_MOVE -> if (mode == drag) {
                matrix.set(savedMatrix)
                Log.i((event.x - start.x).toString(), (event.y - start.y).toString())
                Log.i((view.width.toString()), view.height.toString())
                val dX = event.x - start.x
                val dY = event.y - start.y
                matrix.postTranslate(
                    dX,
                    dY
                ) // create the transformation in the matrix  of points}
            } else if (mode == zoom) {
                // pinch zooming
                val newDist = spacing(event)
                if (newDist > 5f) {
                    matrix.set(savedMatrix)
                    scale = newDist / oldDist // setting the scaling of the
                    // matrix...if scale > 1 means
                    // zoom in...if scale < 1 means
                    // zoom out
                    matrix.postScale(scale, scale, mid.x, mid.y)
                }
            }
        }
        view.imageMatrix = matrix // display the transformation on screen
        return true // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y.toDouble()).toFloat()
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    /** Show an event in the LogCat view, for debugging  */
//    private fun dumpEvent(event: MotionEvent) {
//        val names = arrayOf(
//            "DOWN",
//            "UP",
//            "MOVE",
//            "CANCEL",
//            "OUTSIDE",
//            "POINTER_DOWN",
//            "POINTER_UP",
//            "7?",
//            "8?",
//            "9?"
//        )
//        val sb = StringBuilder()
//        val action = event.action
//        val actionCode = action and MotionEvent.ACTION_MASK
//        sb.append("event ACTION_").append(names[actionCode])
//        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
//            sb.append("(pid ").append(action shr MotionEvent.ACTION_POINTER_ID_SHIFT)
//            sb.append(")")
//        }
//        sb.append("[")
//        for (i in 0 until event.pointerCount) {
//            sb.append("#").append(i)
//            sb.append("(pid ").append(event.getPointerId(i))
//            sb.append(")=").append(event.getX(i).toInt())
//            sb.append(",").append(event.getY(i).toInt())
//            if (i + 1 < event.pointerCount) sb.append(";")
//        }
//        sb.append("]")
//        Log.d("Touch Events ---------", sb.toString())
//    }
}