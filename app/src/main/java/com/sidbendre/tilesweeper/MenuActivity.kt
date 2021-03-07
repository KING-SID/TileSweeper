package com.sidbendre.tilesweeper

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.sidbendre.tilesweeper.model.TileSweeperModel
import com.sidbendre.tilesweeper.ui.TileSweeperView
import kotlinx.android.synthetic.main.activity_main_menu.*

class MenuActivity : AppCompatActivity() {

    companion object {
        var difficulty: Float = 0.0f
        var KEY_DIFFICULTY = "KEY_DIFFICULTY"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }


        easyBtn.setOnClickListener {
            difficulty = TileSweeperModel.Difficulty.EASY.coverage
            var gameIntent = Intent(this, MainActivity::class.java)
            gameIntent.putExtra(KEY_DIFFICULTY, difficulty)
            testTxt.text = difficulty.toString()
            TileSweeperModel.setDifficultyLevel(difficulty)
            // what about running set up tiles()
            TileSweeperModel.resetGame()
            startActivity(gameIntent)

        }

        mediumBtn.setOnClickListener {
            difficulty = TileSweeperModel.Difficulty.MEDIUM.coverage
            var gameIntent = Intent(this, MainActivity::class.java)
            gameIntent.putExtra(KEY_DIFFICULTY, difficulty)
            testTxt.text = difficulty.toString()
            TileSweeperModel.setDifficultyLevel(difficulty)
            TileSweeperModel.resetGame()
            startActivity(gameIntent)
        }

        hardBtn.setOnClickListener {
            difficulty = TileSweeperModel.Difficulty.HARD.coverage
            var gameIntent = Intent(this, MainActivity::class.java)
            gameIntent.putExtra(KEY_DIFFICULTY, difficulty)
            testTxt.text = difficulty.toString()
            TileSweeperModel.setDifficultyLevel(difficulty)
            TileSweeperModel.resetGame()
            startActivity(gameIntent)
        }

        aboutBtn.setOnClickListener{

            Snackbar.make(findViewById(R.id.menuView), getString(R.string.about_sid_string),
                Snackbar.LENGTH_SHORT).show()

        }

    }


//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putFloat(DIFF_STRING, difficulty)
//
//    }


}