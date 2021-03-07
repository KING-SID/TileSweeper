package com.sidbendre.tilesweeper.model

import android.net.wifi.EasyConnectStatusCallback
import android.util.Log
import com.sidbendre.tilesweeper.MainActivity
import java.lang.Math.pow
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.random.Random

object TileSweeperModel {

    enum class Difficulty(val coverage: Float) {
        // Modifiable
        EASY(0.3f), MEDIUM(0.5f), HARD(0.6f), NULL(0.0f)
    }

    enum class Tile(val value: Int?) {
        EMPTY(0), MINE(-1)
    }

    enum class Toggle {
        EXPLORE, FLAG
    }

    enum class Dir(val x: Int, val y: Int) {
        N(0, 1), NW(-1, 1), NE(1, 1), W(-1, 0), E(1, 0),
        SW(-1, -1), S(0, -1), SE(1, -1)
    }

    var currentLevel = 1
    var boardSize = 5
    var flaggedTiles = mutableListOf<Pair<Int, Int>>()
    var exploredTiles = mutableListOf<Pair<Int, Int>>()
    var mineCoordinates = mutableListOf<Pair<Int, Int>>()
    var toggleMode = Toggle.EXPLORE
    var difficulty = Difficulty.EASY
    private val rand = Random
    lateinit var tileMap: Array<Array<Int?>>
//    init {
//        difficulty = Difficulty.EASY
////        tileMap = Array(boardSize) { Array(boardSize) { 5} }
//        tileMap = Array(boardSize) { Array(boardSize) { Tile.EMPTY.value} }
//        setUpTiles()
//    }


    fun switchToggle() {
        if (toggleMode == Toggle.EXPLORE) {
            toggleMode = Toggle.FLAG
        } else if (toggleMode == Toggle.FLAG) {
            toggleMode = Toggle.EXPLORE
        }
    }
//    init
//    {
//        resetGame()
//    }

    fun setDifficultyLevel(difficultyFlt: Float) {

        for (d in Difficulty.values()) {
            if (d.coverage == difficultyFlt) {
                difficulty = d
                break
            }
        }


    }

    fun nextLevelCheck(): Boolean {
        var verdict: Boolean = true
        for (m in mineCoordinates) {
            if (!flaggedTiles.contains(m)) {
                verdict = false
            }
        }
        if (exploredTiles.size.toDouble() == boardSize.toDouble().pow(2.0)) {
            verdict = true
        }
        return verdict
    }






    fun nextLevel() {
        currentLevel += 1
        boardSize += 1
        mineCoordinates = mutableListOf<Pair<Int, Int>>()
        exploredTiles = mutableListOf<Pair<Int, Int>>()
        flaggedTiles = mutableListOf<Pair<Int, Int>>()
        setUpTiles()

    }

    fun resetGame() {
        currentLevel = 1
        if (difficulty == Difficulty.HARD)
        {
            boardSize= 6
        }else
        {

            boardSize = 5
        }
        mineCoordinates = mutableListOf<Pair<Int, Int>>()
        exploredTiles = mutableListOf<Pair<Int, Int>>()
        flaggedTiles = mutableListOf<Pair<Int, Int>>()

        setUpTiles()


    }


    fun setUpTiles() {
//        tileMap = Array(boardSize) { Array(boardSize) { 5} }
        tileMap = Array(boardSize) { Array(boardSize) { Tile.EMPTY.value} }
        placeMines()
        setUpTileCounts()
    }

    private fun placeMines() {
        var numberOfMines = ceil(difficulty.coverage * boardSize.toDouble().pow(2.0)).toInt()
        var minesPlacedCounter = 0
        while (minesPlacedCounter < numberOfMines) {
            var x_r = rand.nextInt(boardSize)
            var y_r = rand.nextInt(boardSize)
            if (tileMap[x_r][y_r] != Tile.MINE.value) {
                tileMap[x_r][y_r] = Tile.MINE.value
                minesPlacedCounter++
                var coord = Pair(x_r, y_r)
                mineCoordinates.add(coord)
//                println(coord)
            }
        }

        Log.i("IAmACheater", mineCoordinates.toString())
    }


    private fun setUpTileCounts() {

        for (c in mineCoordinates) {
            var x = c.first
            var y = c.second
            for (dir in Dir.values()) {
                if (((x + dir.x < boardSize) && ((x + dir.x >= 0))) && ((y + dir.y < boardSize) && (y + dir.y >= 0))) {
                    if (tileMap[x + dir.x][y + dir.y] != Tile.MINE.value) {
                        tileMap[x + dir.x][y + dir.y] = tileMap[x + dir.x][y + dir.y]?.plus(1)

                    }
                }
            }
        }
    }

    fun exploreTile(x: Int, y: Int) {
        exploredTiles.add(Pair(x, y))
    }

    fun flagTile(x: Int, y: Int) {
        flaggedTiles.add(Pair(x, y))
    }

    fun endGameCheck(): Boolean {

        // check if non-mine tile was flagged
        if (flaggedTiles.isNotEmpty()) {
            for (f in flaggedTiles) {
                if (tileMap[f.first][f.second] != Tile.MINE.value) {
//                    Log.i("Model", "Number of flagged Mines: " + flaggedTiles.size)
                    return true
                }
            }
        }
        // check if mined tile was 'explored'
        if (exploredTiles.isNotEmpty()) {
            for (e in exploredTiles) {
                if (tileMap[e.first][e.second] == Tile.MINE.value) {
//                    Log.i("Model", "Number of explored tiles: " + exploredTiles.size)
                    return true
                }
            }
        }
        return false
    }


}