@echo off
cd /d "%~dp0"

echo ==========================================
echo   Weather-Application
echo ==========================================
echo.

if not exist "build\libs\weather-app.jar" (
    echo Building...
    call gradlew.bat jar --quiet
)

echo Starting server...
echo URL: http://localhost:8080/weather?city=Moscow
echo.

java -jar build\libs\weather-app.jar

pause