package com.example.zentrix.features.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.zentrix.domain.model.Product
import com.example.zentrix.ui.theme.ObsidianTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun FavoritesScreen(
    hazeState: HazeState,
    onProductClick: (Product) -> Unit,
    viewModel: FavoritesViewModel = viewModel()
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.markFavoriteAsViewed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianTheme.background)
            .hazeSource(hazeState)
    ) {
        if (favorites.isEmpty()) {
            EmptyFavoritesState()
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Favorites",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = ObsidianTheme.textPrimary
                        )
                        Text(
                            text = "${favorites.size} items",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ObsidianTheme.textSecondary
                        )
                    }
                }

                // Favorites grid
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(favorites.size) { index ->
                        FavoriteProductCard(
                            product = favorites[index],
                            onRemove = { viewModel.removeFavorite(favorites[index].id) },
                            onClick = { onProductClick(favorites[index]) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteProductCard(
    product: Product,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    val cardGradient = listOf(Color(0xFF1A1820), Color(0xFF141218))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(shape)
            .background(Brush.linearGradient(cardGradient))
            .border(0.8.dp, ObsidianTheme.surfaceBorder, shape)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            cardGradient[0].copy(0.6f),
                            cardGradient[1]
                        )
                    )
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Delete button
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(ObsidianTheme.red.copy(0.2f))
                    .border(0.5.dp, ObsidianTheme.red.copy(0.5f), CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Remove",
                    tint = ObsidianTheme.red,
                    modifier = Modifier.size(15.dp)
                )
            }

            // Product info
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    product.brand,
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.4.sp,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ObsidianTheme.accent
                )
                Text(
                    product.name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = ObsidianTheme.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Text(
                        product.price,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = ObsidianTheme.textPrimary
                    )
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianTheme.surfaceElevated)
                            .border(0.5.dp, ObsidianTheme.surfaceBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Star,
                            null,
                            tint = ObsidianTheme.gold,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            product.rating.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = ObsidianTheme.textPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Rounded.FavoriteBorder,
                contentDescription = null,
                tint = ObsidianTheme.textMuted,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "No favorites yet",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ObsidianTheme.textPrimary
            )
            Text(
                text = "Start adding items you love",
                style = MaterialTheme.typography.bodyMedium,
                color = ObsidianTheme.textSecondary
            )
        }
    }
}