package com.example.zentrix.features.product

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.zentrix.domain.model.Product
import com.example.zentrix.features.cart.CartViewModel
import com.example.zentrix.features.favorites.FavoritesViewModel
import com.example.zentrix.ui.theme.ObsidianTheme

@Composable
fun ProductDetailScreen(
    product : Product,
    onNavigateBack : () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
){
    val isInCart by remember {derivedStateOf{cartViewModel.isInCart(product.id)}}
    val isFavorite by remember {derivedStateOf{favoritesViewModel.isFavorite(product.id)}}

    var showAddedToCart by remember { mutableStateOf(false) }

    LaunchedEffect(showAddedToCart) {
        if (showAddedToCart) {
            // Delay for 2 seconds before navigating back
            kotlinx.coroutines.delay(2000)
            showAddedToCart = false
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(ObsidianTheme.background)){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ){
            //Product Image with overlay controls
            Box(
                modifier = Modifier.fillMaxWidth().height(400.dp)
            ){
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    ObsidianTheme.background.copy(alpha = 0.3f),
                                    ObsidianTheme.background
                                )
                            )
                        )
                )

                Row(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    //Back button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ObsidianTheme.surfaceElevated.copy(0.9f))
                            .border(0.8.dp, ObsidianTheme.surfaceBorder, RoundedCornerShape(12.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ){onNavigateBack()},
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = ObsidianTheme.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    //Favorite Button
                    val favScale by animateFloatAsState(
                        if(isFavorite) 1.2f else 1f,
                        spring(stiffness = Spring.StiffnessHigh),
                        label = ""
                        )
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ObsidianTheme.surfaceElevated.copy(0.9f))
                            .border(0.8.dp,ObsidianTheme.surfaceBorder, CircleShape)
                            .clickable(
                                indication = null,
                                interactionSource = remember {MutableInteractionSource()}
                            ){favoritesViewModel.toggleFavorite(product)},
                        contentAlignment = Alignment.Center
                    )
                    {
                        Icon(
                            if (isFavorite) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) ObsidianTheme.red else ObsidianTheme.textSecondary,
                            modifier = Modifier.size(20.dp).scale(favScale)
                        )
                    }


                }
            }
        }
    }
}
