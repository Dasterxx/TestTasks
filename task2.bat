@echo off
setlocal enabledelayedexpansion

rem Директория проекта = директория bat файла
cd /d "%~dp0"

echo ==========================================
echo   File Exchange Service
echo ==========================================
echo.

rem Проверяем Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found!
    echo Please install JDK 21+ and add to PATH
    echo.
    pause
    exit /b 1
)

echo Java version:
java -version
echo.

rem Очистка
echo [1/4] Cleaning...
if exist out rmdir /s /q out 2>nul
mkdir out

rem Компиляция
echo [2/4] Compiling...
javac -d out -sourcepath src\main\java src\main\java\doczilla\com\task2\fileexchange\FileExchangeApplication.java 2>compile_errors.txt

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    echo.
    type compile_errors.txt
    del compile_errors.txt
    pause
    exit /b 1
)

del compile_errors.txt

rem Создаём директории (на всякий случай)
echo [3/4] Preparing directories...
if not exist uploads mkdir uploads
if not exist data mkdir data

rem Запуск
echo [4/4] Starting server...
echo.
echo ==========================================
echo Server running at http://localhost:8080
echo frontend running at http://localhost:3030
echo Press Ctrl+C to stop
echo ==========================================
echo.

java -cp out doczilla.com.task2.fileexchange.FileExchangeApplication

echo.
echo Server stopped
pause