plugins {
  kotlin("multiplatform") version "2.1.0" apply false
  id("org.jetbrains.dokka") version "2.0.0" apply false

  id("com.diffplug.spotless") version "7.2.1"
}

allprojects {
  repositories {
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
