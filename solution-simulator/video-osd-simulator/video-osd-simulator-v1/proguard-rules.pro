-dontshrink
-dontoptimize
-useuniqueclassmembernames
-adaptclassstrings
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions,MethodParameters
-keepdirectories
-dontwarn

-keep class com.cv.simulator.videoosd.v1.VideoOsdSimulatorV1Application {
    public static void main(java.lang.String[]);
}

-keep @org.springframework.context.annotation.Configuration class * { *; }
-keep @org.springframework.stereotype.Component class * { *; }
-keep @org.springframework.stereotype.Service class * { *; }
-keep @org.springframework.boot.context.properties.ConfigurationProperties class * { *; }
-keepclassmembers class * {
    @org.springframework.context.annotation.Bean *;
}

-keep class com.cv.simulator.videoosd.v1.config.** { *; }
-keep class com.cv.simulator.videoosd.v1.websocket.** { *; }
-keep class com.cv.simulator.videoosd.v1.mqtt.** { *; }
-keep class com.cv.simulator.videoosd.v1.model.** { *; }
-keep class com.cv.simulator.videoosd.v1.osd.** { *; }

-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.JsonProperty <fields>;
    @com.fasterxml.jackson.annotation.JsonProperty <methods>;
}
