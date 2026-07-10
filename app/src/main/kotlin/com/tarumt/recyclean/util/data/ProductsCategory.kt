package com.tarumt.recyclean.util.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Watch
import androidx.compose.ui.graphics.vector.ImageVector
import com.tarumt.recyclean.R

enum class ProductsCategory(val icons: ImageVector, val devices: Array<AltDevices>) {
    Mobile(
        Icons.Default.PhoneIphone,
        arrayOf(AltDevices.Apple, AltDevices.Samsung, AltDevices.Xiaomi, AltDevices.Huawei, AltDevices.Vivo)
    ),
    Tablet(
        Icons.Default.Tablet,
        arrayOf(AltDevices.AppleIPad, AltDevices.SamsungTab, AltDevices.XiaomiPad)
    ),
    Laptop(
        Icons.Default.Laptop,
        arrayOf(AltDevices.MacBook, AltDevices.ASUS, AltDevices.Dell, AltDevices.HP, AltDevices.Lenovo)
    ),
    Watch(
        Icons.Default.Watch,
        arrayOf(AltDevices.AppleWatch, AltDevices.GalaxyWatch, AltDevices.Garmin)
    ),
    TV(
        Icons.Default.Tv,
        arrayOf(AltDevices.SonyTV, AltDevices.SamsungTV, AltDevices.LGTV, AltDevices.XiaomiTV)
    ),
    Speaker(
        Icons.Default.Speaker,
        arrayOf(AltDevices.JBL, AltDevices.SonySpeaker, AltDevices.Marshall)
    ),
    Monitor(
        Icons.Default.Monitor,
        arrayOf(AltDevices.LGMonitor, AltDevices.ASUSMonitor, AltDevices.SamsungMonitor)
    ),
    Camera(
        Icons.Default.CameraAlt,
        arrayOf(AltDevices.SonyCamera, AltDevices.Canon, AltDevices.Fujifilm)
    )
}

enum class AltDevices(val deviceName: String, val icon: Int = R.drawable.ic_launcher_foreground) {
    // 手机品牌
    Apple("Apple", R.drawable.apple_logo),
    Samsung("Samsung", R.drawable.samsung_logo),
    Xiaomi("Xiaomi", R.drawable.xiaomi_logo),
    Huawei("Huawei", R.drawable.huawei_logo),
    Vivo("Vivo", R.drawable.vivo_logo),

    // 平板品牌
    AppleIPad("Apple iPad", R.drawable.apple_logo),
    SamsungTab("Samsung Galaxy Tab", R.drawable.samsung_logo),
    XiaomiPad("Xiaomi Pad", R.drawable.xiaomi_logo),

    // 电脑品牌
    MacBook("Apple MacBook", R.drawable.apple_logo),
    ASUS("ASUS", R.drawable.asus_logo),
    Dell("Dell", R.drawable.dell_logo),
    HP("HP", R.drawable.hp_logo),
    Lenovo("Lenovo", R.drawable.lenovo_logo),

    // 手表品牌
    AppleWatch("Apple Watch", R.drawable.apple_logo),
    GalaxyWatch("Samsung Galaxy Watch", R.drawable.samsung_logo),
    Garmin("Garmin", R.drawable.garmin_logo),

    // 电视品牌
    SonyTV("Sony TV", R.drawable.sony_logo),
    SamsungTV("Samsung TV", R.drawable.samsung_logo),
    LGTV("LG TV", R.drawable.lg_logo),
    XiaomiTV("Xiaomi TV", R.drawable.xiaomi_logo),

    // 音响品牌
    JBL("JBL", R.drawable.jbl_logo),
    SonySpeaker("Sony Speaker", R.drawable.sony_logo),
    Marshall("Marshall", R.drawable.marshall_logo),

    // 显示器品牌
    LGMonitor("LG Monitor", R.drawable.lg_logo),
    ASUSMonitor("ASUS Monitor", R.drawable.asus_logo),
    SamsungMonitor("Samsung Monitor", R.drawable.samsung_logo),

    // 相机品牌
    SonyCamera("Sony Camera", R.drawable.sony_logo),
    Canon("Canon", R.drawable.canon_logo),
    Fujifilm("Fujifilm", R.drawable.fujifilm_logo)
}