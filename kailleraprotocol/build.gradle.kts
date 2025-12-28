plugins {
  kotlin("multiplatform")
  id("org.jetbrains.dokka")
}

kotlin {
  applyDefaultHierarchyTemplate {
    common {
      group("unix") {
        withLinux()
        // withMacos()
        // withIos()
        // Add other unix targets if needed in future (watchos/tvos)
      }
    }
  }

  jvm()
  js(IR) {
    browser()
    nodejs()
  }

  // Native Targets
  // iosX64()
  // iosArm64()
  // iosSimulatorArm64()
  // macosX64()
  // macosArm64()
  linuxX64()
  mingwX64()

  sourceSets {
    val commonMain by getting {
      dependencies { implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.0") }
    }
    val commonTest by getting { dependencies { implementation(kotlin("test")) } }

    val jvmMain by getting {
      dependencies { implementation("io.netty:netty-buffer:4.1.115.Final") }
    }

    val jvmTest by getting {
      dependencies {
        implementation("com.google.truth:truth:1.1.5")
        implementation(kotlin("reflect"))
      }
    }

    val nativeMain by getting
  }
}

// kdoc generation support.
subprojects { apply(plugin = "org.jetbrains.dokka") }
