/*
 * BabylAIExampleView.kt
 * BabylAI Android Example App
 *
 * Created by Ahmed Raad on 08/08/2025.
 */

package iq.aau.babylai.android.BabylAI

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import iq.aau.babylai.android.babylaisdk.BabylAI
import iq.aau.babylai.android.babylaisdk.BabylAITheme
import iq.aau.babylai.android.babylaisdk.config.EnvironmentConfig
import iq.aau.babylai.android.babylaisdk.config.ThemeConfig
import iq.aau.babylai.android.babylaisdk.core.enums.BabylAILocale
import iq.aau.babylai.android.babylaisdk.core.errors.BabylAIError
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

/**
 * Environment options for the example app
 */
enum class BabylAIEnvironment(val displayName: String) {
    DEVELOPMENT("Development"),
    STAGING("Staging"),
    PRODUCTION("Production")
}

suspend fun getTokenExample(apiKey: String, tenantId: String, baseUrl: String): String {
    return try {
        TokenClient.fetchToken(
            apiKey = apiKey,
            tenantId = tenantId,
            baseUrl = baseUrl
        )
    } catch (e: Exception) {
        println("❌ Error during token fetch: ${e.message}")
        ""
    }
}

/**
 * Main example view with navigation that demonstrates BabylAI SDK integration
 */
@Composable
fun BabylAIExampleView() {
    val navController = rememberNavController()
    
    // Shared state that persists across navigation
    var isDarkMode by rememberSaveable { mutableStateOf(false) }
    var isArabic by rememberSaveable { mutableStateOf(false) }
    var isInitialized by rememberSaveable { mutableStateOf(false) }
    var environment by rememberSaveable { mutableStateOf(BabylAIEnvironment.DEVELOPMENT) }
    var initializationError by rememberSaveable { mutableStateOf<String?>(null) }

    // Editable credentials, prefilled from local.properties in debug builds only
    var screenId by rememberSaveable { mutableStateOf(BuildConfig.DEFAULT_HELP_SCREEN_ID) }
    var tenantId by rememberSaveable { mutableStateOf(BuildConfig.DEFAULT_TENANT_ID) }
    var apiKey by rememberSaveable { mutableStateOf(BuildConfig.DEFAULT_API_KEY) }


    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                isDarkMode = isDarkMode,
                isArabic = isArabic,
                isInitialized = isInitialized,
                environment = environment,
                initializationError = initializationError,
                screenId = screenId,
                tenantId = tenantId,
                apiKey = apiKey,
                onThemeChange = { isDarkMode = it },
                onLanguageChange = { isArabic = it },
                onInitializationChange = { isInitialized = it },
                onEnvironmentChange = { environment = it },
                onErrorChange = { initializationError = it },
                onScreenIdChange = { screenId = it },
                onTenantIdChange = { tenantId = it },
                onApiKeyChange = { apiKey = it }
            )
        }
        composable("chat") {
            BabylAIChatScreen(
                navController = navController,
                screenId = screenId,
                isDarkMode = isDarkMode
            )
        }
        composable("activeChat") {
            BabylAIChatScreen(
                navController = navController,
                screenId = screenId,
                isDarkMode = isDarkMode
            )
        }
        composable("chatDirect") {
            BabylAIDirectChatScreen(
                navController = navController,
                screenId = screenId,
                isDarkMode = isDarkMode
            )
        }
    }
}

