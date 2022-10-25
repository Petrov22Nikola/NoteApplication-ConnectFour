import javafx.application.Application
import javafx.beans.property.ReadOnlyIntegerWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.Stage
import java.util.*

var dataIndex = 0
var data = Vector<Vector<String>>()
val labelArray = FXCollections.observableArrayList<String>()

val root = VBox()

// MVVM
val vm = DataViewModel()
var vw = DataView(vm)

object DataModel {
    private val dataIndex = ReadOnlyIntegerWrapper(0)
    val DataIndex = dataIndex.readOnlyProperty
    fun updateIndex(i: Int) {
        if (root.children.size != 0) {
            dataIndex.value = i
            vw = DataView(vm)
            root.children.remove(1, root.children.size)
            root.children.add(vw)
        }
    }
}

class DataViewModel {
    val IndexProperty = SimpleStringProperty()
    init {
        DataModel.DataIndex.addListener { _, _, new ->
            new as Int
            IndexProperty.value = "${new}"
        }
    }
    fun UpdateIndex(i: Int) {
        DataModel.updateIndex(i)
    }
}

class DataView(viewModel: DataViewModel) : VBox() {
    init {
        if (data.size != 0) {
            val mainScreen = HBox()
            val dataBox = VBox().apply { spacing = 10.0 }
            val graphBox = VBox()
            dataBox.children.add(
                Label("Dataset name: ${labelArray[dataIndex]}").apply {
                    padding = Insets(0.0, 0.0, 0.0, 10.0) }
            )
            for ((index, value) in data[dataIndex].withIndex()) {
                dataBox.children.add(
                    HBox(
                        Region().apply { prefWidth = 10.0 },
                        Label().apply { text = "Entry #${index}" },
                        TextField().apply {
                            text = value
                            this.textProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                                data[dataIndex][index] = this.text
                            })
                        },
                        Button().apply {
                            text = "X"
                            setOnAction {
                                if (data[dataIndex].size > 1) {
                                    data[dataIndex].removeElementAt(index)
                                    vm.UpdateIndex(dataIndex)
                                }
                            }
                        },
                        Region().apply { prefWidth = 10.0 }
                    ).apply {
                        spacing = 10.0
                    }
                )
            }
            dataBox.children.add(
                HBox(
                    Button().apply {
                        text = "Add Entry";
                        setOnAction {
                        data[dataIndex].add("0.0")
                        vm.UpdateIndex(dataIndex) }
                        prefWidth = 200.0
                    }
                ).apply { alignment = Pos.CENTER }
            )
            val scrollArea = ScrollPane(dataBox).apply {
                vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                isFitToHeight = true
                style = "-fx-background-color: transparent; "
            }
            mainScreen.children.addAll(
                scrollArea,
                Separator().apply {
                    orientation = Orientation.VERTICAL
                    prefHeight = 5000.0 },
                graphBox)
            children.add(mainScreen)
        }
    }
}

class Main : Application()  {
    override fun start(primaryStage: Stage) {
        // Toolbar
        val toolbar = ToolBar()
        toolbar.prefWidthProperty().bind(primaryStage.widthProperty())

        // Data Set Selector - Dropdown
        var comboBox = ComboBox(FXCollections.observableArrayList(labelArray)).apply { prefWidth = 100.0; }
        vm.UpdateIndex(dataIndex)
        comboBox.valueProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            dataIndex = comboBox.selectionModel.selectedIndex
            vm.UpdateIndex(dataIndex)
        })
        toolbar.items.add(comboBox)

        // Separator
        toolbar.items.addAll(
            Region().apply{ minWidth = 10.0 },
            Separator().apply { orientation = Orientation.VERTICAL },
            Region().apply{ minWidth = 10.0 }
        )

        // Data Set Creator - Input Field & Create Button
        val dataField = TextField().apply { promptText = "Data set name" }
        val createBt = Button("Create").apply { minWidth = 50.0 }
        // Create Button - Functioality
        createBt.setOnAction {
            toolbar.items.remove(comboBox)
            labelArray.add(dataField.text)
            dataField.text = ""
            comboBox = ComboBox(FXCollections.observableArrayList(labelArray)).apply {
                value = this.items[this.items.size - 1].toString()
                prefWidth = 100.0
            }
            dataIndex = comboBox.items.size - 1;
            var initV = Vector<String>()
            initV.add("0.0")
            data.add(initV)
            vm.UpdateIndex(dataIndex)
            comboBox.valueProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                dataIndex = comboBox.selectionModel.selectedIndex
                vm.UpdateIndex(dataIndex)
            })
            toolbar.items.add(0, comboBox)
        }
        toolbar.items.addAll(dataField, createBt)

        // Separator
        toolbar.items.addAll(
            Region().apply{ minWidth = 10.0 },
            Separator().apply { orientation = Orientation.VERTICAL },
            Region().apply{ minWidth = 10.0 }
        )

        // Visualization Selector - Buttons
        val lineBt = Button("Line")
        lineBt.prefWidth = 50.0
        val barBt = Button("Bar")
        barBt.prefWidth = 50.0
        val semBt = Button("Bar (SEM)")
        semBt.prefWidth = 70.0
        val pieBt = Button("Pie")
        pieBt.prefWidth = 50.0

        toolbar.items.addAll(lineBt, barBt, semBt, pieBt)

        // Title
        primaryStage.title = "CS349 - A2 Graphs - n3petrov"

        // Update Root
        root.children.addAll(toolbar, vw)

        // Default Dimensions
        primaryStage.scene = Scene(root,800.0, 600.0)

        // Minimum Dimensions
        primaryStage.minWidth = 640.0
        primaryStage.minHeight = 480.0
        primaryStage.show()
    }
}