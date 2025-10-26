# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable
# ===== play safe, dont optimize, preverify
#-dontoptimize : this causes log statments to not get removed
-dontpreverify

#######===== remove Log statements =====
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

#Want to obfuscate your code but still have line numbers in your stack traces? Add these two lines to your ProGuard config
#The first line ensures that source file names are hidden (will be named "SourceFile" in stack traces),
#the second instructs ProGuard to include the line numbers.
#works like a charm in Crashlytics : get decoded package name (from mapping file) and line number and dummy source file name
########================= crashlytics to see crash reports ===============
# check what causes 'compiled from: ' in decompiled classes
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

#### Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

#### Room
-keep class android.arch.persistence.room.Entity
-keep @android.arch.persistence.room.Entity class * { *; }

#### Retrofit { https://square.github.io/retrofit }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

##### okhttp3
# okHttp3
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
# I was able to get rid of this issue adding this Proguard line to my configuration :
-keep class okhttp3.Headers { *; }

#### GSON
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

# Prevent R8 from leaving Data object members always null
-keepclasseswithmembers class * {
    <init>(...);
    @com.google.gson.annotations.SerializedName <fields>;
}
# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

#### Referral
-keep public class com.android.installreferrer.** { *; }

#-printusage usage.txt

#-keep class com.rocketpay.mandate.feature.*.data.remote.** { *; }
#-keep class com.rocketpay.mandate.feature.*.*.data.remote.** { *; }
#-keep class com.rocketpay.mandate.feature.*.data.*.remote.** { *; }
#-keep class com.rocketpay.mandate.feature.*.*.data.*.remote.** { *; }
#-keep class com.rocketpay.mandate.common.feature.** { *; }
#-keep class com.rocketpay.mandate.common.contact.** { *; }
#-keep class com.rocketpay.mandate.common.base.** { *; }
#-keep class com.rocketpay.mandate.common.firebase.** { *; }
#-keep class com.rocketpay.mandate.common.notification.** { *; }
#-keep class com.rocketpay.mandate.*.*.remote.** { *; }
#-keep class com.rocketpay.mandate.*.*.data.remote.** { *; }
#-keep class com.rocketpay.mandate.common.ab.contract.model.** { *; }

-keep class com.google.android.gms.ads.** { *; }
-dontwarn okio.**

-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.joestelmach.natty.**{*;}

#### Hyperverge
-keepclassmembers class co.hyperverge.hypersnapsdk.model.** { *; }
-keepclassmembers class co.hyperverge.hypersnapsdk.data.models.** { *; }

-dontwarn co.hyperverge.hvnfcsdk.adapters.HVNFCAdapter
-dontwarn co.hyperverge.hvnfcsdk.listeners.NFCParserStatus
-dontwarn co.hyperverge.hvnfcsdk.model.HVNFCError
-dontwarn co.hyperverge.hvnfcsdk.views.ProgressWheel
-dontwarn co.hyperverge.hyperdocdetect.carddetectorservice.HVTfliteHelper
-dontwarn co.hyperverge.hyperdocdetect.carddetectorservice.models.HVCardDetectionResult
-dontwarn co.hyperverge.hyperdocdetect.carddetectorservice.models.HVCardDetectorInput
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile