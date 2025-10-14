<p align="center">
  <img src="https://babylai.net/assets/logo-BdByHTQ3.svg" alt="BabylAI Logo" height="200"/>
</p>

# BabylAI Android SDK

An Android SDK that provides integration with BabylAI chat functionality, supporting multiple themes and languages.

## Features

- 🚀 Easy integration with BabylAI chat
- 🌓 Support for light and dark themes
- 🎨 **Advanced Theme Customization** - Custom brand colors for light and dark themes
- 🖼️ **Custom Logo Support** - Replace header logo with your brand logo
- 🌍 **Dynamic Language Switching** - Runtime language change (English and Arabic with RTL)
- 📬 Message receiving callback for custom notification handling
- ⚠️ **Comprehensive Error Handling** - Global and view-specific error callbacks
- ⚡ Quick access to active chats
- 🏗️ Environment-based configuration (Production/Development)
- 🔒 Secure, predefined API endpoints
- 📱 Jetpack Compose UI components
- 🎨 Material Design 3 theming with automatic color generation

## Installation

### Maven Central (Recommended)

The BabylAI Android SDK is published to Maven Central for easy integration. Simply add the dependency to your app-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.aau-iq:babylai-android-sdk:1.0.63")
}
```

**That's it!** Maven Central is already configured by default in Android projects. No additional repository configuration needed.

#### Minimum Requirements
```kotlin
android {
    defaultConfig {
        minSdk = 24  // Required by BabylAI SDK
    }
}
```

### Alternative Installation Methods

<details>
<summary>Option 2: GitHub Releases Repository</summary>

Add the GitHub repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://raw.githubusercontent.com/AAU-IQ/BabylAI-Android/main/releases")
            metadataSources {
                mavenPom()
                artifact()
            }
            content { includeGroup("iq.aau.babylai.android") }
        }
    }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("iq.aau.babylai.android:babylaisdk:1.0.61-beta3")
}
```

</details>

<details>
<summary>Option 3: Direct AAR Download</summary>

1. Download the latest AAR from [releases/latest/BabylAISDK-release.aar](releases/latest/BabylAISDK-release.aar)
2. Place it in your app's `libs/` directory
3. Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(files("libs/BabylAISDK-release.aar"))
}
```

</details>

## Usage

### 1. Initialize BabylAI with Environment Configuration

First, initialize BabylAI with the appropriate environment configuration and set up the token callback:

```kotlin
import iq.aau.babylai.android.babylaisdk.BabylAI
import iq.aau.babylai.android.babylaisdk.config.EnvironmentConfig
import iq.aau.babylai.android.babylaisdk.config.BabylAIEnvironment
import iq.aau.babylai.android.babylaisdk.config.ThemeConfig
import iq.aau.babylai.android.babylaisdk.core.enums.BabylAILocale
import iq.aau.babylai.android.babylaisdk.core.errors.BabylAIError
import androidx.core.graphics.toColorInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create environment configuration
        val config = EnvironmentConfig.production(enableLogging = false) // or EnvironmentConfig.development()
        
        // Initialize BabylAI with environment configuration and custom theming
        BabylAI.shared.initialize(
            context = this,
            config = config,
            locale = BabylAILocale.ENGLISH, // or BabylAILocale.ARABIC
            userInfo = mapOf(
                "name" to "John Doe",
                "email" to "johndoe@example.com",
                "phone" to "+1234567890"
            ),
            themeConfig = ThemeConfig(
                primaryColor = "#4A6741".toColorInt(),           // Elegant forest green for light theme
                secondaryColor = "#D4AF37".toColorInt(),         // Sophisticated gold for light theme
                primaryColorDark = "#81C784".toColorInt(),       // Soft sage green for dark theme
                secondaryColorDark = "#F9D71C".toColorInt(),     // Warm amber for dark theme
                headerLogoRes = R.drawable.your_custom_logo      // Optional: Your brand logo
            ),
            onErrorReceived = { error ->
                // Optional: Handle global errors
                println("❌ SDK Error [${error.errorCode}]: ${error.userFriendlyMessage}")
            }
        )
        
        // IMPORTANT: You MUST set up a token callback for the package to work
        BabylAI.shared.setTokenCallback {
            // Example implementation to get a token
            return@setTokenCallback getToken() // Return your access token as string
        }
        
        // Optional: Set up global error handling callback
        BabylAI.shared.setOnErrorReceived { error: BabylAIError ->
            println("❌ SDK Error [${error.errorCode}]: ${error.userFriendlyMessage}")
            // Handle errors globally - show notifications, log to analytics, etc.
        }
        
        // Optional: Change language dynamically after initialization
        BabylAI.shared.setLocale(BabylAILocale.ARABIC) // Switch to Arabic with RTL support
    }
}
```

> ⚠️ **Important**: You must call `BabylAI.shared.initialize()` and `BabylAI.shared.setTokenCallback()` before using any other BabylAI functionality. Failure to do so will result in authentication errors when trying to launch the chat interface.

### Environment Configuration

The package supports two environments:

- **Production**: Uses production API endpoints, logging disabled by default
- **Development**: Uses development API endpoints, logging enabled by default

You can create environment configurations using factory methods:

```kotlin
// Production environment (logging disabled by default)
val productionConfig = EnvironmentConfig.production()

