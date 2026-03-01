package com.example.zentrix.features

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zentrix.core.designsystem.obsidianGlassStyle
import com.example.zentrix.domain.model.Screen
import com.example.zentrix.features.cart.CartViewModel
import com.example.zentrix.features.favorites.FavoritesViewModel
import com.example.zentrix.ui.theme.ObsidianTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun MainScaffold(windowSize: WindowWidthSizeClass,
                 currentScreen: Screen,
                 onNavigate: (Screen) -> Unit,
                 content: @Composable (PaddingValues, HazeState) -> Unit)
{
    val hazeState  = rememberHazeState()
    val isExpanded = windowSize == WindowWidthSizeClass.Expanded

    val paddingValues = if(!isExpanded) 114.dp else 0.dp

    val selectedNavIndex = when(currentScreen){
        is Screen.Home -> 0
        is Screen.Cart -> 1
        is Screen.Favorites -> 2
        is Screen.Profile -> 3
        else -> 0
    }

    Box(Modifier.fillMaxSize().background(ObsidianTheme.background)) {
        Row(Modifier.fillMaxSize()) {
            if (isExpanded)
                AdaptiveNavigationRail(hazeState,
                selectedIndex = selectedNavIndex,onNavigate = {index->
                        run {
                            when (index) {
                                0 -> onNavigate(Screen.Home)
                                1 -> onNavigate(Screen.Cart)
                                2 -> onNavigate(Screen.Favorites)
                                3 -> onNavigate(Screen.Profile)
                            }
                        }
                }
                )

            Box(Modifier.weight(1f)) { content(PaddingValues(bottom = paddingValues), hazeState) }
        }
        if (!isExpanded) {
            FloatingGlassNavBar(hazeState = hazeState, modifier = Modifier.align(Alignment.BottomCenter),
                selectedIndex = selectedNavIndex,
                onNavigate = {index->
                    when(index){
                        0 -> onNavigate(Screen.Home)
                        1 -> onNavigate(Screen.Cart)
                        2 -> onNavigate(Screen.Favorites)
                        3 -> onNavigate(Screen.Profile)
                    }
                }
                )
        }
    }
}
private data class NavDestination(
    val label: String, val filledIcon: ImageVector, val outlineIcon: ImageVector
)

@Composable
fun FloatingGlassNavBar(hazeState: HazeState,
                        modifier: Modifier = Modifier,
                        selectedIndex:Int,
                        onNavigate:(Int)->Unit,
                        cartViewModel: CartViewModel = hiltViewModel(),
                        favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val cartItem by cartViewModel.cartItems.collectAsStateWithLifecycle()
    val hasNewState by cartViewModel.hasNewState.collectAsStateWithLifecycle()
    val hasNewFavorite by favoritesViewModel.hasNewFavorite.collectAsStateWithLifecycle()

    val destinations = remember {
        listOf(
            NavDestination("Home",    Icons.Filled.Home,         Icons.Outlined.Home),
            NavDestination("Cart",    Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCartCheckout),
            NavDestination("Saved",   Icons.Filled.Favorite,     Icons.Outlined.FavoriteBorder),
            NavDestination("Profile", Icons.Filled.Person,       Icons.Outlined.Person)
        )
    }

    val navShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 50.dp)
            .fillMaxWidth().height(64.dp)
            .graphicsLayer{shadowElevation = 50f} // there will be no system shadow
    ){
        Box(
            modifier = Modifier.fillMaxSize().clip(navShape).background(
                Brush.radialGradient(
                    colors = listOf(ObsidianTheme.accent.copy(alpha = 0.3f), Color.Transparent),
                    radius = 400f)
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
                        isSelected  = selectedIndex == index,
                        onClick     = { onNavigate(index) },
                        badge = when(index){
                            1 -> {
                                if(hasNewState && cartItem.isNotEmpty()){
                                    BadgeType.Count(cartItem.sumOf { it.quantity })
                                } else {
                                    null
                                }
                            }
                            2 ->{
                                if(hasNewFavorite){
                                    BadgeType.Dot
                                } else {
                                    null
                                }
                            }
                            else -> null
                        }
                    )
                }
            }
        }
    }

}

