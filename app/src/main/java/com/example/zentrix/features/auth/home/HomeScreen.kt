package com.example.zentrix.features.auth.home

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.navigation.Navigation
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun HomeScreen(hazeState : HazeState) {
    LazyVerticalStaggeredGrid(
      columns = StaggeredGridCells.Fixed(2),
      modifier = Modifier.fillMaxSize()
        .hazeSource(state = hazeState),
      contentPadding = PaddingValues(16.dp,16.dp,16.dp,100.dp), //Extra bottom padding for the bar
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalItemSpacing = 16.dp
    )  {
      item(span = StaggeredGridItemSpan.FullLine){
        Text(
          "New Arrivals",
          style = MaterialTheme.typography.displaySmall,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
      items(20){index ->
        ProductCard(index)
      }
    }
}

@Composable
fun ProductCard(index: Int) {
  val color = if(index % 2== 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
  Box(modifier = Modifier.fillMaxWidth().height(if(index % 3 == 0) 250.dp else 180.dp).clip(
    RoundedCornerShape(24.dp)).background(color))
}


@Composable
fun MainScaffold(
  windowSize : WindowWidthSizeClass,
  content : @Composable (PaddingValues, HazeState) -> Unit
){
  val hazeState = remember { HazeState() }
  val isExpanded = windowSize == WindowWidthSizeClass.Expanded
  Row(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
    if(isExpanded){
      AdaptiveNavigationRail(hazeState)
    }

    Box(modifier = Modifier.weight(1f)){
      //2. The main content layer ( the source of the blur)
      content(PaddingValues(),hazeState)
    }
    //Floating glass bottom bar
    if(!isExpanded){
        Box(modifier = Modifier.fillMaxSize()){
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 24.dp, vertical = 32.dp) //Floating effect
            ){
                FloatingGlassNavBar(hazeState)
            }
        }
    }
  }
}

//for tablets or ipad e.g. big screen
@Composable
fun AdaptiveNavigationRail(hazeState: HazeState) {
  NavigationRail(
    modifier = Modifier
      .fillMaxHeight()
      .hazeEffect(
        state = hazeState,
        style = HazeStyle(
          tint = HazeTint(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)),
          blurRadius = 20.dp
        )
      ).background(Color.Transparent),
    containerColor = Color.Transparent,
    header = {
      Icon(Icons.Rounded.ShoppingBag, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
  ) {
    Column(
      modifier = Modifier.fillMaxHeight(),
      verticalArrangement = Arrangement.Center
    ){
      NavigationRailItem(
        selected = true,
        onClick = {},
        icon = {Icon(Icons.Rounded.Home, contentDescription = null)},
        label = {Text("Home")}
      )
      NavigationRailItem(
        selected = false,
        onClick = {},
        icon = {Icon(Icons.Rounded.ShoppingCart, contentDescription = null)},
        label = {Text("Cart")}
      )
      NavigationRailItem(
        selected = false,
        onClick = {},
        icon = {Icon(Icons.Rounded.Person, contentDescription = null)},
        label = {Text("Profile")}
      )
    }
  }
}

//Floating Navbar design
@Composable
fun FloatingGlassNavBar(hazeState : HazeState) {
    val shape = RoundedCornerShape(32.dp)

  Surface (
    modifier = Modifier
      .fillMaxWidth().height(72.dp)
      .hazeEffect(
        state = hazeState,
        style = HazeStyle(
          tint = HazeTint(MaterialTheme.colorScheme.surface.copy(0.2f)),
          blurRadius = 20.dp,
          noiseFactor = 0.1f
        )
      )
      .border(
        width = 0.5.dp,
        brush = Brush.verticalGradient(colors = listOf(Color.White.copy(0.3f),Color.Transparent)),
        shape = shape
      ),
      color = Color.Transparent,
      shape = shape,
    shadowElevation = 8.dp
  ){
    Row(

    ){
      NavIcon(Icons.Rounded.Home,"Home",isSelected = true)
      NavIcon(Icons.Rounded.Favorite,"Favourite")
      NavIcon(Icons.Rounded.ShoppingCart,"Cart")
      NavIcon(Icons.Rounded.Person,"Profile")
    }
  }
}

//the  bottom navbar icons
@Composable
fun NavIcon( icon: ImageVector, label: String,isSelected : Boolean = false) {
    val color = if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
  Column (horizontalAlignment = Alignment.CenterHorizontally){
    Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
  }
}