// Production environment with logging enabled
val productionConfigWithLogging = EnvironmentConfig.production(enableLogging = true)

// Development environment (logging enabled by default)
val developmentConfig = EnvironmentConfig.development()

// Development environment with custom timeouts
val customDevConfig = EnvironmentConfig.development(
    enableLogging = true,
    connectionTimeout = 60000,
    receiveTimeout = 30000
)
```

### Dynamic Language Switching

The BabylAI SDK supports dynamic language switching without requiring re-initialization. You can change the language at runtime and the SDK will update all text content and layout direction accordingly.

#### Setting Language Dynamically

```kotlin
// Switch to Arabic with RTL support
BabylAI.shared.setLocale(BabylAILocale.ARABIC)

// Switch back to English with LTR support
BabylAI.shared.setLocale(BabylAILocale.ENGLISH)

// Get current locale
val currentLocale = BabylAI.shared.getLocale()
```

#### Example with UI Controls

```kotlin
@Composable
fun LanguageSwitcher() {
    var isArabic by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Arabic Language")
        Switch(
            checked = isArabic,
            onCheckedChange = { enabled ->
                isArabic = enabled
                // Update SDK language dynamically
                BabylAI.shared.setLocale(
                    if (enabled) BabylAILocale.ARABIC else BabylAILocale.ENGLISH
                )
            }
        )
    }
}
```

#### Language Features

- **English (BabylAILocale.ENGLISH)**:
  - Left-to-right (LTR) layout direction
  - English text content and labels
  - Western number formatting

- **Arabic (BabylAILocale.ARABIC)**:
  - Right-to-left (RTL) layout direction
  - Arabic text content and labels
  - Arabic/Eastern number formatting
  - Proper RTL text alignment

#### Notes

- Language changes take effect immediately in active SDK views
- The locale setting persists across SDK sessions
- RTL layout automatically adjusts all UI components, icons, and navigation
- No re-initialization required when switching languages

### 2. Basic Implementation

Here's a simple example of how to integrate BabylAI in your Android app:

```kotlin
import iq.aau.babylai.android.babylaisdk.BabylAI
import iq.aau.babylai.android.babylaisdk.BabylAITheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<Button>(R.id.openChatButton).setOnClickListener {
            // Show chat interface
            // You can use the composable directly in your Compose UI
        }
    }
}