sealed class BadgeType{
    data class Count(val count : Int) : BadgeType()
    data object Dot : BadgeType()
}



@Composable
private fun NavBarItem(destination: NavDestination, isSelected: Boolean, onClick: () -> Unit , badge : BadgeType ?= null) {
    val iconColor  by animateColorAsState(if (isSelected) ObsidianTheme.accent else ObsidianTheme.textSecondary, tween(200), label = "")
    val labelColor by animateColorAsState(if (isSelected) ObsidianTheme.accent else ObsidianTheme.textSecondary, tween(200), label = "")
    val iconScale  by animateFloatAsState(if (isSelected) 1.12f else 1f, spring(stiffness = Spring.StiffnessMedium), label = "")

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd){ //Position of badge

            Icon(
                imageVector        = if (isSelected) destination.filledIcon else destination.outlineIcon,
                contentDescription = destination.label,
                tint               = iconColor,
                modifier           = Modifier.size(24.dp).scale(iconScale)
            )
            //Badge
            badge?.let {
                when(it){
                    is BadgeType.Count -> {
                        //Count Badge for cart
                        Box(
                            modifier = Modifier
                                .offset(x = 6.dp, y = (-4).dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(ObsidianTheme.red)
                                .border(1.5.dp, ObsidianTheme.background, CircleShape),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = if(it.count > 9) "9+" else it.count.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.ExtraBold),
                                color = Color.White
                            )
                        }
                    }
                    is BadgeType.Dot ->{
                        //Dot badge for favorite
                        Box(
                            modifier = Modifier
                                .offset(x = 6.dp, y = (-2).dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ObsidianTheme.red)
                                .border(1.5.dp, ObsidianTheme.background, CircleShape)
                        )
                    }
                }
            }
        }
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
// ─────────────────────────────────────────────────────────────────────────────
// Navigation rail (tablet)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AdaptiveNavigationRail(
    hazeState: HazeState,
    selectedIndex: Int,
    onNavigate: (Int) -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    val hasNewState by cartViewModel.hasNewState.collectAsStateWithLifecycle()
    val hasNewFavorite by favoritesViewModel.hasNewFavorite.collectAsStateWithLifecycle()

    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    tint = HazeTint(ObsidianTheme.surfaceElevated.copy(alpha = 0.18f)),
                    blurRadius = 24.dp
                )
            )
            .background(Color.Transparent),
        containerColor = Color.Transparent,
        header = {
            Icon(
                Icons.Rounded.ShoppingBag,
                contentDescription = null,
                tint = ObsidianTheme.accent
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            NavigationRailItem(
                selected = selectedIndex == 0,
                onClick = { onNavigate(0) },
                icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                label = { Text("Home") }
            )

            // Cart with badge
            NavigationRailItem(
                selected = selectedIndex == 1,
                onClick = { onNavigate(1) },
                icon = {
                    Box {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                        if (hasNewState && cartItems.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-4).dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(ObsidianTheme.red)
                                    .border(1.5.dp, ObsidianTheme.background, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (cartItems.sumOf { it.quantity } > 9) "9+" else cartItems.sumOf { it.quantity }.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                },
                label = { Text("Cart") }
            )

            // Favorites with badge
            NavigationRailItem(
                selected = selectedIndex == 2,
                onClick = { onNavigate(2) },
                icon = {
                    Box {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                        if (hasNewFavorite) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-2).dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ObsidianTheme.red)
                                    .border(1.5.dp, ObsidianTheme.background, CircleShape)
                            )
                        }
                    }
                },
                label = { Text("Saved") }
            )

            NavigationRailItem(
                selected = selectedIndex == 3,
                onClick = { onNavigate(3) },
                icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                label = { Text("Profile") }
            )
        }
    }
}
