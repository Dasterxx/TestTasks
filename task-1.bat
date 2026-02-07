@echo off
rem Определяем директорию где находится bat файл
set "PROJECT_DIR=%~dp0"

rem Переходим в корень проекта (где лежит bat)
cd /d "%PROJECT_DIR%"

echo ==========================================
echo   WaterSortApplication - Build & Run
echo ==========================================
echo.

echo [1/3] Cleaning old build...
if exist out rmdir /s /q out
mkdir out
if errorlevel 1 (
    echo ERROR: Cannot create out directory
    pause
    exit /b 1
)

echo [2/3] Compiling Java sources...
javac -d out -sourcepath src\main\java src/main/java/doczilla/com/task1/WaterSortApplication.java

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    echo Check that JDK 21+ is installed and in PATH
    pause
    exit /b 1
)

echo [3/3] Starting server...
echo.
echo ==========================================
java -cp out doczilla.com.task1.WaterSortApplication
echo.
echo ==========================================
pause