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
data object AppointmentPageDestination : NavDestination()
data object AddSellPageDestination : NavDestination()
data object DataPageDestination : NavDestination()
data object ProfilePageDestination : NavDestination()

enum class Navigations(val icons: ImageVector, val navDestination: NavDestination) {
    Home(Icons.Default.Home, HomePageDestination),
    Meeting(Icons.Default.RateReview, AppointmentPageDestination),
    AddSell(Icons.Default.AddCircle, AddSellPageDestination),
    Data(Icons.Default.FolderSpecial, DataPageDestination),
    Profile(Icons.Default.Person, ProfilePageDestination)
}