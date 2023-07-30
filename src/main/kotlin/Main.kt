import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.window.*
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.JFileChooser

@Composable
@Preview
fun FrameWindowScope.App() {
    MaterialTheme {
        Column {

            var currentImage by remember { mutableStateOf<Path?>(null) }

            MainMenu(onImageSelection = { currentImage = it })

            val imgFile = currentImage
            if (imgFile != null) {
                val bitmap =
                try {
                    Files.newInputStream(imgFile).use {
                        loadImageBitmap(it)
                    }
                } catch (e : Exception) {
                    // TODO: log error (require to init logging lib/utils)
                    null
                }

                if (bitmap == null) Text("Error: cannot open image", color = Color.Red)
                else {
                    Surface {
                        TagPane(bitmap)
                    }
                }
            }
        }
    }
}

/**
 * WARNING: This is a workaround using swing component to work around the lack of native file explorer
 */
fun FrameWindowScope.chooseImage() : File? {
    val chooser = JFileChooser(System.getProperty("user.home"))
    return when (chooser.showOpenDialog(window)) {
        JFileChooser.APPROVE_OPTION -> chooser.selectedFile
        else -> null
    }
}

@Composable
fun Menu(title: String, dropDownContent: @Composable ColumnScope.() -> Unit) {
    var expand by remember { mutableStateOf(false) }
    TextButton({ expand = true }, colors = ButtonDefaults.buttonColors()) { Text(title) }
    DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
        dropDownContent()
    }
}

@Composable
fun FrameWindowScope.MainMenu(onImageSelection: (Path) -> Unit) {
    TopAppBar {
        val menuButtonColors = ButtonDefaults.buttonColors()
        TextButton(onClick = { chooseImage()?.let { onImageSelection(it.toPath()) } }, colors = menuButtonColors) {
            Text("Open image")
        }

        var showAbout by remember { mutableStateOf(false) }

        TextButton(onClick = { showAbout = !showAbout }, colors = menuButtonColors) {
            Icon(Icons.Filled.Info, "About")
            // TODO: rework this. It is very ugly
            if (showAbout) {
                Popup(focusable = true, onDismissRequest = { showAbout = false }) {
                    Column(Modifier.background(MaterialTheme.colors.background)) {
                        HyperLink(URI("https://cecill.info/licences/Licence_CeCILL-B_V1-en.html"), "License: CeCILL-B")
                        HyperLink(URI("https://github.com/alexismanin/tagim"), "Source code")
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