@Composable
fun BabylAIChatScreen(
    navController: NavController,
    isDirect: Boolean = false,
    isDarkMode: Boolean = false
) {
    // Handle back navigation using system back gesture
    androidx.activity.compose.BackHandler {
        navController.popBackStack()
    }
    
    // Get the BabylAI viewer composable
    val currentTheme = if (isDarkMode) BabylAITheme.DARK else BabylAITheme.LIGHT
    
    val viewerComposable = BabylAI.shared.viewer(
        theme = currentTheme,
        isDirect = isDirect,
        screenId = "YOUR_SCREEN_ID",
        onMessageReceived = { message ->
            println("📨 Received message: $message")
        },
        onDismiss = {
            // Handle SDK back navigation - go back to home screen
            navController.popBackStack()
        }
    )
    
    // Display the SDK view - it will take full screen
    viewerComposable()
}
```

### 3. Advanced Implementation with Jetpack Compose

For a more complete implementation with theme and language switching:

```kotlin
import androidx.compose.runtime.*
import iq.aau.babylai.android.babylaisdk.BabylAI
import iq.aau.babylai.android.babylaisdk.BabylAITheme

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
        BabylAI.shared.viewer(
            theme = BabylAITheme.LIGHT,
            isDirect = false,
            screenId = "YOUR_SCREEN_ID",
            onMessageReceived = { message ->
                // Handle new message notifications
                println("New message: $message")
            },
            onDismiss = { showChat = false }
        )
    }
    
    if (showActiveChat) {
        BabylAI.shared.presentActiveChat(
            theme = BabylAITheme.DARK,
            screenId = "YOUR_SCREEN_ID",
            onMessageReceived = { message ->
                // Handle messages for active chat
                println("Active chat message: $message")
            },
            onDismiss = { showActiveChat = false }
        )
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
        BabylAI.shared.viewer(
            theme = BabylAITheme.LIGHT,
            isDirect = false,
            screenId = "YOUR_SCREEN_ID",
            onMessageReceived = { message ->
                viewModel.handleNewMessage(message)
            },
            onDismiss = { viewModel.closeChat() }
        )
    }
}
```

## API Reference

### BabylAI Class

#### Methods

- `BabylAI.shared.initialize(context: Context, config: EnvironmentConfig, locale: BabylAILocale, userInfo: Map<String, Any>?, themeConfig: ThemeConfig?, onErrorReceived: ((BabylAIError) -> Unit)?)`: Initialize BabylAI with environment configuration and optional theme customization
- `BabylAI.shared.setTokenCallback(callback: suspend () -> String)`: Set a callback function that will be called when the token needs to be refreshed
- `BabylAI.shared.setOnErrorReceived(callback: (BabylAIError) -> Unit)`: Set a global error callback to handle all SDK errors
- `BabylAI.shared.setLocale(locale: BabylAILocale)`: Change the SDK language dynamically without re-initialization
- `BabylAI.shared.getLocale(): BabylAILocale`: Get the currently selected SDK language
- `BabylAI.shared.getViewerComposable(theme: BabylAITheme = BabylAITheme.LIGHT, isDirect: Boolean = false, screenId: String, onMessageReceived: ((String) -> Unit)? = null, onBack: () -> Unit = {}) -> @Composable () -> Unit`: Get the BabylAI chat interface as a Compose composable
- `BabylAI.shared.makeView(theme: BabylAITheme, userInfo: Map<String, Any>, screenId: String, onMessageReceived: ((String) -> Unit)? = null, onErrorReceived: ((BabylAIError) -> Unit)? = null) -> @Composable`: Create the main SDK view
- `BabylAI.shared.viewer(theme: BabylAITheme = BabylAITheme.LIGHT, isDirect: Boolean = false, screenId: String, onMessageReceived: ((String) -> Unit)? = null, onErrorReceived: ((BabylAIError) -> Unit)? = null, onDismiss: () -> Unit = {}) -> @Composable`: Create the SDK viewer with token validation wrapper
- `BabylAI.shared.presentActiveChat(theme: BabylAITheme, screenId: String, onMessageReceived: ((String) -> Unit)? = null, onErrorReceived: ((BabylAIError) -> Unit)? = null, onDismiss: () -> Unit = {}) -> @Composable`: Present the active chat directly

#### Environment Configuration

- `EnvironmentConfig.production(enableLogging: Boolean = false, connectionTimeout: Int = 30_000, receiveTimeout: Int = 15_000)`: Production environment configuration
- `EnvironmentConfig.development(enableLogging: Boolean = true, connectionTimeout: Int = 30_000, receiveTimeout: Int = 15_000)`: Development environment configuration

#### Theme Configuration

- `BabylAITheme.LIGHT`: Light theme
- `BabylAITheme.DARK`: Dark theme
- `ThemeConfig(primaryColor, secondaryColor, primaryColorDark, secondaryColorDark, headerLogoRes)`: Comprehensive theme customization with separate light/dark colors and custom logo support

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
| themeConfig | ThemeConfig? | Optional theme customization with brand colors and logo |
| isDirect | Boolean | Whether to open active chat directly |
| onMessageReceived | ((String) -> Unit)? | Callback for handling new messages |
| onErrorReceived | ((BabylAIError) -> Unit)? | Callback for handling view-specific errors |
| onBack | () -> Unit | Callback for back navigation |

## Message Handling

The package provides a callback for handling new messages through the `onMessageReceived` parameter. You can implement your own notification system or message handling logic. Here's an example of how you might handle new messages:

```kotlin
BabylAI.shared.viewer(
    screenId = "YOUR_SCREEN_ID",
    onMessageReceived = { message ->
        // Implement your preferred notification system
        // For example, using NotificationManager
        // or any other notification approach
        showCustomNotification(message)
    }
)
```

## Error Handling in Views

In addition to the global error callback, individual views can also handle errors locally:

```kotlin
BabylAI.shared.viewer(
    theme = BabylAITheme.LIGHT,
    screenId = "YOUR_SCREEN_ID",
    onMessageReceived = { message ->
        // Handle new messages
        handleNewMessage(message)
    },
    onErrorReceived = { error ->
        // Handle errors specific to this view instance
        handleViewError(error)
    }
)
```

> **Note**: View-specific error callbacks will be called in addition to the global error callback, giving you flexibility to handle errors at both global and local levels.

## Prerequisites

- Android Studio 4.0+
- Android API Level 24+
- Kotlin 1.8+
- Jetpack Compose (for composable integration)

## Example App

For a complete integration example, see [BabylAI-Android-Example](https://github.com/AAU-IQ/BabylAI-Android/tree/main/app/src/main/java/iq/aau/babylai/android/BabylAI).

## Contributing

For any issues or feature requests, please contact the package maintainers or submit an issue on the repository.

## License

Copyright © 2025 BabylAI

This software is provided under a custom license agreement. Usage is permitted only with explicit authorization from BabylAI. This software may not be redistributed, modified, or used in derivative works without written permission from BabylAI.

All rights reserved.
