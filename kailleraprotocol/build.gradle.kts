plugins { kotlin("multiplatform") }

kotlin {
  jvm()
  js(IR) {
    browser()
    nodejs()
  }

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
  }
}
