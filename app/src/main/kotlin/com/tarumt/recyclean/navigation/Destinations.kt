package com.tarumt.recyclean.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.ui.graphics.vector.ImageVector

data object LoginPageDestination : NavDestination()
data object HomePageDestination : NavDestination()

enum class Navigations(val icons: ImageVector) {
    Home(Icons.Default.Home),
    Serve(Icons.Default.RateReview),
    AddSell(Icons.Default.AddCircle),
    Favourite(Icons.Default.FolderSpecial),
    Profile(Icons.Default.Person)
}