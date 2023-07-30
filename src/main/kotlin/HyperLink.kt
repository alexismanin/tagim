import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import java.net.URI

@Composable
fun HyperLink(href: URI, text: String) {
    val uriHandler = LocalUriHandler.current
    TextButton(onClick = { uriHandler.openUri(href.toString()) }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Blue)) {
        Text(text, textDecoration = TextDecoration.Underline)
// TODO: attempt to display uri as tooltip
//        var interaction by remember { mutableStateOf(MutableInteractionSource()) }
//        Text(text, Modifier.hoverable(interactionSource = interaction), textDecoration = TextDecoration.Underline)
//        val hover by interaction.interactions.map { if (it is HoverInteraction.Enter) true else false }.collectAsState(false)
//        if (hover) Popup(Alignment.BottomEnd) { Text(href.toString()) }
    }
}
