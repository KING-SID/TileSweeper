package com.sidbendre.tilesweeper.ui

import android.content.Context
import android.graphics.*
import android.os.Build
import android.provider.Settings.Global.getString
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.sidbendre.tilesweeper.MainActivity
import com.sidbendre.tilesweeper.R
import com.sidbendre.tilesweeper.model.TileSweeperModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import tyrantgit.explosionfield.ExplosionField
import java.lang.Math.ceil
import kotlin.math.pow


class TileSweeperView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paintBackGround: Paint
    private var paintLine: Paint
    private var paintText: Paint
    private var tmpPlayer: PointF? = null
    private var overlayBackground: Paint

    var difficulty: Float = (MainActivity.difficulty)
    var flagBitMap = getBitmapFromVectorDrawable(context, R.drawable.ic_outlined_flag_black)
    var bombBitmap = getBitmapFromVectorDrawable(context, R.drawable.ic_bomb)
//    var explodingBombBitmap = getBitmapFromVectorDrawable(context, R.drawable.ic_bomb)

    //    var flagBitMap = BitmapFactory.decodeResource(context?.resources, R.raw.outlined_flag)


    init {
        paintBackGround = Paint()
        paintBackGround.color = Color.DKGRAY
        paintBackGround.style = Paint.Style.FILL

        paintLine = Paint()
        paintLine.color = Color.WHITE
        paintLine.strokeWidth = 8f
        paintLine.style = Paint.Style.STROKE

        paintText = Paint()
        paintText.color = Color.WHITE
        paintText.textSize = 10.0f
        paintText.textAlign = Paint.Align.CENTER;

        overlayBackground = Paint()
        overlayBackground.color = Color.WHITE
        overlayBackground.style = Paint.Style.FILL
        overlayBackground.alpha = 180
//        resetGame()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = View.MeasureSpec.getSize(widthMeasureSpec)
        val h = View.MeasureSpec.getSize(heightMeasureSpec)
        val d = if (w == 0) h else if (h == 0) w else if (w < h) w else h
        setMeasuredDimension(d, d)
    }

    fun setDifficultyLevel(levelDifficulty: Float) {
        difficulty = levelDifficulty
        TileSweeperModel.setDifficultyLevel(difficulty)
    }

    fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context!!, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        paintText.textSize = (height / TileSweeperModel.boardSize).toFloat()
