# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AppSoftware\AndroidSDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService

-keepattributes SourceFile,LineNumberTable

-keepclasseswithmembernames class * {
  native <methods>;
}

-keepclasseswithmembernames class * {
  public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
  public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }

#gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.sunloto.shandong.bean.** { *; }

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#fresco
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**

#umeng
-dontwarn com.umeng.update.c$a
-dontwarn com.umeng.update.net.DownloadingService$1
-dontwarn com.umeng.update.net.DownloadingService$b
-dontwarn com.umeng.update.net.c$c
-keep public class * extends com.umeng.**
-keep class com.umeng.** { *; }
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class studio.uphie.zhihudaily.R$*{
    public static final int *;
}

