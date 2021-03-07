package com.sidbendre.tilesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import com.sidbendre.tilesweeper.model.TileSweeperModel
import kotlinx.android.synthetic.main.activity_main.*
import tyrantgit.explosionfield.ExplosionField
import kotlin.math.ceil
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    companion object {
        var difficulty: Float = 0.0f
        var KEY_DIFFICULTY = "KEY_DIFFICULTY"
        var boardSize = 3
        lateinit var tileBlast: ImageView
        lateinit var explosionField: ExplosionField
    }

    fun startGame()
    {
        tileView.resetGameView()
    }

    override fun onResume() {
        super.onResume()
        tileView.invalidate()
        tileView.requestLayout()
        tileView.visibility = View.VISIBLE
//        startGame()
    }

    // TODO: figure out why first time run doesn't load the mines
    // TODO: change the text size and the flag size as per the grid sizes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        setContentView(R.layout.activity_main)
        lvlTxt.text = getString(R.string.level_text_string)+" ${TileSweeperModel.currentLevel}"


        tileBlast = findViewById(R.id.bombPic)
        explosionField = ExplosionField.attach2Window(( this))

        difficulty = intent.getFloatExtra(KEY_DIFFICULTY, 0.0f)
        tileView.setDifficultyLevel(difficulty)
//        tileView.resetGameView()
        var numberOfMines = ceil(difficulty!! * boardSize.toDouble().pow(2.0)).toInt()
        info_text.text = getString(R.string.mines_present_label_text_string)+" $numberOfMines"
        Log.i("Main","card_view: "+ceil(difficulty!!* boardSize.toDouble().pow(2.0)).toInt().toString())
        Log.i("Main", "Difficulty reported: $difficulty")



        resetBtn.setOnClickListener {
            tileView.resetGameView()
            revealTiles()
//            explosionField.clearAnimation()
//            explosionField.clear()
////            bombPic.refreshDrawableState()
////            bombPic.setImageResource(R.drawable.ic_bomb)
////            bombPic.setImageDrawable(getDrawable(R.drawable.ic_bomb))
//            explosionField.refreshDrawableState()
//            explosionField.invalidate()
//            explosionField.requestLayout()
//            explosionField = ExplosionField.attach2Window(( this))
//            tileBlast.clearAnimation()
//            tileBlast.clearColorFilter()
//            tileBlast.refreshDrawableState()
//            tileBlast.invalidate()
//            tileBlast.requestLayout()
//            tileBlast =  findViewById(R.id.bombPic)
//            bombPic.setImageDrawable(getDrawable(R.drawable.ic_bomb))
//            explosionField = ExplosionField.attach2Window(( this))
//            bombPic.visibility = View.INVISIBLE
//            bombPic.visibility = View.VISIBLE
        }


        modeToggle.setOnClickListener {
            tileView.switchMode()
        }




    }

    fun revealTiles() {
        val x = tileView.right
        val y = tileView.bottom
        val startRadius = 0
        val endRadius = Math.hypot(
            tileView.width.toDouble(),
            tileView.height.toDouble()
        ).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(
            tileView,
            x,
            y,
            startRadius.toFloat(),
            endRadius.toFloat()
        )
        anim.duration = 1500
        tileView.visibility = View.VISIBLE
        anim.start()
    }


}
