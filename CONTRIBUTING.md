# Contributing to KailleraProtocol-kmp

Thank you for your interest in contributing to the KailleraProtocol-kmp project! This document provides guidelines and information to help you get started.

## Repository Organization

This is a Kotlin Multiplatform (KMP) project. The main module is `kailleraprotocol`.

- **`kailleraprotocol/`**: The main module containing the protocol implementation.
    - **`src/commonMain/`**: Common code shared across all platforms (pure Kotlin).
    - **`src/jvmMain/`**: JVM-specific implementations and optimizations (e.g., Netty integration).
    - **`src/jsMain/`**: JavaScript-specific implementations.
    - **`src/commonTest/`**, **`src/jvmTest/`**, **`src/jsTest/`**: Tests for the respective source sets.

## Common Commands

You can use the Gradle wrapper (`./gradlew`) to run various tasks.

### Compiling
To build the Kotlin code:

```bash
./gradlew :kailleraprotocol:compileKotlinJvm 
```

### Testing
To run all checks (including tests and linting):
```bash
./gradlew check
```

To run only JVM tests:
```bash
./gradlew kailleraprotocol:jvmTest
```

### Building
To build the entire project (including tests and linting):
```bash
./gradlew build
```

## Guidelines

### Formatting
- Adhere to [Google's Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).
- The project is configured with Kotlin style checks. Running `./gradlew check` will verify formatting.

### Testing
- Please ensure all tests pass before submitting a Pull Request.
- New features or bug fixes should be accompanied by relevant unit tests.
