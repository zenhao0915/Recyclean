-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,allowobfuscation,allowshrinking class * {
    <fields>;
}

-keep class com.tarumt.recyclean.util.data.** { *; }
-keep class com.tarumt.recyclean.screen.** { *; }

-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }

-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean