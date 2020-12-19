-keep class com.d.lib.common.** {*;}

# ----- ButterKnife -----
-keep class butterknife.** {*;}
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder {*;}
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# ----- NineOldAndroids -----
-keep class com.nineoldandroids.** {*;}
-dontwarn com.nineoldandroids.*

# ----- Glide v4 -----
-keep class com.bumptech.glide.** {*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {*;}
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
