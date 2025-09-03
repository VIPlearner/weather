import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(
    searchMode: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    onKeyboardSearch: () -> Unit,
    onClickPinpoint: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier,
    onClickSearch: ((Boolean) -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }

    // Request focus when entering search mode
    LaunchedEffect(searchMode) {
        if (searchMode) {
            focusRequester.requestFocus()
        }
    }

    Row(modifier){
        if (searchMode) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions { onKeyboardSearch() },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                interactionSource = interactionSource,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    DecorationBox(
                        value = value,
                        searchMode = searchMode,
                        onCancelSearch = { onClickSearch?.invoke(false) },
                        onValueChange = onValueChange,
                        interactionSource = interactionSource,
                        innerTextField = innerTextField
                    )
                }
            )
        } else {
            val interactionSource = remember { MutableInteractionSource() }
            DecorationBox(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(true),
                        onClick = { onClickSearch?.invoke(true) }
                    ),
                searchMode = searchMode,
                value = "",
                onValueChange = {},
                onCancelSearch = { onClickSearch?.invoke(false) },
                interactionSource = interactionSource,
                innerTextField = {}
            )
        }

        IconButton(
            modifier = Modifier
                .animateContentSize(tween())
                .then(if (searchMode) Modifier.size(0.dp) else Modifier),
            onClick = {onClickPinpoint() }
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DecorationBox(
    modifier: Modifier = Modifier,
    searchMode: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    onCancelSearch: () -> Unit,
    interactionSource: MutableInteractionSource,
    innerTextField: @Composable () -> Unit,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val onBackgroundColor = MaterialTheme.colorScheme.onSurface

    val border = BorderStroke(
        width = 1.dp,
        color = backgroundColor
    )

    Surface(
        modifier = modifier.height(45.dp),
        shape = CircleShape,
        border = border,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = onBackgroundColor,
                modifier = Modifier.padding(end = 8.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "Search...",
                        style = MaterialTheme.typography.bodyLarge.copy(color = onBackgroundColor),
                    )
                }

                if (isFocused || value.isNotEmpty()) {
                    innerTextField()
                }
            }

            if (searchMode) {
                CloseIcon(
                    onClick = {
                        onValueChange("")
                        onCancelSearch()
                              },
                    modifier = Modifier
                )
            }
        }
    }
}


@Composable
fun CloseIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Box(modifier = modifier
        .size(30.dp)
        .clip(CircleShape)
        .clickable {
            onClick()
        }
        .background(color = MaterialTheme.colorScheme.surfaceContainerHighest, shape = CircleShape)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .width(24.dp)
                .height(24.dp)
                .padding(3.dp),
            imageVector = Icons.Default.Close,
            contentDescription = "close button",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