/**
 * Home screen with SDK configuration options
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    isDarkMode: Boolean,
    isArabic: Boolean,
    isInitialized: Boolean,
    environment: BabylAIEnvironment,
    initializationError: String?,
    screenId: String,
    tenantId: String,
    apiKey: String,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (Boolean) -> Unit,
    onInitializationChange: (Boolean) -> Unit,
    onEnvironmentChange: (BabylAIEnvironment) -> Unit,
    onErrorChange: (String?) -> Unit,
    onScreenIdChange: (String) -> Unit,
    onTenantIdChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BabylAI SDK Example") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Configuration Section
            Text(
                text = "SDK Configuration",
                style = MaterialTheme.typography.headlineSmall
            )

            // Credentials (editable, cached in-memory for this session)
            CredentialField(
                value = screenId,
                onValueChange = onScreenIdChange,
                label = "Help Screen ID"
            )

            CredentialField(
                value = tenantId,
                onValueChange = onTenantIdChange,
                label = "Tenant ID"
            )

            CredentialField(
                value = apiKey,
                onValueChange = onApiKeyChange,
                label = "API Key"
            )

            // Theme Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Theme")
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onThemeChange(it) }
                )
            }
            
            // Language Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Arabic Language")
                Switch(
                    checked = isArabic,
                    onCheckedChange = {
                        onLanguageChange(it)
                        BabylAI.shared.setLocale(if (it) BabylAILocale.ARABIC else BabylAILocale.ENGLISH)
                    }
                )
            }
            
            // Environment Picker
            Column {
                Text(
                    text = "Environment",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BabylAIEnvironment.entries.forEachIndexed { index, env ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = BabylAIEnvironment.entries.size
                            ),
                            onClick = { onEnvironmentChange(env) },
                            selected = environment == env
                        ) {
                            Text(env.displayName)
                        }
                    }
                }
            }
            
            // Show error if any
            initializationError?.let { error ->
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Initialize SDK Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val config = when (environment) {
                                BabylAIEnvironment.PRODUCTION -> EnvironmentConfig.production(enableLogging = true)
                                BabylAIEnvironment.STAGING -> EnvironmentConfig.staging()
                                BabylAIEnvironment.DEVELOPMENT -> EnvironmentConfig.development()
                            }
                            
                            BabylAI.shared.initialize(
                                context = context,
                                config = config,
                                locale = if (isArabic) BabylAILocale.ARABIC else BabylAILocale.ENGLISH,
                                themeConfig = ThemeConfig(
                                    primaryColor = "#4A6741".toColorInt(), // Elegant forest green for light theme
                                    secondaryColor = "#D4AF37".toColorInt(), // Sophisticated gold for light theme
                                    primaryColorDark = "#81C784".toColorInt(), // Soft sage green for dark theme
                                    secondaryColorDark = "#F9D71C".toColorInt(), // Warm amber for dark theme
                                    headerLogoRes = R.drawable.ngrok_light // Using custom ngrok logo
                                )
                            )
                            
                            BabylAI.shared.setTokenCallback {
                                getTokenExample(apiKey = apiKey, tenantId = tenantId, baseUrl = config.baseUrl)
                            }
                            BabylAI.shared.setOnErrorReceived { error: BabylAIError ->
                                println("❌ SDK Error [${error.errorCode}]: ${error.userFriendlyMessage}")
                            }
                            
                            onInitializationChange(true)
                            onErrorChange(null)
                            println("✅ SDK initialized successfully")
                        } catch (e: Exception) {
                            onErrorChange(e.message)
                            println("❌ SDK initialization failed: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Initialize SDK")
            }
            
            // SDK Action Buttons (only show when initialized)
            if (isInitialized) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Text(
                    text = "SDK Actions",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Button(
                    onClick = { 
                        navController.navigate("chat")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Launch Help Screen & Chat")
                }
                
                Button(
                    onClick = { 
                        navController.navigate("activeChat")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Launch Active Chat")
                }
                
                // Add debugging option
                OutlinedButton(
                    onClick = { 
                        navController.navigate("chatDirect")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🐛 Debug: Direct SDK (No Token Validation)")
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Status Information
            Text(
                text = "Status",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Text(
                text = if (isInitialized) "✅ SDK Initialized" else "⚠️ SDK Not Initialized",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isInitialized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Single-line credential input with a "Paste from clipboard" trailing button.
 */
@Composable
private fun CredentialField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val context = LocalContext.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = {
                readClipboardText(context)?.let(onValueChange)
            }) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Paste $label"
                )
            }
        },
        modifier = modifier
    )
}

/**
 * Reads the first item of the system clipboard as trimmed text, or null if empty.
 */
private fun readClipboardText(context: Context): String? {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = clipboard?.primaryClip ?: return null
    if (clip.itemCount == 0) return null
    return clip.getItemAt(0).coerceToText(context).toString().trim().ifEmpty { null }
}

/**
 * BabylAI Chat Screen that properly displays the SDK without top bar
 */
@Composable
fun BabylAIChatScreen(
    navController: NavController,
    screenId: String,
    isDarkMode: Boolean
) {
    // Handle back navigation using system back gesture
    androidx.activity.compose.BackHandler {
        navController.popBackStack()
    }
    
    // No Scaffold or TopAppBar - let the SDK handle full-screen experience
    // Get the BabylAI viewer composable
    val currentTheme = if (isDarkMode) BabylAITheme.DARK else BabylAITheme.LIGHT
    println("🎨 BabylAIChatScreen - isDarkMode: $isDarkMode, theme: $currentTheme")
    
    val viewerComposable = BabylAI.shared.viewer(
        theme = currentTheme,
        isDirect = false,
        screenId = screenId,
        onMessageReceived = { message ->
            println("📨 Received message: $message")
        },
        onDismiss ={
            // Handle SDK back navigation - go back to home screen
            navController.popBackStack()
        }
    )

    // Display the SDK view - it will take full screen
//    viewerComposable()
}

/**
 * Direct BabylAI Chat Screen that bypasses token validation for debugging
 */
@Composable
fun BabylAIDirectChatScreen(
    navController: NavController,
    screenId: String,
    isDarkMode: Boolean
) {
    // Handle back navigation using system back gesture
    androidx.activity.compose.BackHandler {
        navController.popBackStack()
    }

    // No Scaffold or TopAppBar - let the SDK handle full-screen experience
    // Get the direct SDK content without token validation
    val directContent = BabylAI.shared.viewer(
        theme = if (isDarkMode) BabylAITheme.DARK else BabylAITheme.LIGHT,
        isDirect = true,
        screenId = screenId,
        onMessageReceived = { message ->
            println("📨 [Direct] Received message: $message")
        },
        onDismiss = {
            // Handle SDK back navigation - go back to home screen
            navController.popBackStack()
        }
    )

    // Display the SDK view - it will take full screen
//    directContent()
}

/**
 * Token fetching function (mirrors iOS implementation)
 */


@Preview(showBackground = true)
@Composable
fun BabylAIExampleViewPreview() {
    MaterialTheme {
        BabylAIExampleView()
    }
}
