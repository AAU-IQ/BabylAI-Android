<p align="center">
  <img src="https://babylai.net/assets/logo-BdByHTQ3.svg" alt="BabylAI Logo" height="200"/>
</p>

# BabylAI Android SDK

An Android SDK that provides integration with BabylAI chat functionality, supporting multiple themes and languages.

## Features

- 🚀 Easy integration with BabylAI chat
- 🌓 Support for light and dark themes
- 🌍 Multilingual support (English and Arabic with RTL)
- 📬 Message receiving callback for custom notification handling
- ⚡ Quick access to active chats
- 🏗️ Environment-based configuration (Production/Development)
- 🔒 Secure, predefined API endpoints
- 📱 Jetpack Compose UI components
- 🎨 Material Design 3 theming

## Installation

### Option 1: JitPack (Recommended)

Add the JitPack repository to your project-level `build.gradle` or `settings.gradle.kts`:

```gradle
// settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://raw.githubusercontent.com/AAU-IQ/BabylAI-Android/main/releases")
            metadataSources {
                mavenPom()        // trust the POM
                artifact()        // and the .aar file itself
                ignoreGradleMetadataRedirection()
            }
            content {
                includeGroup("iq.aau.babylai.android")
            }
        }
    }
}
```

Add the dependency to your app-level `build.gradle.kts`:

```gradle
dependencies {
    implementation 'com.github.AAU-IQ:BabylAI-Android:v1.0.3'
}
```

### Option 2: Direct AAR Download

1. Download the latest AAR from [releases/latest/BabylAISDK-release.aar](releases/latest/BabylAISDK-release.aar)
2. Place it in your app's `libs/` directory
3. Add to your `build.gradle.kts`:

```gradle
dependencies {
    implementation files('libs/BabylAISDK-release.aar')
}
```

## Usage

### 1. Initialize BabylAI with Environment Configuration

First, initialize BabylAI with the appropriate environment configuration and set up the token callback:

```kotlin
import iq.aau.babylai.android.babylaisdk.BabylAI
import iq.aau.babylai.android.babylaisdk.config.EnvironmentConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize BabylAI with environment configuration
        BabylAI.initialize(
            apiKey = "YOUR_API_KEY",
            environment = EnvironmentConfig.PRODUCTION // or EnvironmentConfig.DEVELOPMENT
        )
        
        // IMPORTANT: You MUST set up a token callback for the package to work
        BabylAI.setTokenCallback {
            // Example implementation to get a token
            return@setTokenCallback getToken() // Return your access token as string
        }
    }
}
```

> ⚠️ **Important**: You must call `BabylAI.initialize()` and `BabylAI.setTokenCallback()` before using any other BabylAI functionality. Failure to do so will result in authentication errors when trying to launch the chat interface.

### Environment Configuration

The package supports two environments:

- **Production**: Uses production API endpoints, logging disabled by default
- **Development**: Uses development API endpoints, logging enabled by default

You can customize additional settings:

```kotlin
// Production environment with custom settings
BabylAI.initialize(
    apiKey = "YOUR_API_KEY",
    environment = EnvironmentConfig.PRODUCTION
)

// Development environment with custom settings
BabylAI.initialize(
    apiKey = "YOUR_API_KEY",
    environment = EnvironmentConfig.DEVELOPMENT
)
```

### 2. Basic Implementation

Here's a simple example of how to integrate BabylAI in your Android app:

```kotlin
import iq.aau.babylai.android.babylaisdk.BabylAI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<Button>(R.id.openChatButton).setOnClickListener {
            // Show chat interface
            BabylAI.showChat(
                context = this,
                theme = BabylAITheme.LIGHT, // or BabylAITheme.DARK
                locale = BabylAILocale.ENGLISH // or BabylAILocale.ARABIC
            )
        }
    }
}
```

### 3. Advanced Implementation with Jetpack Compose

For a more complete implementation with theme and language switching:

```kotlin
import androidx.compose.runtime.*
import iq.aau.babylai.android.babylaisdk.BabylAI
import iq.aau.babylai.android.babylaisdk.BabylAITheme
import iq.aau.babylai.android.babylaisdk.BabylAILocale

@Composable
fun BabylAIExample() {
    var showChat by remember { mutableStateOf(false) }
    var showActiveChat by remember { mutableStateOf(false) }
    
    Column {
        Button(onClick = { showChat = true }) {
            Text("Launch BabylAI")
        }
        
        Button(onClick = { showActiveChat = true }) {
            Text("Launch Active Chat")
        }
    }
    
    if (showChat) {
        BabylAI.getViewerComposable(
            theme = BabylAITheme.LIGHT,
            isDirect = false,
            onMessageReceived = { message ->
                // Handle new message notifications
                println("New message: $message")
            },
            onBack = { showChat = false }
        )()
    }
    
    if (showActiveChat) {
        BabylAI.getViewerComposable(
            theme = BabylAITheme.DARK,
            isDirect = true,
            onMessageReceived = { message ->
                // Handle messages for active chat
                println("Active chat message: $message")
            },
            onBack = { showActiveChat = false }
        )()
    }
}
```

