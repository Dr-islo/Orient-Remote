# Orient Remote - Proguard / R8 rules

# Keep Hilt generated components
-keep class dagger.hilt.internal.aggregatedroot.codegen.* { *; }
-keep class hilt_aggregated_deps.* { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager

# Keep data models (used for DataStore serialization keys / reflection-free but kept for safety)
-keep class com.orientremote.app.data.model.** { *; }

# Kotlin coroutines
-dontwarn kotlinx.coroutines.flow.**

# General Android
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
