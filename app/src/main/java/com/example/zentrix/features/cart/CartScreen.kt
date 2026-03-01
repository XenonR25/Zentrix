package com.example.zentrix.features.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.zentrix.domain.model.CartItem
import com.example.zentrix.ui.theme.ObsidianTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun CartScreen(
    paddingValues: PaddingValues,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()



    Box(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ){
        if(cartItems.isEmpty()){
            EmptyCartState()
        } else {
            Column(modifier = Modifier.fillMaxSize()){ // this will push my content up
                //Header
                Box(modifier = Modifier.fillMaxWidth()
                    .padding(32.dp)) {
                    Column{
                        Text(
                            text = "Shopping Cart",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = ObsidianTheme.textPrimary
                        )
                        Text(
                            text = "${cartItems.size} items",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ObsidianTheme.textSecondary
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp,bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems , key = {it.product.id}) {cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onQuantityChange = {newQuantity ->
                                viewModel.updateQuantity(cartItem.product.id, newQuantity)
                            },
                            onRemove = {viewModel.removeFromCart(cartItem.product.id)}
                        )
                    }
                }
            }
            Surface(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .height(80.dp)
                ){
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Column {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.labelMedium,
                                color = ObsidianTheme.textSecondary
                            )
                            Text(
                                text = "£${String.format("%.2f", totalPrice)}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = ObsidianTheme.textPrimary
                            )
                        }
                        //This is the checkout button, it will consist the checkout logic
                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ObsidianTheme.accent.copy(0.3f))
                                .border(0.5.dp, ObsidianTheme.accent.copy(0.6f), RoundedCornerShape(14.dp))
                                .clickable{} //For checkout box or checkout screen
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center

                        ){
                            Text(text = "Checkout",style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),color = ObsidianTheme.accent)
                        }
                    }
                }
            }
        }

    }

}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove:() -> Unit
){
    val shape = RoundedCornerShape(20.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = shape,
        color = ObsidianTheme.surfaceElevated,
        tonalElevation = 2.dp
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.8.dp, ObsidianTheme.surfaceElevated, shape)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            AsyncImage(
                model = cartItem.product.imageUrl,
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ObsidianTheme.background),
                contentScale = ContentScale.Crop

            )
            //Product Details
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ){
                Column(verticalArrangement = Arrangement.spacedBy(4.dp))
                {
                    Text(cartItem.product.brand,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold ),
                        color = ObsidianTheme.accent
                    )
                    Text(text = cartItem.product.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ObsidianTheme.textPrimary,
                        maxLines = 2
                        )
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = cartItem.product.price,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = ObsidianTheme.textPrimary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuantityButton(
                            icon = Icons.Rounded.Remove,
                            onClick = {onQuantityChange(cartItem.quantity - 1)}
                        )
                        Text(
                            text = cartItem.quantity.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = ObsidianTheme.textPrimary
                        )
                        QuantityButton(
                            icon = Icons.Rounded.Add,
                            onClick = {onQuantityChange(cartItem.quantity + 1)}
                        )
                    }
                }
            }
            //Delete Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ObsidianTheme.red.copy(0.15f))
                    .border(0.5.dp,ObsidianTheme.red.copy(0.4f), CircleShape)
                    .clickable(indication = null, interactionSource = remember{ MutableInteractionSource() }) {
                     onRemove();
                    },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = ObsidianTheme.red,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun QuantityButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(ObsidianTheme.accent.copy(0.15f))
            .border(0.5.dp, ObsidianTheme.accent.copy(0.4f), CircleShape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = ObsidianTheme.accent,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun EmptyCartState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Rounded.ShoppingCart,
                contentDescription = null,
                tint = ObsidianTheme.textMuted,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "Your cart is empty",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ObsidianTheme.textPrimary
            )
            Text(
                text = "Add items to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = ObsidianTheme.textSecondary
            )
        }
    }
}