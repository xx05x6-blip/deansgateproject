package com.example.deansgateproject.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.deansgateproject.data.repository.DeliveryRepository
import com.example.deansgateproject.ui.theme.DeansgateProjectTheme

@Composable
fun MainContainerScreen(
    modifier: Modifier = Modifier,
    repository: DeliveryRepository = DeliveryRepository
) {
    MainDeliveryScreen(
        modifier = modifier,
        repository = repository
    )
}

@Preview(showBackground = true)
@Composable
fun MainContainerScreenPreview() {
    DeansgateProjectTheme {
        MainContainerScreen()
    }
}
