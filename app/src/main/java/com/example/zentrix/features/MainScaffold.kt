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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentrix.core.designsystem.obsidianGlassStyle
import com.example.zentrix.domain.model.Screen
import com.example.zentrix.ui.theme.ObsidianTheme
import dev.chrisbanes.haze.HazeState
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
            if (isExpanded) AdaptiveNavigationRail(hazeState)

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
fun FloatingGlassNavBar(hazeState: HazeState, modifier: Modifier = Modifier,
                        selectedIndex:Int,onNavigate:(Int)->Unit) {

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
            .graphicsLayer{shadowElevation = 30f} // there will be no system shadow
    ){
        Box(
            modifier = Modifier.fillMaxSize().clip(navShape).background(
                Brush.radialGradient(
                    colors = listOf(ObsidianTheme.accent.copy(alpha = 0.7f), Color.Transparent),
                    radius = 300f)
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
                        onClick     = { onNavigate(index) }
                    )
                }
            }
        }
    }

}

@Composable
private fun NavBarItem(destination: NavDestination, isSelected: Boolean, onClick: () -> Unit) {
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
