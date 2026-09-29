package com.example.anotafacil.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnotationProductsStatusDialog(
    text: String,
    icon: ImageVector = Icons.Default.Check,
    iconColor: Color = MaterialTheme.colorScheme.onPrimary,
    containerIconColor: Color = MaterialTheme.colorScheme.primary,
    confirmButtonText: String = "OK",
    confirmButtonTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    confirmButtonContainerColor: Color = MaterialTheme.colorScheme.primary,
    confirmClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 36.dp, bottom = 28.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ícone de Check com fundo azul circular
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = containerIconColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Sucesso",
                            tint = iconColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Título Principal
                    Text(
                        text = text,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))


                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Button(
                            onClick = confirmClick,
                            colors = ButtonDefaults.buttonColors(
                                contentColor = confirmButtonTextColor,
                                containerColor = confirmButtonContainerColor
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = confirmButtonText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
}


@Preview(showBackground = true)
@Composable
fun PreviewAnnotationProductsStatusDialog() {
    AnnotationProductsStatusDialog("",)
}