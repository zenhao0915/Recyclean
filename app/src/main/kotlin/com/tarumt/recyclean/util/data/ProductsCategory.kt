package com.tarumt.recyclean.util.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Microwave
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Watch
import androidx.compose.ui.graphics.vector.ImageVector
import com.tarumt.recyclean.R

enum class ProductsCategory(val icons: ImageVector, val devices: Array<AltDevices>) {
    Mobile(
        Icons.Default.PhoneIphone,
        arrayOf(AltDevices.IPhone13Pro, AltDevices.GalaxyS22, AltDevices.BrokenIPhone11)
    ),
    Tablet(
        Icons.Default.Tablet,
        arrayOf(AltDevices.IPadPro, AltDevices.GalaxyTabS8)
    ),
    Laptop(
        Icons.Default.Laptop,
        arrayOf(AltDevices.MacBookPro, AltDevices.AsusROG, AltDevices.DellXPS)
    ),
    Watch(
        Icons.Default.Watch,
        arrayOf(AltDevices.AppleWatch7, AltDevices.GalaxyWatch4)
    ),
    Microwave(
        Icons.Default.Microwave,
        arrayOf(AltDevices.PanasonicInverter, AltDevices.ToshibaSolo)
    ),
    Speaker(
        Icons.Default.Speaker,
        arrayOf(AltDevices.JBLFlip5, AltDevices.SonySRS)
    ),
    Monitor(
        Icons.Default.Monitor,
        arrayOf(AltDevices.LGUltraGear, AltDevices.SamsungOdyssey)
    ),
    Camera(
        Icons.Default.CameraAlt,
        arrayOf(AltDevices.CanonR6, AltDevices.SonyA7)
    )
}

enum class AltDevices(val deviceName: String, val icon: Int = R.drawable.ic_launcher_foreground) {
    // --- Mobile ---
    IPhone13Pro("iPhone 13 Pro"),
    GalaxyS22("Samsung Galaxy S22"),
    BrokenIPhone11("Broken iPhone 11"),

    // --- Tablet ---
    IPadPro("iPad Pro 12.9"),
    GalaxyTabS8("Samsung Galaxy Tab S8"),

    // --- Laptop ---
    MacBookPro("MacBook Pro 2020"),
    AsusROG("ASUS ROG Zephyrus"),
    DellXPS("Dell XPS 13"),

    // --- Watch ---
    AppleWatch7("Apple Watch Series 7"),
    GalaxyWatch4("Samsung Galaxy Watch 4"),

    // --- Microwave ---
    PanasonicInverter("Panasonic Inverter"),
    ToshibaSolo("Toshiba Solo Microwave"),

    // --- Speaker ---
    JBLFlip5("JBL Flip 5"),
    SonySRS("Sony SRS-XB33"),

    // --- Monitor ---
    LGUltraGear("LG UltraGear 27\""),
    SamsungOdyssey("Samsung Odyssey G7"),

    // --- Camera ---
    CanonR6("Canon EOS R6"),
    SonyA7("Sony Alpha 7 IV")
}