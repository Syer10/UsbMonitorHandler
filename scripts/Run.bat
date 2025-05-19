@echo off
rem Configuration file path
set CONFIG_FILE=config.ini

:loadConfig
if exist "%CONFIG_FILE%" (
    for /f "usebackq tokens=1,2 delims==" %%A in (`type "%CONFIG_FILE%"`) do (
        if /I "%%A"=="USB_SERIAL" set "USB_SERIAL=%%B"
        if /I "%%A"=="MAIN_MONITOR_ID" set "MAIN_MONITOR_ID=%%B"
        if /I "%%A"=="SECONDARY_MONITOR_ID" set "SECONDARY_MONITOR_ID=%%B"
        if /I "%%A"=="MULTI_MONITOR_TOOL" set "MULTI_MONITOR_TOOL=%%B"
    )
) else goto :promptNew

:promptUseDefaults
echo.
echo Current configuration (if any):
echo    USB Serial: %USB_SERIAL%
echo    Main Monitor ID: %MAIN_MONITOR_ID%
echo    Secondary Monitor ID: %SECONDARY_MONITOR_ID%
echo    Multi Monitor Tool: %MULTI_MONITOR_TOOL%
echo.
set /p USE_DEFAULTS=Use these values? (Y/N):
if /I "%USE_DEFAULTS%"=="Y" goto :run

:promptNew
rem Prompt the user for each parameter
set /p USB_SERIAL=Enter USB serial (e.g., 1111111111):
set /p MAIN_MONITOR_ID=Enter main monitor ID (e.g., DELD0G5):
set /p SECONDARY_MONITOR_ID=Enter secondary monitor ID (e.g., DEL27Y8):
set /p MULTI_MONITOR_TOOL=Enter path to MultiMonitorTool (include quotes if needed, e.g., "D:\Programs\MultiMonitorTool.exe"):

echo.
echo Saving configuration to %CONFIG_FILE%...
echo USB_SERIAL=%USB_SERIAL%>"%CONFIG_FILE%"
echo MAIN_MONITOR_ID=%MAIN_MONITOR_ID%>>"%CONFIG_FILE%"
echo SECONDARY_MONITOR_ID=%SECONDARY_MONITOR_ID%>>"%CONFIG_FILE%"
echo MULTI_MONITOR_TOOL=%MULTI_MONITOR_TOOL%>>"%CONFIG_FILE%"

:run
echo.
echo Running UsbMonitorHandler with the following parameters:
echo    USB Serial: %USB_SERIAL%
echo    Main Monitor ID: %MAIN_MONITOR_ID%
echo    Secondary Monitor ID: %SECONDARY_MONITOR_ID%
echo    Multi Monitor Tool: %MULTI_MONITOR_TOOL%
echo.

".\jre\bin\java.exe" --enable-native-access=ALL-UNNAMED -jar UsbMonitorHandler.jar --usb-serial %USB_SERIAL% --main-monitor-id %MAIN_MONITOR_ID% --secondary-monitor-id %SECONDARY_MONITOR_ID% --multi-monitor-tool %MULTI_MONITOR_TOOL%

echo.
echo Process completed. Press any key to exit.
pause >nul
