package ui.assignments.connectfour

import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import javafx.scene.text.Font
import ui.assignments.connectfour.model.Model
import ui.assignments.connectfour.model.Model.startGame
import ui.assignments.connectfour.model.Player
import java.io.FileInputStream
import javafx.scene.input.MouseEvent
import javafx.util.Duration
import ui.assignments.connectfour.model.Grid
import ui.assignments.connectfour.model.Model.dropPiece
import ui.assignments.connectfour.model.Model.onGameWin
import ui.assignments.connectfour.model.Model.onPieceDropped
import java.lang.Double.max
import java.lang.Double.min
import java.lang.Exception
import java.util.Vector
import kotlin.concurrent.thread

class DataView(viewModel: Model) : AnchorPane() {
    init {
        var anchor = this

        // Red Player
        val rLmbda = { ->
            var redPiece = ImageView(Image(
                FileInputStream("src\\main\\resources\\ui\\assignments\\connectfour\\piece_red.png")))
            redPiece.toBack()
            redPiece.fitHeight = 65.0
            redPiece.fitWidth = 65.0
            redPiece.setPreserveRatio(true);
            anchor.children.add(redPiece)
            AnchorPane.setTopAnchor(redPiece, 40.0)
            AnchorPane.setLeftAnchor(redPiece, 20.0)
            var loc = 0.0
            var placed = false
            redPiece.setOnMouseDragged {
                if (!placed) {
                    loc = min(max(0.0, it.sceneX - 40.0), 1010.0)
                    if (loc >= 185 && loc < 265) loc = 227.0
                    if (loc >= 265 && loc < 345) loc = 307.0
                    if (loc >= 345 && loc < 425) loc = 387.0
                    if (loc >= 425 && loc < 505) loc = 467.0
                    if (loc >= 505 && loc < 585) loc = 547.0
                    if (loc >= 585 && loc < 665) loc = 627.0
                    if (loc >= 665 && loc < 745) loc = 707.0
                    if (loc >= 745 && loc < 825) loc = 787.0
                    AnchorPane.setLeftAnchor(redPiece, loc)
                }
            }
            redPiece.setOnMouseReleased {
                if (!placed) {
                    loc = min(max(0.0, it.sceneX - 40.0), 1010.0)
                    print("Loc: $loc \n")
                    var col = (loc.toInt() - 185) / 80;
                    print("Column: $col \n")
                    if (col >= 0 && col <= 7 && Model.dropRate[col] > 80.0 && loc.toInt() > 185) {
                        Model.dropPiece(col)
                        print("Dropped in: $col \n")
                        print("New drop rate: ${Model.dropRate[col]}")
                        var mv = TranslateTransition()
                        mv.duration = Duration(200.0)
                        mv.node = redPiece
                        mv.toY = Model.dropRate[col] + 40.0
                        mv.play()
                        placed = true
                    } else {
                        print("Failed \n")
                        AnchorPane.setLeftAnchor(redPiece, 20.0)
                    }
                }
            }
        }

        // Yellow Player
        val yLmbda = { ->
            var yellowPiece = ImageView(Image(
                FileInputStream("src\\main\\resources\\ui\\assignments\\connectfour\\piece_yellow.png")))
            yellowPiece.toBack()
            yellowPiece.fitHeight = 65.0
            yellowPiece.fitWidth = 65.0
            yellowPiece.setPreserveRatio(true);
            anchor.children.add(yellowPiece)
            AnchorPane.setTopAnchor(yellowPiece, 40.0)
            AnchorPane.setRightAnchor(yellowPiece, 20.0)
            var loc = 0.0
            var placed = false
            yellowPiece.setOnMouseDragged {
                if (!placed) {
                    loc = min(max(0.0, it.sceneX - 40.0), 1010.0)
                    if (loc >= 185 && loc < 265) loc = 227.0
                    if (loc >= 265 && loc < 345) loc = 307.0
                    if (loc >= 345 && loc < 425) loc = 387.0
                    if (loc >= 425 && loc < 505) loc = 467.0
                    if (loc >= 505 && loc < 585) loc = 547.0
                    if (loc >= 585 && loc < 665) loc = 627.0
                    if (loc >= 665 && loc < 745) loc = 707.0
                    if (loc >= 745 && loc < 825) loc = 787.0
                    AnchorPane.setLeftAnchor(yellowPiece, loc)
                }
            }
            yellowPiece.setOnMouseReleased {
                if (!placed) {
                    loc = min(max(0.0, it.sceneX - 40.0), 1010.0)
                    print("Loc: $loc \n")
                    var col = (loc.toInt() - 185) / 80;
                    print("Column: $col \n")
                    if (col >= 0 && col <= 7 && Model.dropRate[col] > 80.0) {
                        dropPiece(col)
                        print("Dropped in: $col \n")
                        print("New drop rate: ${Model.dropRate[col]}")
                        var mv = TranslateTransition()
                        mv.duration = Duration(200.0)
                        mv.node = yellowPiece
                        mv.toY = Model.dropRate[col] + 40.0
                        mv.play()
                        placed = true
                    } else {
                        print("Failed \n")
                        setLeftAnchor(yellowPiece, 1000.0)
                    }
                }
            }
        }

        // Base Structure
        var image = FileInputStream("src\\main\\resources\\ui\\assignments\\connectfour\\grid_8x7.png")
        var structureImage = ImageView(Image(image))
        structureImage.fitHeight = 920.0
        structureImage.fitWidth = 640.0
        structureImage.setPreserveRatio(true);
        structureImage.toFront()
        var p1 = Label("Player #1")
        var p2 = Label("Player #2")
        var clickPlay = Button("Click here to start game!")
        clickPlay.alignment = Pos.CENTER
        clickPlay.prefWidth = 300.0
        clickPlay.prefHeight = 100.0
        clickPlay.background = Background(BackgroundFill(Color.LIGHTGREEN, null, null))
        p1.font = Font.font(22.0)
        p2.font = Font.font(22.0)
        clickPlay.font = Font.font(22.0)
        clickPlay.setOnMouseClicked {
            clickPlay.isVisible = false
            Model.onNextPlayer.addListener { _, _, new ->
                new as Player
                print("Current Player: ${Model.onNextPlayer.value} \n")
                if (new == Player.ONE) rLmbda()
                else yLmbda()
            }
            startGame()
        }
        anchor.children.addAll(structureImage, p1, p2, clickPlay)
        AnchorPane.setBottomAnchor(structureImage, 20.0)
        AnchorPane.setLeftAnchor(structureImage, 220.0)
        AnchorPane.setTopAnchor(p1, 5.0)
        AnchorPane.setLeftAnchor(p1, 5.0)
        AnchorPane.setTopAnchor(p2, 5.0)
        AnchorPane.setRightAnchor(p2, 5.0)
        AnchorPane.setTopAnchor(clickPlay, 20.0)
        AnchorPane.setLeftAnchor(clickPlay, 390.0)

        // Drawing
        Model.onGameDraw.addListener(ChangeListener { observable, oldValue, newValue ->
            var drawGame = Button("Draw!")
            drawGame.alignment = Pos.CENTER
            drawGame.prefWidth = 300.0
            drawGame.prefHeight = 100.0
            drawGame.background = Background(BackgroundFill(Color.GRAY, null, null))
            drawGame.font = Font.font(22.0)
            AnchorPane.setTopAnchor(drawGame, 20.0)
            AnchorPane.setLeftAnchor(drawGame, 390.0)
            anchor.children.add(drawGame)
        })

        // Victory
        Model.onGameWin.addListener(ChangeListener { observable, oldValue, newValue ->
            print("Win \n")
            var endGame = Button()
            if (onGameWin.value == Player.ONE) {
                endGame.text = "Player #1 Victory!"
                endGame.background = Background(BackgroundFill(Color.RED, null, null))
            }
            else {
                endGame.text = "Player #2 Victory!"
                endGame.background = Background(BackgroundFill(Color.YELLOW, null, null))
            }
            endGame.alignment = Pos.CENTER
            endGame.prefWidth = 300.0
            endGame.prefHeight = 100.0
            endGame.font = Font.font(22.0)
            AnchorPane.setTopAnchor(endGame, 20.0)
            AnchorPane.setLeftAnchor(endGame, 390.0)
            anchor.children.add(endGame)
        })
    }
}