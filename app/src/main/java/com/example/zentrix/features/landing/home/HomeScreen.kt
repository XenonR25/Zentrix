package com.example.zentrix.features.landing.home

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zentrix.core.designsystem.obsidianGlassStyle
import com.example.zentrix.domain.model.Category
import com.example.zentrix.domain.model.Product
import com.example.zentrix.domain.model.PromoItem
import com.example.zentrix.ui.theme.ObsidianTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlin.math.abs
import coil.compose.AsyncImage
import com.example.zentrix.core.designsystem.FilterBottomSheet
import com.example.zentrix.features.cart.CartViewModel
import com.example.zentrix.features.favorites.FavoritesViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Obsidian Design Tokens
// ─────────────────────────────────────────────────────────────────────────────



// ─────────────────────────────────────────────────────────────────────────────
// Mock Data
// ─────────────────────────────────────────────────────────────────────────────





private val promoItems = listOf(
    PromoItem(
        "Summer Drops", "Up to 40% off selected styles", "LIMITED",
        listOf(Color(0xFF1A0E2E), Color(0xFF2D1B4E), Color(0xFF0D0A1A)), ObsidianTheme.accent
    ),
    PromoItem("New Season", "Fresh arrivals landing every week", "NEW IN",
        listOf(Color(0xFF0A1628), Color(0xFF152640), Color(0xFF080E1A)), ObsidianTheme.gold),
    PromoItem("Members Only", "Exclusive deals crafted for you", "VIP",
        listOf(Color(0xFF0A1F18), Color(0xFF0F2E22), Color(0xFF060F0C)), ObsidianTheme.green)
)

private val categories = listOf(
    Category("All", "+"),
    Category("Tops", "👕"),
    Category("Shoes", "👟"),
    Category("Bags", "👜"),
    Category("Watches", "⌚"),
    Category("Jewellery", "💍"),
    Category("Denim", "🧥"),
    Category("Headphone", "🎧"),
    Category("Laptop", "💻"),
    Category("Controller", "🎮")
)




