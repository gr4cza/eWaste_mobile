package hu.bme.ewaste.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.bme.ewaste.R
import hu.bme.ewaste.data.dto.DetectionResponse
import hu.bme.ewaste.data.dto.Location
import hu.bme.ewaste.data.model.TrashCanType
import java.util.*

@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun DetectionCard(
    detectionResponse: DetectionResponse,
    onClick: (UUID) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(
                bottom = 6.dp,
                top = 6.dp
            )
            .fillMaxWidth(),
        elevation = 7.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row() {
                Icon(
                    ImageVector.vectorResource(id = R.drawable.trash_solid),
                    contentDescription = "",
                    tint = (when (detectionResponse.type) {
                        TrashCanType.TRASH -> "#457539".color
                        TrashCanType.PLASTIC -> "#FBF30A".color
                        TrashCanType.PAPER -> "#3E9AFF".color
                    }),
                    modifier = Modifier
                        .size(50.dp)
                        .align(CenterVertically)
                        .padding(11.dp)
                )
                Text(
                    text = detectionResponse.type.name.replaceFirstChar { it.uppercase() },
                    modifier = Modifier
                        .align(CenterVertically)
                        .fillMaxWidth(0.6f)
                        .padding(6.dp)
                )
            }
            Button(
                onClick = { onClick(detectionResponse.id) },
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 12.dp,
                    end = 20.dp,
                    bottom = 12.dp
                ),
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(end = 6.dp)
            ) {
                Text(text = "Empty")
            }
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    DetectionCard(
        DetectionResponse(
            UUID.randomUUID(),
            TrashCanType.PAPER,
            Location(4.0, 3.0)
        )
    ) {}
}

val String.color
    get() = Color(android.graphics.Color.parseColor(this))