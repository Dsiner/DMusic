#############################################
#
# Android开发中一些需要保留的公共部分
#
#############################################
# 保留四大组件，自定义的Application等这些类不被混淆，因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

# 处理support包
-dontnote android.support.**
-dontwarn android.support.**

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
-keep interface android.support.v4.app.** {*;}
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 方法名中含有“JNI”字符的，认定是Java Native Interface方法，自动排除
-keepclasseswithmembers class * {
    ... *JNI*(...);
}
# 方法名中含有“JRI”字符的，认定是Java Reflection Interface方法，自动排除
-keepclasseswithmembernames class * {
	... *JRI*(...);
}
-keep class **JNI* {*;}

# 保留在Activity中的方法参数是view的方法，这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保留带有回调函数onXXEvent的，不被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

# 保留Keep注解的类名和方法
-keep,allowobfuscation @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclassmembers class * {
    @android.support.annotation.Keep *;
}

# WebView处理，项目中没有使用到WebView忽略即可
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

#############################################
#
# 项目中特殊处理部分
#
#############################################
# --------------- 处理公共类 ---------------
# 移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用
# 记得proguard-android.txt中一定不要加-dontoptimize才起作用
# 另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# SharedPreferences
-keep public class * extends com.d.commen.commen.AbstractPreference

# --------------- 处理Model类 ---------------

# --------------- 处理反射类 ---------------

# --------------- 处理js交互 ---------------
# 保留js要调用的原生方法不被混淆
#-keepclassmembers class com.d.commen.commen.JSInterface {
#    <methods>;
#}

# --------------- 处理第三方依赖库 ---------------
# ----- android-skin-loader -----
-keep class cn.feng.skin.** {*;}

# ----- tinypinyin -----
-keep class com.github.promeg.pinyinhelper.** {*;}

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

# ----- EventBus -----
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode {*;}
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
-keepclassmembers class ** {
    public void onEvent*(**);
}
