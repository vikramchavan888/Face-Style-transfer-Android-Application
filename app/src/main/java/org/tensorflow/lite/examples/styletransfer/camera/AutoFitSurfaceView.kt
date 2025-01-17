package org.tensorflow.lite.examples.styletransfer.camera

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView
import kotlin.math.roundToInt

/**
 * A [SurfaceView] that can be adjusted to a specified aspect ratio and
 * performs center-crop transformation of input frames.
 */
class AutoFitSurfaceView(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle) {

  private var aspectRatio = 0f
  private var widthDiff = 0
  private var heightDiff = 0
  private var requestLayout = false

  /**
   * Sets the aspect ratio for this view. The size of the view will be
   * measured based on the ratio calculated from the parameters. Note that
   * the actual sizes of parameters don't matter, that is, calling
   * setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
   *
   * @param width Relative horizontal size
   * @param height Relative vertical size
   */
  fun setAspectRatio(width: Int, height: Int) {
    require(width > 0 && height > 0) { "Size cannot be negative" }
    aspectRatio = width.toFloat() / height.toFloat()
    requestLayout()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)
    if (aspectRatio == 0f) {
      setMeasuredDimension(width, height)
    } else {
      // Performs center-crop transformation of the camera frames
      val newWidth: Int
      val newHeight: Int
      if (width < height * aspectRatio) {
        newHeight = height
        newWidth = (height / aspectRatio).roundToInt()
      } else {
        newWidth = width
        newHeight = (width / aspectRatio).roundToInt()
      }

      Log.d(TAG, "Measured dimensions set: $newWidth x $newHeight")
      widthDiff = width - newWidth
      heightDiff = height - newHeight
      requestLayout = true
      setMeasuredDimension(newWidth, newHeight)
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    if (requestLayout) {
      requestLayout = false
      layout(
        widthDiff / 2,
        heightDiff / 2,
        right + (widthDiff / 2),
        bottom + (heightDiff / 2)
      )
    }
    super.onLayout(changed, left, top, right, bottom)
  }

  companion object {
    private val TAG = AutoFitSurfaceView::class.java.simpleName
  }
}
