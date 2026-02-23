package com.example.zentrix.features.favorites

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.zentrix.domain.model.Product
import dev.chrisbanes.haze.HazeState

@Composable
fun FavoritesScreen(
    hazeState: HazeState,
    onProductClick: (Product) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {

}