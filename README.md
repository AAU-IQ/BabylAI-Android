# BabylAI Android SDK

Official Android SDK for BabylAI - AI-powered chat interface.

## Installation

### Option 1: Direct AAR Download
Download the latest AAR from [releases/latest/BabylAISDK-release.aar](releases/latest/BabylAISDK-release.aar)

Add to your app's `build.gradle`:
```gradle
dependencies {
    implementation files('libs/BabylAISDK-release.aar')
}
```

### Option 2: JitPack (Recommended)
```gradle
dependencies {
    implementation 'com.github.AAU-IQ:babyl-ai-android-sdk:1.0.0'
}
```

## Quick Start

```kotlin
// Initialize SDK
BabylAI.initialize(
    apiKey = "your-api-key",
    environment = EnvironmentConfig.PRODUCTION
)

// Show chat interface
BabylAI.showChat(context)
```

## Documentation
- [Integration Guide](docs/integration-guide.md)
- [API Reference](docs/api-reference.md)

## Version History
- [v$VERSION](releases/$VERSION/) - Latest release
- [All Releases](releases/)

## License
MIT License - see [LICENSE](LICENSE) file
