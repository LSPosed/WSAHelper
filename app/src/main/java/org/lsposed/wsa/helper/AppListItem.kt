package org.lsposed.wsa.helper

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import org.lsposed.wsa.helper.data.model.App
import org.lsposed.wsa.helper.ui.MarqueeText

@Composable
fun AppListItem(app: App, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp).fillMaxWidth(),
        shadowElevation = if (app.selected) 20.dp else 2.dp,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Row(
            modifier = Modifier.selectable(false, onClick = onClick),
        ) {
            AppIcon(app)
            Column(
                modifier = Modifier
                    .padding(start=16.dp, end=16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                MarqueeText(app.label.toString(), style = typography.titleLarge)
                MarqueeText(
                    "${app.versionName} (${app.versionCode})",
                    style = typography.titleMedium,
                    overflow = TextOverflow.Ellipsis
                )
                MarqueeText(app.packageName, style = typography.bodyLarge)
            }
        }
    }
}

@Composable
fun AppIcon(app: App) {
    Image(
        painter = rememberDrawablePainter(drawable = app.icon),
        contentDescription = app.packageName,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(8.dp)
            .size(84.dp)
            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
    )

}
