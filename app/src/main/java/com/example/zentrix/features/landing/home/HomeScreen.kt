package com.example.zentrix.features.landing.home

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    Category("All", "✦"), Category("Tops", "👕"), Category("Shoes", "👟"),
    Category("Bags", "👜"), Category("Watches", "⌚"), Category("Jewellery", "💍"),
    Category("Denim", "🧥")
)

private val products = listOf(
    Product(
        "Linen Overshirt", "ARKET", "£89", "£120", 4.7f,
        listOf(Color(0xFF1A1820), Color(0xFF141218)), ObsidianTheme.accent
    ),
    Product("Air Foam Runner", "ADIDAS", "£130", null, 4.5f,
        listOf(Color(0xFF0F1A28), Color(0xFF0A1018)), ObsidianTheme.gold, isNew = true),
    Product("Merino Rollneck", "COS", "£95", "£145", 4.8f,
        listOf(Color(0xFF1C1820), Color(0xFF141018)), ObsidianTheme.accent, discount = "-35%"),
    Product("Mini Crossbody", "& OTHER STORIES", "£65", null, 4.3f,
        listOf(Color(0xFF1A1428), Color(0xFF110E1A)), ObsidianTheme.accent, isNew = true),
    Product("Slim Chino", "UNIQLO", "£49", "£69", 4.6f,
        listOf(Color(0xFF0E1620), Color(0xFF080E14)), Color(0xFF5BA3D0), discount = "-29%"),
    Product("Ceramic Watch", "MVMT", "£210", null, 4.9f,
        listOf(Color(0xFF201818), Color(0xFF140E0E)), ObsidianTheme.gold),
    Product("Canvas Tote", "NORSE PROJECTS", "£55", null, 4.4f,
        listOf(Color(0xFF101A10), Color(0xFF0A100A)), ObsidianTheme.green, isNew = true),
    Product("Waffle Hoodie", "REIGNING CHAMP", "£160", "£200", 4.7f,
        listOf(Color(0xFF1E1210), Color(0xFF140C0A)), ObsidianTheme.red, discount = "-20%"),
    Product("Slip-On Loafer", "G.H.BASS", "£115", null, 4.5f,
        listOf(Color(0xFF141A10), Color(0xFF0C100A)), ObsidianTheme.green),
    Product("Silk Scarf", "TOTEME", "£120", "£160", 4.8f,
        listOf(Color(0xFF1A1230), Color(0xFF100A1E)), ObsidianTheme.accent, discount = "-25%"),
    Product("Puffer Vest", "PATAGONIA", "£175", null, 4.9f,
        listOf(Color(0xFF0A1830), Color(0xFF060E1C)), Color(0xFF5BA3D0), isNew = true),
    Product("Raw Denim", "NUDIE JEANS", "£195", "£230", 4.6f,
        listOf(Color(0xFF101828), Color(0xFF0A1018)), Color(0xFF5BA3D0), discount = "-15%")
)


