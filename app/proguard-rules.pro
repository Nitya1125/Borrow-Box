# Add project specific ProGuard rules here.
-keep class com.borrowbox.app.models.** { *; }
-keep class com.borrowbox.app.data.** { *; }
-keep class com.borrowbox.app.utils.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
  <init>(...);
}
-dontwarn com.bumptech.glide.**

# ZXing
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# ML Kit Barcode Scanning
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# CameraX
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**
