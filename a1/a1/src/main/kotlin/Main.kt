import javafx.application.Application
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import java.awt.Color
import java.util.Vector
import javax.swing.Scrollable

class Main : Application()  {
    override fun start(primaryStage: Stage) {
        val rootVBox = VBox()

        // Toolbar
        val toolbar = ToolBar()
        toolbar.prefWidthProperty().bind(primaryStage.widthProperty())
        toolbar.style = "-fx-spacing: 10;"

        // View Switch Group
        val viewLabel = Label("View:")
        viewLabel.padding = Insets(10.0,0.0,10.0,0.0)
        val listView = Button("List")
        listView.prefWidth = 50.0
        val gridView = Button("Grid")
        gridView.prefWidth = 50.0
        listView.opacity = 0.5
        gridView.requestFocus()
        toolbar.items.add(viewLabel)
        toolbar.items.add(listView)
        toolbar.items.add(gridView)

        val s1 = Separator()
        s1.orientation = Orientation.VERTICAL
        toolbar.items.add(s1)

        // Show Archived Group
        val archiveCheck = CheckBox()
        toolbar.items.add(Label("Show archived:"))
        toolbar.items.add(archiveCheck)
        val s2 = Separator()
        s2.orientation = Orientation.VERTICAL
        toolbar.items.add(s2)

        // Ordering Group
        val labelArray = FXCollections.observableArrayList<String>(
            "Length (asc)",
            "Length (desc)"
        )
        val comboBox = ComboBox(FXCollections.observableArrayList(labelArray))
        comboBox.value = "Length (asc)"
        toolbar.items.addAll(Label("Order by:"), comboBox)

        // Clear
        val clearButton = Button("Clear").apply { prefWidth = 50.0 }

        // Status Bar
        val statBar = ToolBar()
        val statVal = Label("0 notes, 0 of which are active")
        statBar.items.add(statVal)

        // Adding ToolBar children
        val r1 = Region().apply { prefWidth = 10.0 }
        HBox.setHgrow(r1, Priority.ALWAYS)
        toolbar.items.add(r1)
        toolbar.items.add(clearButton)

        // List Format - Special Create Note
        val listNotesVBox = VBox().apply { spacing = 20.0 }
        val txtArea = TextArea().apply { prefHeight = 42.0;
            style = "-fx-border-insets: -10; -fx-border-color: #fc9f77; -fx-border-width: 10; -fx-border-radius: 2; " +
                    "-fx-border-style: solid;" }
        HBox.setHgrow(txtArea, Priority.ALWAYS)
        val createButton = Button("Create").apply { prefWidth = 75.0; prefHeight = 42.0;
            style = "-fx-border-insets: -10; -fx-border-color: #fc9f77; -fx-border-width: 10; -fx-border-radius: 2; " +
                    "-fx-border-style: solid;"}
        val listSpecialNoteHBox = HBox(Region().apply { prefWidth = 10.0 }, txtArea, createButton)
        listSpecialNoteHBox.prefWidthProperty().bind(primaryStage.widthProperty())
        listSpecialNoteHBox.alignment = Pos.CENTER
        listSpecialNoteHBox.spacing = 10.0
        listSpecialNoteHBox.padding = Insets(0.0, 20.0, 10.0, 0.0)

        // List View Scrollable Functionality
        val listScroll = ScrollPane(listNotesVBox).apply {
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            style = "-fx-background-color: transparent; "
            isFitToWidth = true
            isFitToHeight = true
        }

        // All Messages
        var msgV = Vector<Pair<String, Boolean>>()
        var changes = 0
        var archived = 0

        val lmbdaStatus = { ->
            // Update Status Bar - Functionality
            var notesNumber = msgV.size
            if (notesNumber != 1) statVal.text = "$notesNumber notes, ${notesNumber - archived} of which are active"
            else statVal.text = "$notesNumber note, ${notesNumber - archived} of which is active"
        }

        val lmbda = { ->
            // Update global messages
            if (comboBox.value == "Length (desc)") msgV.sortBy { it.first.length }
            else msgV.sortByDescending { it.first.length }
            // Clear messages from screen
            listNotesVBox.children.remove(2, listNotesVBox.children.size)
            // Redraw all messages
            var i = 0
            for (msg in msgV) {
                ++i
                val arch = CheckBox().apply { id = "${i}" }
                if (msg.second) arch.isSelected = true
                if (arch.isSelected && !archiveCheck.isSelected) continue
                val ll = TextArea("${msg.first}").apply {
                    if (arch.isSelected) style = "-fx-control-inner-background: #d4d2d4; "
                    else style = "-fx-control-inner-background: #ffffe0; "
                    isWrapText = true
                    prefHeight = 100.0
                    isEditable = false
                    prefWidthProperty().bind(txtArea.widthProperty())
                }
                arch.selectedProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                    if (arch.isSelected) {
                        ll.style = "-fx-control-inner-background: #d4d2d4; "
                        msgV[arch.id.toInt() - 1] = Pair(msgV[arch.id.toInt() - 1].first, true)
                        archived++
                        if (!archiveCheck.isSelected) {
                            archiveCheck.id = "${changes}"
                            changes++
                        }
                        lmbdaStatus()
                    }
                    else {
                        ll.style = "-fx-control-inner-background: #ffffe0; "
                        msgV[arch.id.toInt() - 1] = Pair(msgV[arch.id.toInt() - 1].first, false)
                        if (archived > 0) --archived
                        // Update Status Bar - Functionality
                        lmbdaStatus()
                    }
                })
                val msgHBox = HBox(
                    Region().apply { prefWidth = 20.0 },
                    ll,
                    Region().apply { prefWidth = 10.0 },
                    arch,
                    Region().apply { prefWidth = 10.0 },
                    Label("Archived"),
                    Region().apply { prefWidth = 20.0 }
                )
                listNotesVBox.children.addAll(
                    Region(),
                    msgHBox
                )
            }
            lmbdaStatus()
        }

        // Create Notes On Click - Functionality
        createButton.setOnAction {
            // Update global messages
            msgV.add(Pair(txtArea.text, false))
            lmbda()
        }

        listNotesVBox.children.addAll(Region(), listSpecialNoteHBox)

        // Grid Format - Special Create Note
        val gtxtArea = TextArea().apply { minHeight = 205.0; maxHeight = 205.0; minWidth = 205.0; maxWidth = 205.0;
            style = "-fx-border-insets: -10; -fx-border-color: #fc9f77; -fx-border-width: 10; -fx-border-radius: 2; " +
                    "-fx-border-style: solid;" }
        val gcreateButton = Button("Create").apply { prefWidth = 205.0; prefHeight = 42.0;
            style = "-fx-border-insets: -10; -fx-border-color: #fc9f77; -fx-border-width: 10; -fx-border-radius: 2; " +
                    "-fx-border-style: solid;"}
        val gridPane = TilePane()
        gridPane.padding = Insets(20.0, 20.0, 20.0, 20.0)
        val gridVBox = VBox()
        gridVBox.children.addAll(gtxtArea, gcreateButton)
        gridPane.children.add(gridVBox)
        gridPane.hgap = 20.0
        gridPane.vgap = 20.0

        // Grid View Scrollable Functionality
        val gridScroll = ScrollPane(gridPane).apply {
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            style = "-fx-background-color: transparent; "
            isFitToWidth = true
            isFitToHeight = true
            prefHeightProperty().bind(listScroll.heightProperty())
        }

        val glmbda = { ->
            // Update global messages
            if (comboBox.value == "Length (desc)") msgV.sortBy { it.first.length }
            else msgV.sortByDescending { it.first.length }
            // Clear messages from screen
            gridPane.children.remove(1, gridPane.children.size)
            // Redraw all messages
            var i = 0
            for (msg in msgV) {
                ++i
                val arch = CheckBox().apply { id = "${i}" }
                if (msg.second) arch.isSelected = true
                if (arch.isSelected && !archiveCheck.isSelected) continue
                val ll = TextArea("${msg.first}").apply {
                    if (arch.isSelected) style = "-fx-control-inner-background: #d4d2d4; "
                    else style = "-fx-control-inner-background: #ffffe0; "
                    isWrapText = true
                    isEditable = false
                    minHeight = 205.0; maxHeight = 205.0; minWidth = 205.0; maxWidth = 205.0;
                }
                arch.selectedProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                    if (arch.isSelected) {
                        ll.style = "-fx-control-inner-background: #d4d2d4; "
                        msgV[arch.id.toInt() - 1] = Pair(msgV[arch.id.toInt() - 1].first, true)
                        archived++
                        if (!archiveCheck.isSelected) {
                            archiveCheck.id = "${changes}"
                            changes++
                        }
                        lmbdaStatus()
                    }
                    else {
                        ll.style = "-fx-control-inner-background: #ffffe0; "
                        msgV[arch.id.toInt() - 1] = Pair(msgV[arch.id.toInt() - 1].first, false)
                        if (archived > 0) --archived
                        // Update Status Bar - Functionality
                        lmbdaStatus()
                    }
                })
                val msgVBox = VBox(
                    ll,
                    Region().apply { prefHeight = 10.0; },
                    HBox(
                        arch,
                        Region().apply { prefWidth = 10.0 },
                        Label("Archived")
                    )
                )
                gridPane.children.addAll(msgVBox)
            }
            lmbdaStatus()
        }

        // Create Grid Notes - Functionality
        gcreateButton.setOnAction {
            msgV.add(Pair(txtArea.text, false))
            glmbda()
        }

        var lst = true

        // View Change - Functionality
        listView.setOnAction {
            if (!lst) {
                lst = true
                rootVBox.children.remove(1, 2)
                rootVBox.children.add(1, listScroll)
                lmbda()
                listView.opacity = 0.5
                gridView.opacity = 1.0
                gridView.requestFocus()
            }
        }

        gridView.setOnAction {
            if (lst) {
                lst = false
                rootVBox.children.remove(1, 2)
                rootVBox.children.add(1, gridScroll)
                glmbda()
                listView.opacity = 1.0
                gridView.opacity = 0.5
                listView.requestFocus()
            }
        }

        // Clear Notes On Click - Functionality
        clearButton.setOnAction {
            /* Index starts at 2 so that we only delete created notes
               and not the special note or preceding space!     */
            msgV.clear()
            archived = 0
            listNotesVBox.children.remove(2, listNotesVBox.children.size)
            gridPane.children.remove(1, gridPane.children.size)
            // Update Status Bar - Functionality
            statVal.text = "0 notes, 0 of which are active"
        }

        comboBox.valueProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            lmbda()
            glmbda()
        })

        // Displaying / Hiding Archived Notes
        archiveCheck.selectedProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            lmbda()
            glmbda()
        })

        // Force recalculation
        archiveCheck.idProperty().addListener { observable, oldValue, newValue ->
            lmbda()
            glmbda()
        }

        // Initial 4 Notes
        msgV.add(Pair("Drive the car to the mechanic for an oil change", false))
        msgV.add(Pair("Finish your assignments before reading week", true))
        msgV.add(Pair("Study for upcoming exams", false))
        msgV.add(Pair("Buy groceries for this week", true))
        archived = 2
        lmbda()
        glmbda()

        // Title
        primaryStage.title = "CS349 - A1 Notes - n3petrov"
        val r2 = Region().apply { prefWidth = 10.0 }
        VBox.setVgrow(r2, Priority.ALWAYS)
        rootVBox.children.addAll(toolbar, listScroll, r2, Separator(), statBar)
        primaryStage.scene = Scene(rootVBox,800.0, 600.0)
        // Minimums
        primaryStage.minWidth = 640.0
        primaryStage.minHeight = 480.0
        primaryStage.show()
    }
}