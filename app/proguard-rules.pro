# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Retrofit Rules
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepattributes Signature, InnerClasses, EnclosingMethod

# OkHttp Rules
-keepclassmembers class okhttp3.internal.publicsuffix.PublicSuffixDatabase {
    *** publicSuffixListBytes;
    *** publicSuffixExceptionListBytes;
}
-dontwarn okhttp3.**
-dontwarn okio.**

# Room DB Rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Kotlinx Serialization Rules
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep class *$$serializer { *; }
-keep class * { @kotlinx.serialization.Serializable *; }
-keepclassmembers class * {
    *** Companion;
}

# Hilt & Dagger Rules
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent
-keep class * extends dagger.hilt.internal.GeneratedComponentManager
-keep class * implements dagger.hilt.internal.GeneratedComponent
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * {
    public <init>(...);
}