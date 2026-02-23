package com.example.zentrix.features.product

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.zentrix.domain.model.Product
import com.example.zentrix.features.cart.CartViewModel
import com.example.zentrix.features.favorites.FavoritesViewModel
import com.example.zentrix.ui.theme.ObsidianTheme
import com.google.android.gms.common.Feature

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
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                    , horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    if(product.isNew){
                        DetailBadge(text = "New", color = ObsidianTheme.accent)
                    }
                    if(product.discount != null){
                        DetailBadge(text= product.discount , color = ObsidianTheme.red)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp, fontWeight = FontWeight.Bold),
                    color = ObsidianTheme.accent
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    repeat(5){index->
                        Icon(
                            Icons.Rounded.Star,
                            contentDescription = null,
                            tint = if(index < product.rating.toInt()) ObsidianTheme.gold else ObsidianTheme.surfaceBorder,
                            modifier = Modifier.size(20.dp)

                        )
                    }
                    Text(
                        text = "${product.rating} (245 reviews)",
                        style = MaterialTheme.typography.labelSmall,
                        color = ObsidianTheme.textSecondary
                    )
                }

                HorizontalDivider(
                    color = ObsidianTheme.surfaceBorder,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                //Description
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ObsidianTheme.textPrimary
                    )

                Text(
                    text = generateDescription(product),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ObsidianTheme.textSecondary,
                    lineHeight = 22.sp
                )

                HorizontalDivider(
                    color = ObsidianTheme.surfaceBorder,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 8.dp)

                )

                //Features
                Text(
                    text = "Key Features",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ObsidianTheme.textPrimary
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)){
                    FeatureItem("Premium Quality Materials")
                    FeatureItem("Sustainable & ethically sourced")
                    FeatureItem("Free shipping on orders over £50")
                    FeatureItem("30 day return policy")
                }
            }
        }

        //Bottom bar with price and add to cart
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(90.dp),
            color = ObsidianTheme.surfaceElevated.copy(0.95f),
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ){
          Row(
              modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
          )  {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)){
                  Text(
                      text = "Total Price",
                      style = MaterialTheme.typography.labelSmall,
                      color = ObsidianTheme.textSecondary
                  )
                  Row(
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalAlignment = Alignment.CenterVertically
                  ){
                      Text(product.price, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = ObsidianTheme.textPrimary)
                      product.originalPrice?.let{
                          Text(text = it,style = MaterialTheme.typography.bodySmall,color = ObsidianTheme.textMuted)
                      }
                  }
              }
              val buttonColor by animateColorAsState(
                  if(isInCart) ObsidianTheme.green else ObsidianTheme.accent,
                  label = ""
              )
              Box(
                  modifier = Modifier
                      .height(56.dp)
                      .clip(RoundedCornerShape(16.dp))
                      .background(buttonColor)
                      .clickable(
                          indication = null,
                          interactionSource = remember { MutableInteractionSource() }
              ){
                          cartViewModel.addToCart(product)
                          showAddedToCart = true
                      }
                      .padding(horizontal = 32.dp),
                  contentAlignment = Alignment.Center
              ){
               Row(
                   horizontalArrangement = Arrangement.spacedBy(8.dp),
                   verticalAlignment = Alignment.CenterVertically
               )   {
                   Icon(
                       Icons.Rounded.ShoppingCart,
                       contentDescription = null,
                       tint = Color.White,
                       modifier = Modifier.size(20.dp)
                   )
                   Text(
                       text = if(isInCart) "Added To Cart " else "Add to Cart",
                       style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                       color = Color.White
                   )
                 }
              }
          }
        }
        if(showAddedToCart){
            Box(
                Modifier.align(Alignment.TopCenter)
                    .padding(top = 60.dp)
            ){
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ObsidianTheme.green,
                    shadowElevation = 8.dp
                ){
                    Text(
                        text = "✓ Added to cart",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureItem(features : String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(ObsidianTheme.accent)
        )
        Text(text = features , style = MaterialTheme.typography.bodyMedium, color = ObsidianTheme.textSecondary)
    }
}

@Composable
fun DetailBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(0.20f))
            .border(0.8.dp,color.copy(0.5f),RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp , vertical = 6.dp)
    ){
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            ), color = color
        )
    }
}

private fun generateDescription(product: Product): String {
    return when {
        product.name.contains("Watch",ignoreCase = true) ->
            "Precision-engineered timepiece combining classic design with modern functionality. Features scratch-resistant crystal, water resistance, and refined craftsmanship that stands the test of time."
        product.name.contains("Shoe",ignoreCase = true) || product.name.contains("Runner",ignoreCase = true) || product.name.contains("Loafer", ignoreCase = true) ->
            "Expertly crafted footwear that balances comfort and style. Premium materials ensure durability while the ergonomic design provides all-day comfort for any occasion."
        product.name.contains("Shirt",ignoreCase = true) || product.name.contains("Hoodie", ignoreCase = true) || product.name.contains("Rollneck",ignoreCase = true) || product.name.contains("Vest",ignoreCase = true) || product.name.contains("Coat",ignoreCase = true) ->
            "Thoughtfully designed apparel that elevates your everyday wardrobe. Made from premium fabrics with attention to fit and finish, offering both comfort and contemporary style."
        product.name.contains("Chino", ignoreCase = true) || product.name.contains("Denim", ignoreCase = true) ->
            "Essential bottom wear that combines classic styling with contemporary fit. Premium fabric construction ensures lasting quality and all-day comfort."

        product.name.contains("Earbuds", ignoreCase = true) || product.name.contains("Headphones", ignoreCase = true) || product.name.contains("Speaker", ignoreCase = true) ->
            "Immersive audio experience with crystal-clear sound quality. Advanced technology delivers premium acoustics while ergonomic design ensures comfortable extended use."

        product.name.contains("Keyboard", ignoreCase = true) || product.name.contains("Mouse", ignoreCase = true) ->
            "Precision-engineered peripheral designed for optimal performance. Responsive feedback and ergonomic comfort enhance your productivity and gaming experience."

        product.name.contains("Camera", ignoreCase = true) ->
            "Capture life's moments in stunning detail with advanced imaging technology. Versatile features and intuitive controls make professional-quality content creation accessible."

        product.name.contains("Tablet", ignoreCase = true) ->
            "Powerful portable computing in a sleek, lightweight design. Vibrant display and responsive performance make it perfect for work, creativity, and entertainment."

        else ->
            "Premium quality product designed with attention to detail and crafted from the finest materials. Combines functionality with elegant aesthetics to enhance your lifestyle."
    }
}