// ─────────────────────────────────────────────────────────────────────────────
// HomeScreen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(hazeState: HazeState , viewModel: HomeViewModel= hiltViewModel() ,onProductClick:(Product)-> Unit, onSignOut : () -> Unit, cartViewModel: CartViewModel = hiltViewModel(),favoritesViewModel: FavoritesViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCat by remember { mutableIntStateOf(0) }
    val name by viewModel.userName.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianTheme.background)
                .hazeSource(state = hazeState),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 16.dp, bottom = 110.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                GreetingHeader(name = name, onSignOut = onSignOut)
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                ObsidianSearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, onFilterClick = { showFilterSheet = true })
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                PromotionsPager(promoItems)
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                SectionHeader("Categories")
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                CategoryChipsRow(
                    categories = categories,
                    selectedIndex = selectedCat,
                    onSelect = { selectedCat = it }
                )
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                SectionHeader("New Arrivals")
            }

            // Display products from API
            items(count = uiState.products.size, key = {index -> uiState.products[index].id}) { index ->
                ProductCard(
                    product = uiState.products[index],
                    index = index,
                    onClick = {onProductClick(uiState.products[index])},
                    favoritesViewModel = favoritesViewModel
                    )
            }

            if(showFilterSheet){
                FilterBottomSheet(){}
            }
        }

        // Loading indicator
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ObsidianTheme.background.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ObsidianTheme.accent)
            }
        }

        // Error message
        uiState.errorMessage?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))  // Dark scrim overlay
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* Dismiss on backdrop click if needed */ },
                contentAlignment = Alignment.Center
            ) {
                // Error dialog card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.Transparent,
                    shadowElevation = 24.dp
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        ObsidianTheme.surfaceElevated.copy(0.95f),
                                        ObsidianTheme.surfaceElevated.copy(0.98f)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(0.3f),
                                        Color.White.copy(0.05f)
                                    )
                                ),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .padding(32.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Error icon with circular background
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(ObsidianTheme.red.copy(0.15f))
                                    .border(1.dp, ObsidianTheme.red.copy(0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.ErrorOutline,
                                    contentDescription = null,
                                    tint = ObsidianTheme.red,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            // Error title
                            Text(
                                text = "Connection Error",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ObsidianTheme.textPrimary
                            )

                            // Error message
                            Text(
                                text = error,
                                color = ObsidianTheme.textSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            // Retry button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .width(24.dp)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(ObsidianTheme.accent.copy(0.2f))
                                    .border(0.7.dp , ObsidianTheme.accent.copy(0.6f) , RoundedCornerShape(16.dp))
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { viewModel.retry() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Retry",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = ObsidianTheme.accent
                                )
                            }

                            // Cancel/Dismiss text button (optional)
                            Text(
                                text = "Dismiss",
                                style = MaterialTheme.typography.labelMedium,
                                color = ObsidianTheme.textMuted,
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { /* Clear error state */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Greeting
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GreetingHeader(name : String, onSignOut: () -> Unit) {

    var showConfirmationDialog by remember { mutableStateOf(false)}

    Row(
        modifier              = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                "Good morning,",
                style = MaterialTheme.typography.bodySmall,
                color = ObsidianTheme.textSecondary
            )

            Text(
                text = name.ifEmpty { "..." },
                style         = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color         = ObsidianTheme.textPrimary,
                letterSpacing = 0.3.sp
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)

        ){
            Box(
                modifier = Modifier.size(40.dp)
                    .clip(CircleShape)
                    .background(ObsidianTheme.surfaceElevated)
                    .border(0.8.dp, ObsidianTheme.surfaceBorder,CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember {MutableInteractionSource()}
                    ){
                        showConfirmationDialog = true
                    },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Logout,
                    contentDescription = "Sign out",
                    tint = ObsidianTheme.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(ObsidianTheme.accent, Color(0xFFB06CF6))))
                    .border(1.5.dp, ObsidianTheme.accent.copy(0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(name.firstOrNull()?.uppercase() ?: "?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    //Confirmation Dialog - Avoids accidental sign outs
    if(showConfirmationDialog){
        AlertDialog(
            onDismissRequest = {showConfirmationDialog = false},
            containerColor = ObsidianTheme.surfaceElevated,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Sign Out?",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ObsidianTheme.textPrimary
                    )
            },
            text = {
                Text("You'll need to sign in again to use this app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ObsidianTheme.textSecondary)
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianTheme.red.copy(alpha = 0.15f))
                        .border(0.5.dp, ObsidianTheme.red.copy(0.4f),RoundedCornerShape(12.dp))
                        .clickable(
                            indication = null,
                            interactionSource = remember {MutableInteractionSource()}
                        ){
                            showConfirmationDialog = false
                            onSignOut()
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        ){
                    Text("Sign out",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ObsidianTheme.red)
                }

            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianTheme.surfaceElevated)
                        .border(0.5.dp, ObsidianTheme.surfaceBorder,RoundedCornerShape(12.dp))
                        .clickable(
                            indication = null,
                            interactionSource = remember {MutableInteractionSource()})
                        {   showConfirmationDialog = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp)){
                    Text(
                        "Cancel",
                        style = MaterialTheme.typography.labelMedium,
                        color = ObsidianTheme.textSecondary
                    )
                }




            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Search bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ObsidianSearchBar(query: String, onQueryChange: (String) -> Unit, onFilterClick: () -> Unit) {

    var isFilter by remember {mutableStateOf(false)}

    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(ObsidianTheme.surfaceElevated)
            .border(
                0.8.dp,
                Brush.linearGradient(
                    listOf(ObsidianTheme.surfaceBorder.copy(0.9f), ObsidianTheme.surfaceBorder.copy(0.3f))
                ),
                shape
            )
    ) {
        Row(
            modifier          = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Search, null, tint = ObsidianTheme.textMuted, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                text     = if (query.isEmpty()) "Search items, brands…" else query,
                color    = if (query.isEmpty()) ObsidianTheme.textMuted else ObsidianTheme.textPrimary,
                style    = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianTheme.accentSoft)
                    .border(0.5.dp, ObsidianTheme.accent.copy(0.3f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ){
                        onFilterClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Tune, "Filter", tint = ObsidianTheme.accent, modifier = Modifier.size(17.dp))
            }

        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Promotions pager
// ─────────────────────────────────────────────────────────────────────────────

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PromotionsPager(items: List<PromoItem>) {
    val pagerState = rememberPagerState(pageCount = { items.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % items.size)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        HorizontalPager(
            state          = pagerState,
            contentPadding = PaddingValues(end = 28.dp),
            pageSpacing    = 12.dp,
            modifier       = Modifier.fillMaxWidth()
        ) { page ->
            val scale = 1f - 0.05f * abs(pagerState.currentPageOffsetFraction)
            PromoCard(
                items[page],
                Modifier.graphicsLayer { scaleX = scale; scaleY = scale }.fillMaxWidth().height(175.dp)
            )
        }
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            repeat(items.size) { i ->
                val sel = pagerState.currentPage == i
                val w by animateDpAsState(if (sel) 22.dp else 5.dp, spring(stiffness = Spring.StiffnessMediumLow), label = "")
                Box(
                    Modifier.padding(horizontal = 3.dp).height(5.dp).width(w)
                        .clip(CircleShape)
                        .background(if (sel) ObsidianTheme.accent else ObsidianTheme.surfaceBorder)
                )
            }
        }
    }
}

@Composable
private fun PromoCard(item: PromoItem, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(26.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Brush.linearGradient(item.gradientColors))
            .border(0.8.dp, Brush.linearGradient(listOf(item.accentColor.copy(0.35f), Color.Transparent)), shape)
    ) {
        Box(Modifier.size(120.dp).offset(x = 190.dp, y = (-25).dp).clip(CircleShape).background(item.accentColor.copy(0.22f)))
        Box(Modifier.size(70.dp).offset(x = 225.dp, y = 80.dp).clip(CircleShape).background(item.accentColor.copy(0.12f)))
        Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                Modifier.clip(RoundedCornerShape(8.dp))
                    .background(item.accentColor.copy(0.18f))
                    .border(0.5.dp, item.accentColor.copy(0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(item.badge, color = item.accentColor,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.8.sp, fontSize = 9.sp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(item.title,    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = ObsidianTheme.textPrimary)
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = ObsidianTheme.textSecondary)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Row(Modifier.fillMaxWidth().padding(top = 4.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ObsidianTheme.textPrimary)
        Text("See all", style = MaterialTheme.typography.labelMedium, color = ObsidianTheme.accent,
            modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {})
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//Category chips — horizontal scroll Row, single line, zero wrapping
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CategoryChipsRow(categories: List<Category>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            // horizontalScroll keeps the row on ONE line — fixes the "Watches" wrap bug
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEachIndexed { index, cat ->
            val isSelected = index == selectedIndex
            val bgColor by animateColorAsState(
                if (isSelected) ObsidianTheme.accent else ObsidianTheme.surfaceElevated, tween(180), label = "")
            val textColor by animateColorAsState(
                if (isSelected) Color.White else ObsidianTheme.textSecondary, tween(180), label = "")

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(bgColor)
                    .border(
                        0.8.dp,
                        if (isSelected) ObsidianTheme.accent.copy(0.5f) else ObsidianTheme.surfaceBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onSelect(index) }
                    .padding(horizontal = 16.dp, vertical = 9.dp)
            ) {
                Text(
                    "${cat.emoji}  ${cat.label}",
                    style    = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color    = textColor,
                    maxLines = 1     // safety guard — never allow a chip to wrap
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Product card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ProductCard(product: Product, index: Int, onClick : () -> Unit,    favoritesViewModel: FavoritesViewModel = hiltViewModel()) {
    val favorites by favoritesViewModel.favorites.collectAsStateWithLifecycle()

    val isFav = favorites.any{it.id == product.id}
    val cardHeight = when (index % 3) { 0 -> 245.dp; 1 -> 205.dp; else -> 225.dp }
    val shape      = RoundedCornerShape(22.dp)

    // ─────────────────────────────────────────────────────────────────────
    // Color palettes — UI concern, not data
    // ─────────────────────────────────────────────────────────────────────
    val cardGradients = listOf(
        listOf(Color(0xFF1A1820), Color(0xFF141218)),  // Deep charcoal
        listOf(Color(0xFF0F1A28), Color(0xFF0A1018)),  // Navy blue
        listOf(Color(0xFF1C1820), Color(0xFF141018)),  // Purple-tinted black
        listOf(Color(0xFF1A1428), Color(0xFF110E1A)),  // Violet dark
        listOf(Color(0xFF0E1620), Color(0xFF080E14)),  // Steel blue
        listOf(Color(0xFF201818), Color(0xFF140E0E)),  // Warm brown-black
        listOf(Color(0xFF101A10), Color(0xFF0A100A)),  // Forest green dark
        listOf(Color(0xFF1E1210), Color(0xFF140C0A)),  // Rust brown
        listOf(Color(0xFF141A10), Color(0xFF0C100A)),  // Olive green dark
        listOf(Color(0xFF1A1230), Color(0xFF100A1E)),  // Deep purple
        listOf(Color(0xFF0A1830), Color(0xFF060E1C)),  // Ocean blue
        listOf(Color(0xFF101828), Color(0xFF0A1018))   // Midnight blue
    )

    val accentColors = listOf(
        ObsidianTheme.accent,      // Purple
        ObsidianTheme.gold,        // Gold
        ObsidianTheme.green,       // Green
        Color(0xFF5BA3D0),         // Sky blue
        ObsidianTheme.red,         // Red
        Color(0xFFE8C05C),         // Warm yellow
        Color(0xFF4ECCA3),         // Mint
        Color(0xFFFF6B9D),         // Pink
    )

    // Select colors based on index — consistent per position
    val cardGradient = cardGradients[index % cardGradients.size]
    val accentColor = accentColors[index % accentColors.size]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clip(shape)
            .border(0.8.dp, Brush.linearGradient(cardGradient), shape)
            .clickable{onClick()}
    ) {
        AsyncImage(model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
            )

        Box(Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color.Transparent, cardGradient[0].copy(0.6f),cardGradient[1]))))
        Column(Modifier.fillMaxSize().padding(13.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                when {
                    product.isNew          -> TagBadge(text = "NEW",           color = accentColor)
                    product.discount != null -> TagBadge(text = product.discount, color = ObsidianTheme.red)
                    else                   -> Spacer(Modifier.size(4.dp))
                }
                val favScale by animateFloatAsState(if (isFav) 1.25f else 1f, spring(stiffness = Spring.StiffnessHigh), label = "")
                Box(
                    modifier = Modifier.size(30.dp).clip(CircleShape)
                        .background(ObsidianTheme.surfaceElevated.copy(0.8f))
                        .border(0.5.dp, ObsidianTheme.surfaceBorder, CircleShape)
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() })
                        {
                            favoritesViewModel.toggleFavorite(product) //toggles favorite
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isFav) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                        null,
                        tint = if (isFav) ObsidianTheme.red else ObsidianTheme.textSecondary,
                        modifier = Modifier.size(15.dp).scale(favScale)
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(product.brand,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp, fontSize = 8.5.sp, fontWeight = FontWeight.Bold),
                    color = accentColor.copy(0.85f))
                Text(product.name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = ObsidianTheme.textPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                )
                {
                    Column {
                        Text(product.price,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = ObsidianTheme.textPrimary)

                        product.originalPrice?.let {
                            Text(it, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ObsidianTheme.textMuted)
                        }
                    }
                    Row(
                        Modifier.clip(RoundedCornerShape(10.dp))
                            .background(ObsidianTheme.surfaceElevated)
                            .border(0.5.dp, ObsidianTheme.surfaceBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(Icons.Rounded.Star, null, tint = ObsidianTheme.gold, modifier = Modifier.size(11.dp))
                        Text(product.rating.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = ObsidianTheme.textPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun TagBadge(text: String, color: Color) {
    Box(
        Modifier.clip(RoundedCornerShape(8.dp))
            .background(color.copy(0.18f))
            .border(0.5.dp, color.copy(0.45f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.8.sp, fontSize = 8.5.sp), color = color)
    }
}






