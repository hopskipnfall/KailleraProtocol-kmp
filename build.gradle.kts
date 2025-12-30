plugins {
  kotlin("multiplatform") version "2.3.0" apply false
  id("org.jetbrains.dokka") version "2.1.0" apply false

  id("com.diffplug.spotless") version "8.1.0"
}

allprojects {
  group = "io.github.hopskipnfall"
  version = "0.1.0-SNAPSHOT"

  repositories {
    google()
    mavenLocal()
    mavenCentral()
  }
}

// Formatting/linting.
spotless {
  kotlin {
    target("**/*.kt", "**/*.kts")
    targetExclude("bin/", "build/", ".git/", ".idea/", ".mvn", "src/main/java-templates/")
    ktfmt().googleStyle()
  }

  yaml {
    target("**/*.yml", "**/*.yaml")
    targetExclude("build/", ".git/", ".idea/", ".mvn")
    jackson()
  }
}
