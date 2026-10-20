# Build environment for Clausura (AGP 4.0.2 / Gradle 6.1.1 / compileSdk 30).
# Stage 1: current sdkmanager needs Java 17.
FROM eclipse-temurin:17-jdk-jammy AS sdk

ARG CMDLINE_TOOLS=commandlinetools-linux-11076708_latest.zip
ENV ANDROID_SDK_ROOT=/opt/android-sdk

RUN apt-get update \
 && apt-get install -y --no-install-recommends unzip curl \
 && rm -rf /var/lib/apt/lists/* \
 && mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools" \
 && curl -fsSL -o /tmp/tools.zip "https://dl.google.com/android/repository/$CMDLINE_TOOLS" \
 && unzip -q /tmp/tools.zip -d "$ANDROID_SDK_ROOT/cmdline-tools" \
 && mv "$ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools" "$ANDROID_SDK_ROOT/cmdline-tools/latest" \
 && rm /tmp/tools.zip

# build-tools 28.0.3 is what app/build.gradle asks for; AGP 4.0.2 requires
# at least 29.0.2 and uses that one instead.
RUN yes | "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" --licenses > /dev/null \
 && "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" \
        "platforms;android-30" "build-tools;28.0.3" "build-tools;29.0.2"

# Stage 2: AGP 4.0.x / Gradle 6.1.1 run on Java 8 or 11.
FROM eclipse-temurin:11-jdk-jammy

ENV ANDROID_SDK_ROOT=/opt/android-sdk \
    ANDROID_HOME=/opt/android-sdk \
    GRADLE_USER_HOME=/project/.docker-cache/gradle \
    HOME=/tmp

COPY --from=sdk /opt/android-sdk /opt/android-sdk
RUN chmod -R a+rX /opt/android-sdk

WORKDIR /project
