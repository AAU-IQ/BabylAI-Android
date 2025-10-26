# Default: no device name
DEVICE ?=

# Automatically add -s flag only if DEVICE is set
ADB := adb $(if $(DEVICE),-s $(DEVICE))

# Switch to gesture navigation
gesture:
	@echo "Switching to gesture navigation..."
	$(ADB) shell cmd overlay enable --user 0 com.android.internal.systemui.navbar.gestural
	$(ADB) shell cmd overlay disable --user 0 com.android.internal.systemui.navbar.threebutton
	$(ADB) shell killall com.android.systemui
	@echo "✅ Gesture navigation enabled."

# Switch to 3-button navigation
buttons:
	@echo "Switching to 3-button navigation..."
	$(ADB) shell cmd overlay enable --user 0 com.android.internal.systemui.navbar.threebutton
	$(ADB) shell cmd overlay disable --user 0 com.android.internal.systemui.navbar.gestural
	$(ADB) shell killall com.android.systemui
	@echo "✅ 3-button navigation enabled."

# Show connected devices
devices:
	@adb devices