package com.thevinesh.dejavu.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.thevinesh.dejavu.resources.Res
import com.thevinesh.dejavu.resources.comfortaa_variable
import org.jetbrains.compose.resources.Font

@Composable
fun DejaVuTheme(content: @Composable () -> Unit) {
    val comfortaa = rememberComfortaaFontFamily()
    val typography = remember(comfortaa) {
        Typography(
            displayLarge = TextStyle(
                fontFamily = comfortaa,
                fontWeight = FontWeight.Normal,
                fontSize = 60.sp,
                color = CloudWhite
            ),
            displayMedium = TextStyle(
                fontFamily = comfortaa,
                fontWeight = FontWeight.Normal,
                fontSize = 50.sp,
                color = CloudWhite
            ),
            headlineLarge = TextStyle(
                fontFamily = comfortaa,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = CloudWhite
            ),
            headlineMedium = TextStyle(
                fontFamily = comfortaa,
                fontWeight = FontWeight.Normal,
                fontSize = 30.sp,
                color = CloudWhite
            ),
            titleLarge = TextStyle(
                fontFamily = comfortaa,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                color = CloudWhite
            ),
            bodyLarge = TextStyle(
                fontFamily = comfortaa,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = CloudWhite
            ),
            labelLarge = TextStyle(
                fontFamily = comfortaa,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = CloudWhite
            ),
            labelMedium = TextStyle(
                fontFamily = comfortaa,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = CloudWhite
            )
        )
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Coral,
            onPrimary = CloudWhite,
            secondary = SunshineYellow,
            onSecondary = StageRed,
            tertiary = Teal,
            onTertiary = CloudWhite,
            background = StageRed,
            onBackground = CloudWhite,
            surface = StageRed,
            onSurface = CloudWhite,
            error = SunshineYellow
        ),
        typography = typography,
        content = content
    )
}

@Composable
fun rememberComfortaaFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.comfortaa_variable, weight = FontWeight.Normal),
        Font(Res.font.comfortaa_variable, weight = FontWeight.Bold)
    )
}
