//@file:JvmName("JXrxApp")
package com.eginez.JXrx

import javafx.application.Application
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.concurrent.Task
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuBar
import javafx.scene.input.Clipboard
import javafx.scene.layout.VBox
import javafx.stage.Stage
import tornadofx.*
import java.awt.*
import java.util.*
import javax.imageio.ImageIO
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

    fun clearContent() {
        Platform.runLater {
            var newModel = emptySet<String>()
            newModel += content.last()
            model = newModel
            listView.items.clear()
        }
    }
}

class JXrxApp:App(MyView::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        Platform.setImplicitExit(false)
        val os = System.getProperty("os.name")
        if (os.startsWith("Mac")){
            val menuBar = MenuBar()
            menuBar.useSystemMenuBarProperty().set(true)
        }
        SystemTray.getSystemTray().add(createTrayIcon({}))
    }

    fun createTrayIcon(onExit: () -> Unit): TrayIcon {
        val trayIcon = ImageIO.read(ClassLoader.getSystemClassLoader().getResource("imgs/tray_icon.png"))
        return TrayIcon(trayIcon, "JXrx", createPopupMenu(onExit))
    }

    fun createPopupMenu(onExit: ()->Unit): PopupMenu {
        val popUp = PopupMenu()
        val menuExit = MenuItem("Exit")
        val menuClear = MenuItem("Clear")
        val menuShow = MenuItem("Show")
        val menus = listOf<MenuItem>(menuShow, menuClear, menuExit)
        menuClear.addActionListener {
            val view = FX.find(MyView::class.java)
            view.clearContent()
        }
        menuShow.addActionListener {
            val view = FX.find(MyView::class.java)
            Platform.runLater { view.primaryStage.show() }
        }

        menuExit.addActionListener {
            onExit()
            Platform.exit()
            System.exit(0)
        }
        menus.forEach { popUp.add(it) }
        return popUp
    }
}



fun main(args: Array<String>) {
    Application.launch(JXrxApp::class.java, *args)
}



