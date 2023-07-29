import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JFileChooser

@Composable
@Preview
fun FrameWindowScope.App() {
    var currentImage by mutableStateOf<String?>(null)
    MaterialTheme {
        Column {

            Button(onClick = { chooseImage()?.let { currentImage = it.toString() } }) {
                Text("Choose image")
            }

            if (currentImage != null) {
                val bitmap =
                try {
                    Files.newInputStream(Paths.get(currentImage)).use {
                        loadImageBitmap(it)
                    }
                } catch (e : Exception) {
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

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
