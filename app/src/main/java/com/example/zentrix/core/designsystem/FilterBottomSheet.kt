package com.example.zentrix.core.designsystem

import android.view.RoundedCorner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentrix.domain.model.FilterState
import com.example.zentrix.domain.model.SortOption
import com.example.zentrix.ui.theme.ObsidianTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    currentFilter: FilterState,
    onApplyFilter: (FilterState) -> Unit,
    onClearFilter: () -> Unit,
    onDismissRequest: () -> Unit
){
    var minPrice by remember { mutableStateOf(currentFilter.minPrice?.toString() ?: "") }
    var maxPrice by remember { mutableStateOf(currentFilter.maxPrice?.toString() ?: "") }
    var minRating by remember { mutableFloatStateOf(currentFilter.minRating ?: 0f) }
    var selectedCategories by remember { mutableStateOf(currentFilter.categories) }
    var showNewOnly by remember { mutableStateOf(currentFilter.showNewOnly) }
    var showDiscountedOnly by remember { mutableStateOf(currentFilter.showDiscountedOnly) }
    var sortBy by remember { mutableStateOf(currentFilter.sortBy) }

    val categories = listOf("Tops","Shoes","Bags","Watches","Jewellery","Denim","Headphone","Laptop","Controller")



    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        containerColor = ObsidianTheme.background,
        dragHandle = {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ObsidianTheme.surfaceBorder)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            //Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                  text = "Filter Products",
                  style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                  color = ObsidianTheme.textPrimary
              )
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = ObsidianTheme.textSecondary
                    )
                }
            }
            //Minimum Range and Maximum Range

            FilterSection(title = "Price Range"){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    FilterTextField(value = minPrice, onValueChange = {minPrice = it}, label = "Min (£)", modifier = Modifier.weight(1f))
                    FilterTextField(value = maxPrice, onValueChange = {maxPrice = it}, label = "Max (£)", modifier = Modifier.weight(1f))

                }
            }
            //Rating Section
            FilterSection(title = "Minimum Rating"){
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
                    Slider(
                        value = minRating,
                        onValueChange = {minRating = it},
                        valueRange = 0f..5f,
                        steps = 4,
                        colors= SliderDefaults.colors(
                            thumbColor = ObsidianTheme.accent,
                            activeTrackColor = ObsidianTheme.accent,
                            inactiveTrackColor = ObsidianTheme.surfaceBorder
                        )
                    )
                    Text(
                        text = if(minRating > 0f) "${minRating.toInt()} stars & above"  else "All ratings",
                        style = MaterialTheme.typography.bodySmall,
                        color = ObsidianTheme.textSecondary
                    )
                }
            }

            //Categories Filter
            FilterSection(title = "Categories"){
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
                    categories.chunked(3).forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            rowCategories.forEach { category ->
                                FilterChip(
                                    label = category,
                                    isSelected = category in selectedCategories,
                                    onClick = {  },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                                    }
                            repeat(3 - rowCategories.size){
                                Spacer(modifier = Modifier.weight(1f))
                            }
                    }
                }
            }
            // Quick Filters
            FilterSection(title = "Quick Filters") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterCheckbox(
                        label = "New Arrivals Only",
                        checked = showNewOnly,
                        onCheckedChange = { showNewOnly = it }
                    )
                    FilterCheckbox(
                        label = "Discounted Items Only",
                        checked = showDiscountedOnly,
                        onCheckedChange = { showDiscountedOnly = it }
                    )
                }
            }

            // Sort By
            FilterSection(title = "Sort By") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortOption.entries.forEach { option ->
                        if (option != SortOption.NONE) {
                            SortOptionItem(
                                label = when (option) {
                                    SortOption.PRICE_LOW_TO_HIGH -> "Price: Low to High"
                                    SortOption.PRICE_HIGH_TO_LOW -> "Price: High to Low"
                                    SortOption.RATING_HIGH_TO_LOW -> "Rating: High to Low"
                                    SortOption.NAME_A_TO_Z -> "Name: A to Z"
                                    else -> ""
                                },
                                isSelected = sortBy == option,
                                onClick = { sortBy = option }
                            )
                        }
                    }
                }
            }
            //Action BUttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){
                //Clear Button
                Box(modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ObsidianTheme.surfaceElevated)
                    .border(0.8.dp,ObsidianTheme.surfaceBorder, RoundedCornerShape(16.dp))
                    .clickable{
                        minPrice = ""
                        maxPrice = ""
                        minRating = 0f
                        selectedCategories = emptySet()
                        showNewOnly = false
                        showDiscountedOnly = false
                        sortBy = SortOption.NONE
                        onClearFilter()
                        onDismissRequest()
                    },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = ObsidianTheme.textSecondary
                    )
                }
                //Apply Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianTheme.accent)
                        .clickable{
                            val filter = FilterState(
                                minPrice = minPrice.toFloatOrNull(),
                                maxPrice = maxPrice.toFloatOrNull(),
                                minRating = if(minRating > 0f) minRating else null,
                                categories = selectedCategories,
                                showNewOnly = showNewOnly,
                                showDiscountedOnly = showDiscountedOnly,
                                sortBy = sortBy
                            )
                            onApplyFilter(filter)
                            onDismissRequest()
                        }, contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Apply Filters",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White)

                }
            }

        }
    }
}

@Composable
fun SortOptionItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if(isSelected) ObsidianTheme.accent else ObsidianTheme.surfaceElevated)
                .border(
                    0.8.dp,
                    if(isSelected) ObsidianTheme.accent else ObsidianTheme.surfaceBorder
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ){ onClick() }
                .padding(horizontal = 16.dp , vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            if(isSelected){
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(ObsidianTheme.accent),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)

                    )
                }
            }
        }
}

@Composable
fun FilterCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ObsidianTheme.surfaceElevated)
            .border(0.5.dp, ObsidianTheme.surfaceBorder , RoundedCornerShape(12.dp))
            .clickable {onCheckedChange(!checked)}
            .padding(horizontal = 16.dp , vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label, style = MaterialTheme.typography.bodyMedium, color = ObsidianTheme.textPrimary
        )

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if(checked) ObsidianTheme.accent else ObsidianTheme.surfaceElevated)
                .border(
                    1.dp , if(checked) ObsidianTheme.accent else ObsidianTheme.surfaceBorder
                ),
            contentAlignment = Alignment.Center
        ){
            if(checked){
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = "Checked",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun FilterTextField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = {Text(label, style = MaterialTheme.typography.bodySmall)},
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ObsidianTheme.surfaceElevated,
                unfocusedContainerColor = ObsidianTheme.surfaceElevated,
                focusedIndicatorColor = ObsidianTheme.accent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = ObsidianTheme.textPrimary,
                unfocusedTextColor = ObsidianTheme.textPrimary,
                cursorColor = ObsidianTheme.accent
            ),singleLine = true
        )
}


@Composable
fun FilterSection(title: String, content: @Composable () -> Unit) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp) ){
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = ObsidianTheme.textPrimary
            )
            content()
        }
}

@Composable
fun FilterChip(label: String,isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if(isSelected) ObsidianTheme.accent.copy(0.15f) else ObsidianTheme.surfaceElevated)
            .border(0.8.dp , if( isSelected) ObsidianTheme.accent else ObsidianTheme.surfaceBorder , RoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ){onClick()},
        contentAlignment = Alignment.Center
    ){
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if(isSelected) ObsidianTheme.accent else ObsidianTheme.textSecondary,
            fontSize = 12.sp
        )
    }
}