### 4. ViewModel Integration

For better state management with ViewModels:

```kotlin
class ChatViewModel : ViewModel() {
    private val _showChat = MutableStateFlow(false)
    val showChat: StateFlow<Boolean> = _showChat.asStateFlow()
    
    fun openChat() {
        _showChat.value = true
    }
    
    fun closeChat() {
        _showChat.value = false
    }
    
    fun handleNewMessage(message: String) {
        // Handle incoming messages from BabylAI
        viewModelScope.launch {
            // Update UI, show notifications, etc.
            println("Received: $message")
        }
    }
}

@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val showChat by viewModel.showChat.collectAsState()
    
    if (showChat) {
        BabylAI.getViewerComposable(
            theme = BabylAITheme.LIGHT,
            onMessageReceived = { message ->
                viewModel.handleNewMessage(message)
            },
            onBack = { viewModel.closeChat() }
        )()
    }
}
```

## API Reference

### BabylAI Class

#### Methods

- `BabylAI.initialize(apiKey: String, environment: EnvironmentConfig)`: Initialize BabylAI with environment configuration
- `BabylAI.setTokenCallback(callback: () -> String)`: Set a callback function that will be called when the token needs to be refreshed
- `BabylAI.showChat(context: Context, theme: BabylAITheme = BabylAITheme.LIGHT, locale: BabylAILocale = BabylAILocale.ENGLISH)`: Show the BabylAI chat interface
- `BabylAI.getViewerComposable(theme: BabylAITheme = BabylAITheme.LIGHT, isDirect: Boolean = false, onMessageReceived: ((String) -> Unit)? = null, onBack: () -> Unit = {}) -> @Composable () -> Unit`: Get the BabylAI chat interface as a Compose composable

#### Environment Configuration

- `EnvironmentConfig.PRODUCTION`: Production environment configuration
- `EnvironmentConfig.DEVELOPMENT`: Development environment configuration

#### Theme Configuration

- `BabylAITheme.LIGHT`: Light theme
- `BabylAITheme.DARK`: Dark theme

#### Locale Configuration

- `BabylAILocale.ENGLISH`: English language
- `BabylAILocale.ARABIC`: Arabic language (with RTL support)

### Token Callback

The token callback is essential for authentication with the BabylAI service. The callback should:

1. Make an API request to get a fresh token
2. Parse the response correctly (the token is at the root level with key "token")
3. Return the token as a string
4. Handle errors appropriately

Example response structure:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900
}
```

### Automatic Token Refresh

The package automatically handles token expiration by:

1. Detecting 401 (Unauthorized) or 403 (Forbidden) HTTP errors
2. Automatically calling your token callback to get a fresh token
3. Storing the new token for subsequent requests

This ensures that your users won't experience disruptions when their token expires during a session.

### Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| theme | BabylAITheme | UI theme (.LIGHT or .DARK) |
| locale | BabylAILocale | Language (.ENGLISH or .ARABIC) |
| isDirect | Boolean | Whether to open active chat directly |
| onMessageReceived | ((String) -> Unit)? | Callback for handling new messages |
| onBack | () -> Unit | Callback for back navigation |

## Message Handling

The package provides a callback for handling new messages through the `onMessageReceived` parameter. You can implement your own notification system or message handling logic. Here's an example of how you might handle new messages:

```kotlin
BabylAI.getViewerComposable(
    onMessageReceived = { message ->
        // Implement your preferred notification system
        // For example, using NotificationManager
        // or any other notification approach
        showCustomNotification(message)
    }
)()
```

## Prerequisites

- Android Studio 4.0+
- Android API Level 24+
- Kotlin 1.8+
- Jetpack Compose (for composable integration)

## Example App

For a complete integration example, see [BabylAI-Android-Example](https://github.com/AAU-IQ/BabylAI-Android-Example).

## Contributing

For any issues or feature requests, please contact the package maintainers or submit an issue on the repository.

## License

Copyright © 2024 BabylAI

This software is provided under a custom license agreement. Usage is permitted only with explicit authorization from BabylAI. This software may not be redistributed, modified, or used in derivative works without written permission from BabylAI.

All rights reserved.

Latest version: v1.0.1
