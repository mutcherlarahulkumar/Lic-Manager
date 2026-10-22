# Keep rules for the release (R8-minified) build. Without these, reflective
# JSON (Moshi) and Retrofit interfaces break at runtime in release.

# --- Retrofit ---
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, AnnotationDefault
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# --- Moshi (reflective Kotlin adapter) ---
-keep class kotlin.reflect.jvm.internal.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep @com.squareup.moshi.JsonQualifier interface *

# --- App data models (parsed by Moshi via reflection) ---
-keep class in.bliss.bma.data.model.** { *; }

# --- OkHttp / Okio (benign platform warnings) ---
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
