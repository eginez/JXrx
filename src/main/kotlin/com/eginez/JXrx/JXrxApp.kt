//@file:JvmName("JXrxApp")
package com.eginez.JXrx

import javafx.application.Application
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.input.Clipboard
import javafx.scene.layout.VBox
import tornadofx.*
import java.util.*
import kotlin.concurrent.fixedRateTimer

class MyView: View() {

    override val root = VBox()
    var label = Label()
    var model = emptySet<String>()
    var content = FXCollections.observableArrayList<String>()
    var listView = ListView<String>(content)
    var timer: Timer

    init {
        label.text = "BufferContent"
        root += label
        root += listView.apply {
            setOnMouseClicked { mouseEvent ->
                if (mouseEvent.clickCount == 2) {
                    val item = this.selectionModel.selectedItem
                    Clipboard.getSystemClipboard().putString(item)
                }
            }
        }
        timer = fixedRateTimer(name="clipboardChecker", initialDelay = 0, period = 500, daemon = true) {
            Platform.runLater {
                val string = Clipboard.getSystemClipboard().string
                if (!model.contains(string)) {
                    model += string
                    content.add(0, string)
                }
            }
        }

    }
}

class JXrxApp:App(MyView::class)



fun main(args: Array<String>) {
    Application.launch(JXrxApp::class.java, *args)
}