//        TileSweeperModel.tileMap =
//            Array(TileSweeperModel.boardSize) { Array(TileSweeperModel.boardSize) { TileSweeperModel.Tile.EMPTY.value } }
//        TileSweeperModel.setUpTiles()
//        Bitmap.createBitmap(flag)
//        flagBitMap = Bitmap.createScaledBitmap(flagBitMap,width/2,height/2,false)
        flagBitMap = Bitmap.createScaledBitmap(
            flagBitMap,
            width / TileSweeperModel.boardSize,
            height / TileSweeperModel.boardSize,
            false
        )

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGameArea(canvas, TileSweeperModel.boardSize)
        drawPlayers(canvas)
        drawTmpPlayer(canvas)

    }


    private fun drawGameArea(canvas: Canvas, size: Int) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackGround)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintLine)

        for (line in 1 until size) {

            // Horizontal
            canvas.drawLine(
                0f,
                (line * height / size).toFloat(),
                width.toFloat(),
                (line * height / size).toFloat(),
                paintLine
            )
            canvas.drawLine(
                (line * width / size).toFloat(),
                0f,
                (line * width / size).toFloat(),
                height.toFloat(),
                paintLine
            )
        }

        canvas.drawLine(
            (2 * width / size).toFloat(), 0f, (2 * width / size).toFloat(), height.toFloat(),
            paintLine
        )


        if (TileSweeperModel.endGameCheck()) {
            Toast.makeText(
                (context as MainActivity),
                context.getString(R.string.gameover_text_string),
                Toast.LENGTH_SHORT
            ).show()
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayBackground)
            MainActivity.explosionField.explode(MainActivity.tileBlast)

        } else if (TileSweeperModel.nextLevelCheck()) {
            TileSweeperModel.nextLevel()
            nextLevelView()
            invalidate()
            requestLayout()
            (context as MainActivity).revealTiles()
        }
    }


    private fun drawPlayers(canvas: Canvas) {
        drawFlags(canvas)
        drawExplored(canvas)
    }


    private fun drawFlags(canvas: Canvas) {
        for (f in TileSweeperModel.flaggedTiles) {
            val centerX =
                (f.first * width / TileSweeperModel.boardSize + width / (2 * TileSweeperModel.boardSize)).toFloat()
            val centerY =
                (f.second * height / TileSweeperModel.boardSize + height / (2 * TileSweeperModel.boardSize)).toFloat()
            var storedValue = TileSweeperModel.tileMap[f.first][f.second]
            if (storedValue == TileSweeperModel.Tile.MINE.value) {
                var flagw = flagBitMap.width
                var flagh = flagBitMap.height
                canvas?.drawBitmap(flagBitMap, centerX - (flagw / 2), centerY - (flagh / 2), null)
            }
//
        }

    }


    private fun drawExplored(canvas: Canvas) {
        for (e in TileSweeperModel.exploredTiles) {
            val centerX =
                (e.first * width / TileSweeperModel.boardSize + width / (2 * TileSweeperModel.boardSize)).toFloat()
            val centerY =
                (e.second * height / TileSweeperModel.boardSize + height / (2 * TileSweeperModel.boardSize)).toFloat()
            var storedValue = TileSweeperModel.tileMap[e.first][e.second]
            if (storedValue == TileSweeperModel.Tile.MINE.value) {
                drawMine(canvas, centerX, centerY)

            } else {

                drawText(canvas, storedValue.toString(), centerX, centerY)
            }
        }
    }

    private fun drawTmpPlayer(canvas: Canvas) {
        if (tmpPlayer != null && TileSweeperModel.toggleMode == TileSweeperModel.Toggle.FLAG) {

            var flagw = flagBitMap.width
            var flagh = flagBitMap.height
            canvas?.drawBitmap(
                flagBitMap,
                tmpPlayer!!.x - (flagw / 2),
                tmpPlayer!!.y - (flagh / 2),
                null
            )
        }
    }


    private fun drawMine(canvas: Canvas, centerX: Float, centerY: Float) {
        var bw = bombBitmap.width
        var bh = bombBitmap.height
        canvas?.drawBitmap(bombBitmap, centerX - (bw / 2), centerY - (bh / 2), null)

    }

    private fun drawText(canvas: Canvas, value: String, centerX: Float, centerY: Float) {
        canvas.drawText(
            value,
            centerX,
            (centerY - ((paintText.descent() + paintText.ascent()) / 2)),
            paintText
        )
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_MOVE) {
            if (TileSweeperModel.toggleMode == TileSweeperModel.Toggle.FLAG) {
                tmpPlayer = PointF(event.x, event.y)
                invalidate()
            }
        } else if (event?.action == MotionEvent.ACTION_UP) {
            tmpPlayer = null
            val tX = event.x.toInt() / (width / TileSweeperModel.boardSize)
            val tY = event.y.toInt() / (height / TileSweeperModel.boardSize)
            val coord = Pair(tX, tY)
            if (tX < TileSweeperModel.boardSize && tY < TileSweeperModel.boardSize && (!TileSweeperModel.flaggedTiles.contains(
                    coord
                ) || !TileSweeperModel.exploredTiles.contains(
                    coord
                ))
            ) {
                if (TileSweeperModel.toggleMode == TileSweeperModel.Toggle.EXPLORE) {

                    TileSweeperModel.exploreTile(tX, tY)

                } else if (TileSweeperModel.toggleMode == TileSweeperModel.Toggle.FLAG) {
                    TileSweeperModel.flagTile(tX, tY)

                }
                invalidate() // redraws the view, so onDraw gets called again
                requestLayout()
            }
        }
        return true
    }


    fun resetGameView() {
        Log.i("ViewClass", "resetGame() entered")
        TileSweeperModel.resetGame()
        Log.i("ViewClass", "resetGame() finished")
        (context as MainActivity).info_text.text =
            context.getString(R.string.mines_present_label_text_string) + " ${
                kotlin.math.ceil(
                    difficulty * TileSweeperModel.boardSize.toDouble().pow(2.0)
                ).toInt()
            }"
        (context as MainActivity).lvlTxt.text =
            context.getString(R.string.level_text_string) + " ${1}"
        invalidate()
        requestLayout()

    }


    fun nextLevelView() {
        flagBitMap = Bitmap.createScaledBitmap(
            flagBitMap,
            width / TileSweeperModel.boardSize,
            height / TileSweeperModel.boardSize,
            false
        )

//        flagBitMap = Bitmap.createScaledBitmap(
//            flagBitMap,
//            width / TileSweeperModel.boardSize,
//            height / TileSweeperModel.boardSize,
//            false
//        )
        paintText.textSize = (height / TileSweeperModel.boardSize).toFloat()
        (context as MainActivity).info_text.text =
            context.getString(R.string.mines_present_label_text_string) + " ${
                ceil(
                    difficulty!! * TileSweeperModel.boardSize.toDouble().pow(2.0)
                ).toInt()
            }"
        Toast.makeText(
            (context as MainActivity),
            context.getString(R.string.flagged_all_msg_text_string),
            Toast.LENGTH_SHORT
        ).show()
        (context as MainActivity).lvlTxt.text =
            context.getString(R.string.level_text_string) + " ${TileSweeperModel.currentLevel}"
    }


    fun switchMode() {
        TileSweeperModel.switchToggle()
    }


}