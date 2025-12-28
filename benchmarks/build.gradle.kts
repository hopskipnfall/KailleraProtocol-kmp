plugins {
  kotlin("jvm")
  id("me.champeau.jmh") version "0.7.2"
}

repositories { mavenCentral() }

dependencies {
  implementation(project(":kailleraprotocol"))
  implementation(kotlin("stdlib"))
  implementation("io.netty:netty-buffer:4.1.115.Final")
  implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.0")
}

jmh {
  // Basic JMH configuration
  jmhVersion = "1.37"
  warmupIterations = 1

  // Run with ./gradlew jmh -PjmhDryRun
  if (project.hasProperty("jmhDryRun")) {
    this@jmh.includes = listOf(".*")
    warmupIterations = 0
    iterations = 1
    fork = 0
    failOnError = true
    benchmarkMode = listOf("ss") // "Single Shot" mode (runs method once, minimal timing overhead)
    resultFormat = "JSON"
  }
}

tasks.named("jmh") {
  doLast {
    if (project.hasProperty("jmhDryRun")) {
      val resultsFile = project.layout.buildDirectory.file("results/jmh/results.json").get().asFile
      val json = resultsFile.readText()
      // A simple check: if the JSON is empty or just an empty array "[]", it means no benchmarks
      // ran successfully.
      if (json.replace("\\s+".toRegex(), "") == "[]" || json.isBlank()) {
        throw GradleException(
          "JMH benchmarks failed to produce results (likely due to an exception)."
        )
      }
    }
  }
}
