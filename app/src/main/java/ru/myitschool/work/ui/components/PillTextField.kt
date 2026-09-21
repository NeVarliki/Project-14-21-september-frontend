package ru.myitschool.work.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import ru.myitschool.work.ui.theme.WorkTheme

@Composable
fun PillTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    password: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {},
) {
    val colors = WorkTheme.colors
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        isError = isError,
        singleLine = true,
        shape = CircleShape,
        textStyle = MaterialTheme.typography.bodySmall,
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (password) KeyboardType.Password else KeyboardType.Text,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.field,
            unfocusedContainerColor = colors.field,
            disabledContainerColor = colors.field.copy(alpha = 0.6f),
            errorContainerColor = colors.field,
            focusedTextColor = colors.onField,
            unfocusedTextColor = colors.onField,
            disabledTextColor = colors.onField.copy(alpha = 0.5f),
            errorTextColor = colors.onField,
            cursorColor = colors.onField,
            errorCursorColor = colors.error,
            focusedPlaceholderColor = colors.onField.copy(alpha = 0.4f),
            unfocusedPlaceholderColor = colors.onField.copy(alpha = 0.4f),
            disabledPlaceholderColor = colors.onField.copy(alpha = 0.25f),
            errorPlaceholderColor = colors.onField.copy(alpha = 0.4f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = colors.error,
        )
    )
}
