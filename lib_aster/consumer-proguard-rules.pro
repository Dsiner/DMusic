-keep class com.d.lib.aster.** {*;}

# ----- Gson -----
-keep class com.google.gson.** {*;}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature,SourceFile,LineNumberTable
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-keep class sun.misc.Unsafe {*;}
# Application classes that will be serialized/deserialized over Gson

# ----- rxjava && rxandroid -----
-dontwarn io.reactivex.**
-keep class io.reactivex.** {*;}

# ----- okhttp3 && okio -----
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** {*;}
-keep class okio.** {*;}

# ----- retrofit2 -----
-dontwarn retrofit2.**
-keep class retrofit2.** {*;}
