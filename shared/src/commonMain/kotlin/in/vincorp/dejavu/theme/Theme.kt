package in.vincorp.dejavu.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import in.vincorp.dejavu.resources.Res
import in.vincorp.dejavu.resources.circula
import org.jetbrains.compose.resources.Font

@Composable
fun DejaVuTheme(content: @Composable () -> Unit) {
    val circulaFont = FontFamily(Font(Res.font.circula))
    val typography = remember(circulaFont) {
        Typography(
            displayLarge = TextStyle(
                fontFamily = circulaFont,
                fontWeight = FontWeight.Normal,
                fontSize = 60.sp,
                color = PrimaryText
            ),
            headlineLarge = TextStyle(
                fontFamily = circulaFont,
                fontWeight = FontWeight.Normal,
                fontSize = 50.sp,
                color = PrimaryText
            ),
            headlineMedium = TextStyle(
                fontFamily = circulaFont,
                fontWeight = FontWeight.Normal,
                fontSize = 30.sp,
                color = PrimaryText
            ),
            titleLarge = TextStyle(
                fontFamily = circulaFont,
                fontWeight = FontWeight.Normal,
                fontSize = 30.sp,
                color = PrimaryText
            ),
            bodyLarge = TextStyle(
                fontFamily = circulaFont,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = PrimaryText
            ),
            labelLarge = TextStyle(
                fontFamily = circulaFont,
                fontWeight = FontWeight.Normal,
                fontSize = 30.sp,
                color = PrimaryText
            )
        )
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = LighterTheme,
            onPrimary = PrimaryText,
            secondary = Yellow,
            onSecondary = PrimaryText,
            tertiary = Green,
            onTertiary = PrimaryText,
            background = Background,
            onBackground = PrimaryText,
            surface = Background,
            onSurface = PrimaryText,
            error = Yellow
        ),
        typography = typography,
        content = content
    )
}

@Composable
fun rememberCirculaFontFamily(): FontFamily {
    return FontFamily(Font(Res.font.circula))
}
