plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    alias(libs.plugins.maven.publish)
    signing
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
}

group = "io.ak1.pix"
version = "1.6.8"

android {
    compileSdk = 36
    namespace = "io.ak1.pix"

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

// ==================== SPOTLESS CONFIGURATION ====================
spotless {
    kotlin {
        target("src/**/*.kt")
        targetExclude("**/build/**/*.kt")

        ktlint("1.8.0")
            .editorConfigOverride(
                mapOf(
                    "ktlint_standard_no-wildcard-imports" to "disabled",
                    "ktlint_standard_trailing-comma-on-call-site" to "disabled",
                    "ktlint_standard_trailing-comma-on-declaration-site" to "disabled",
                    "max_line_length" to "120",
                ),
            )

        val currentYear = 2026
        licenseHeader(
            """
            /*
             * Copyright (C) $currentYear Akshay Sharma
             *
             * Licensed under the Apache License, Version 2.0 (the "License");
             * you may not use this file except in compliance with the License.
             * You may obtain a copy of the License at
             *
             *      http://www.apache.org/licenses/LICENSE-2.0
             *
             * Unless required by applicable law or agreed to in writing, software
             * distributed under the License is distributed on an "AS IS" BASIS,
             * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
             * See the License for the specific language governing permissions and
             * limitations under the License.
             */
            """.trimIndent(),
        )

        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.8.0")
    }

    format("xml") {
        target("**/*.xml")
        targetExclude("**/build/**/*.xml")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// ==================== DOKKA CONFIGURATION ====================
val dokkaCurrentYear = 2026
tasks.dokkaHtml.configure {
    outputDirectory.set(layout.buildDirectory.dir("dokka/html"))

    dokkaSourceSets {
        configureEach {
            moduleName.set("Pix Image Picker")
            moduleVersion.set(version.toString())

            includes.from("README.md")

            sourceLink {
                localDirectory.set(file("src/main/java"))
                remoteUrl.set(
                    uri("https://github.com/akshay2211/PixImagePicker/tree/master/pix/src/main/java").toURL(),
                )
                remoteLineSuffix.set("#L")
            }

            documentedVisibilities.set(
                setOf(
                    org.jetbrains.dokka.DokkaConfiguration.Visibility.PUBLIC,
                    org.jetbrains.dokka.DokkaConfiguration.Visibility.PROTECTED,
                ),
            )

            externalDocumentationLink {
                url.set(uri("https://developer.android.com/reference/").toURL())
            }

            externalDocumentationLink {
                url.set(uri("https://kotlin.github.io/kotlinx.coroutines/").toURL())
            }

            suppressObviousFunctions.set(true)
            suppressInheritedMembers.set(false)
        }
    }

    pluginsMapConfiguration.set(
        mapOf(
            "org.jetbrains.dokka.base.DokkaBase" to """{
                "footerMessage": "© $dokkaCurrentYear Akshay Sharma - Pix Image Picker",
                "homepageLink": "https://github.com/akshay2211/PixImagePicker",
                "separateInheritedMembers": true,
                "customAssets": [],
                "customStyleSheets": [],
                "customScripts": []
            }""",
        ),
    )
}

tasks.register("dokkaHtmlOpen") {
    dependsOn("dokkaHtml")
    doLast {
        val htmlFile = file("${'$'}{layout.buildDirectory.get()}/dokka/html/index.html")
        if (htmlFile.exists() && System.getProperty("os.name").lowercase().contains("mac")) {
            Runtime.getRuntime().exec(arrayOf("open", htmlFile.absolutePath))
        }
    }
}

// ==================== DETEKT CONFIGURATION ====================
// Detekt is configured at the root level in build.gradle.kts
// This module just declares the detekt plugin above

dependencies {
    // Kotlin
    implementation(libs.kotlin.stdlib)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.fragment.ktx)

    // CameraX
    implementation(libs.bundles.camerax)

    // Glide
    implementation(libs.glide)
    ksp(libs.glide.ksp)
    implementation(libs.glide.recyclerview.integration) {
        exclude(group = "com.android.support")
    }

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // Detekt plugins
    detektPlugins(libs.detekt.formatting)
}

signing {
    sign(publishing.publications)
}
