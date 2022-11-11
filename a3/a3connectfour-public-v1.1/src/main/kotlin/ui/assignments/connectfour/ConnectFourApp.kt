package ui.assignments.connectfour

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.*
import javafx.stage.Stage


class ConnectFourApp : Application() {
    override fun start(stage: Stage) {
        // MVVM
        val vm = ui.assignments.connectfour.model.Model
        var vw = DataView(vm)

        val scene = Scene(vw, 1080.0, 720.0)
        stage.title = "CS349 - A3 Connect Four - n3petrov"
        stage.scene = scene
        stage.isResizable = false
        stage.show()
    }
}