// ─────────────────────────────────────────────────────────────────────────────
// HomeScreen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(hazeState: HazeState , viewModel: HomeViewModel= hiltViewModel() , onSignOut : () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCat by remember { mutableIntStateOf(0) }
    val name by viewModel.userName.collectAsStateWithLifecycle()
    LazyVerticalStaggeredGrid(
        columns       = StaggeredGridCells.Fixed(2),
        modifier      = Modifier
            .fillMaxSize()
            .background(ObsidianTheme.background)
            .hazeSource(state = hazeState),          // <-- content behind navbar is blurred
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, top = 16.dp, bottom = 110.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing   = 12.dp
    ) {
        item(span = StaggeredGridItemSpan.FullLine) { GreetingHeader(name = name, onSignOut = onSignOut) }
        item(span = StaggeredGridItemSpan.FullLine) {
            ObsidianSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
        }
        item(span = StaggeredGridItemSpan.FullLine) { PromotionsPager(promoItems) }
        item(span = StaggeredGridItemSpan.FullLine) { SectionHeader("Categories") }

        // Single-line horizontal scroll row — eliminates the gap completely
        item(span = StaggeredGridItemSpan.FullLine) {
            CategoryChipsRow(
                categories    = categories,
                selectedIndex = selectedCat,
                onSelect      = { selectedCat = it }
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) { SectionHeader("New Arrivals") }
        items(products.size) { index -> ProductCard(product = products[index], index = index) }
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
private fun ObsidianSearchBar(query: String, onQueryChange: (String) -> Unit) {
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
                    .padding(horizontal = 10.dp, vertical = 7.dp),
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
        Text("See all →", style = MaterialTheme.typography.labelMedium, color = ObsidianTheme.accent,
            modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {})
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ✅ Category chips — horizontal scroll Row, single line, zero wrapping
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
fun ProductCard(product: Product, index: Int) {
    var isFav by remember { mutableStateOf(product.isFavorite) }
    val cardHeight = when (index % 3) { 0 -> 245.dp; 1 -> 205.dp; else -> 225.dp }
    val shape      = RoundedCornerShape(22.dp)

    Box(
        modifier = Modifier.fillMaxWidth().height(cardHeight).clip(shape)
            .background(Brush.linearGradient(product.cardGradient))
            .border(0.8.dp, Brush.linearGradient(listOf(ObsidianTheme.surfaceBorder.copy(0.7f), Color.Transparent)), shape)
    ) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, product.accentColor.copy(0.07f)))))
        Column(Modifier.fillMaxSize().padding(13.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                when {
                    product.isNew          -> TagBadge(text = "NEW",           color = product.accentColor)
                    product.discount != null -> TagBadge(text = product.discount, color = ObsidianTheme.red)
                    else                   -> Spacer(Modifier.size(4.dp))
                }
                val favScale by animateFloatAsState(if (isFav) 1.25f else 1f, spring(stiffness = Spring.StiffnessHigh), label = "")
                Box(
                    modifier = Modifier.size(30.dp).clip(CircleShape)
                        .background(ObsidianTheme.surfaceElevated.copy(0.8f))
                        .border(0.5.dp, ObsidianTheme.surfaceBorder, CircleShape)
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { isFav = !isFav },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isFav) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                        null, tint = if (isFav) ObsidianTheme.red else ObsidianTheme.textSecondary,
                        modifier = Modifier.size(15.dp).scale(favScale)
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(product.brand,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp, fontSize = 8.5.sp, fontWeight = FontWeight.Bold),
                    color = product.accentColor.copy(0.85f))
                Text(product.name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = ObsidianTheme.textPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
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

// ─────────────────────────────────────────────────────────────────────────────
// Main scaffold
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MainScaffold(windowSize: WindowWidthSizeClass, content: @Composable (PaddingValues, HazeState) -> Unit) {
    val hazeState  = rememberHazeState()
    val isExpanded = windowSize == WindowWidthSizeClass.Expanded

    Box(Modifier.fillMaxSize().background(ObsidianTheme.background)) {
        Row(Modifier.fillMaxSize()) {
            if (isExpanded) AdaptiveNavigationRail(hazeState)
            Box(Modifier.weight(1f)) { content(PaddingValues(), hazeState) }
        }
        if (!isExpanded) {
            FloatingGlassNavBar(hazeState = hazeState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Navigation rail (tablet)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AdaptiveNavigationRail(hazeState: HazeState) {
    NavigationRail(
        modifier       = Modifier.fillMaxHeight().hazeEffect(hazeState, obsidianGlassStyle()).background(Color.Transparent),
        containerColor = Color.Transparent,
        header = { Icon(Icons.Rounded.ShoppingBag, null, tint = ObsidianTheme.accent) }
    ) {
        Column(Modifier.fillMaxHeight(), Arrangement.Center) {
            NavigationRailItem(true,  {}, { Icon(Icons.Filled.Home,         null) }, label = { Text("Home") })
            NavigationRailItem(false, {}, { Icon(Icons.Filled.ShoppingCart,  null) }, label = { Text("Cart") })
            NavigationRailItem(false, {}, { Icon(Icons.Filled.Person,        null) }, label = { Text("Profile") })
        }
    }
}


private data class NavDestination(
    val label: String, val filledIcon: ImageVector, val outlineIcon: ImageVector
)

@Composable
fun FloatingGlassNavBar(hazeState: HazeState, modifier: Modifier = Modifier) {
    var selectedNav by remember { mutableIntStateOf(0) }

    val destinations = remember {
        listOf(
            NavDestination("Home",    Icons.Filled.Home,         Icons.Outlined.Home),
            NavDestination("Cart",    Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCartCheckout),
            NavDestination("Saved",   Icons.Filled.Favorite,     Icons.Outlined.FavoriteBorder),
            NavDestination("Profile", Icons.Filled.Person,       Icons.Outlined.Person)
        )
    }

    val navShape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            .fillMaxWidth().height(72.dp)
            .graphicsLayer{shadowElevation =0f} // there will be no system shadow
    ){
        Box(
            modifier = Modifier.fillMaxSize().clip(navShape).background(
                Brush.radialGradient(
                    colors = listOf(ObsidianTheme.accent.copy(alpha = 0.19f), Color.Transparent),
                    radius = 600f)
            )
        )
        Box(
           modifier = Modifier
               .fillMaxSize()
               .clip(navShape)
               .hazeEffect(state = hazeState, style = obsidianGlassStyle())
               .border(width = 0.8.dp,
                   brush = Brush.verticalGradient(listOf(Color.White.copy(0.14f),Color.White.copy(0.02f)))
                   , shape = navShape
               )


        ) {
            Row(
                modifier              = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                destinations.forEachIndexed { index, dest ->
                    NavBarItem(
                        destination = dest,
                        isSelected  = selectedNav == index,
                        onClick     = { selectedNav = index }
                    )
                }
            }
        }
    }

}

@Composable
private fun NavBarItem(destination: NavDestination, isSelected: Boolean, onClick: () -> Unit) {
    val iconColor  by animateColorAsState(if (isSelected) ObsidianTheme.accent else ObsidianTheme.textSecondary, tween(200), label = "")
    val labelColor by animateColorAsState(if (isSelected) ObsidianTheme.accent else ObsidianTheme.textMuted, tween(200), label = "")
    val iconScale  by animateFloatAsState(if (isSelected) 1.12f else 1f, spring(stiffness = Spring.StiffnessMedium), label = "")

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector        = if (isSelected) destination.filledIcon else destination.outlineIcon,
            contentDescription = destination.label,
            tint               = iconColor,
            modifier           = Modifier.size(24.dp).scale(iconScale)
        )
        Text(
            text      = destination.label,
            style     = MaterialTheme.typography.labelSmall.copy(
                fontSize   = 10.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color     = labelColor,
            textAlign = TextAlign.Center
        )
    }
}