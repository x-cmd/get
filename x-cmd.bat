@echo off
setlocal
REM Copyright 2022-? Li Junhao (l@x-cmd.com). Licensed under the GNU AFFERO GENERAL PUBLIC LICENSE, Version 3.

echo INFO: Check if Git-For-Windows is installed

set gitbash=%USERPROFILE%\.x-cmd.root\data\git-for-windows\bin\bash.exe
if EXIST "%gitbash%"        goto :start-git-bash

set gitbash=%PROGRAMFILES%\Git\bin\bash.exe
if EXIST "%gitbash%"        goto :start-git-bash

set gitbash=%USERPROFILE%\scoop\apps\git\current\bin\bash.exe
if EXIST "%gitbash%"        goto :start-git-bash

set gitbash=%USERPROFILE%\AppData\Local\Programs\Git\bin\bash.exe
if EXIST "%gitbash%"        goto :start-git-bash

set gitbash=git-bash
where.exe "%gitbash%" >nul 2>&1
if %errorlevel% == 0        goto :start-git-bash

if EXIST  %USERPROFILE%\.x-cmd.root\data\git-for-windows goto :init
echo INFO: create directory to place git-for-windows -- %USERPROFILE%\.x-cmd.root\data\git-for-windows
mkdir %USERPROFILE%\.x-cmd.root\data\git-for-windows

:init
echo INFO: cd into %USERPROFILE%\.x-cmd.root\data\git-for-windows
set gitbash=%USERPROFILE%\.x-cmd.root\data\git-for-windows\bin\bash.exe
cd /d %USERPROFILE%\.x-cmd.root\data\git-for-windows

echo .
echo --------------------------------------------------------------------------------------------------------
echo STEP 1: Download git-for-windows to %USERPROFILE%\.x-cmd.root\data\git-for-windows
echo --------------------------------------------------------------------------------------------------------
echo .
curl -L -o git-for-windows.7z.exe https://github.com/x-cmd-build/git-for-windows/releases/download/v2.47.0/git-for-windows.7z.exe
if %errorlevel% equ 0  goto :install
echo ERROR: Download failure. Press any key to exit.
pause
exit 1

:install
echo .
echo --------------------------------------------------------------------------------------------------------
echo STEP 2: Install git-for-windows. It might take a few minutes. Don't close this window.
echo --------------------------------------------------------------------------------------------------------
echo .
git-for-windows.7z.exe -y

if %errorlevel% equ 0  goto :robocopy

@echo ERROR: Installation failure. Press any key to exit.
pause
exit 1

:robocopy
echo .
echo --------------------------------------------------------------------------------------------------------
echo STEP 3: Using robocopy to relocate the git-for-windows folder
echo --------------------------------------------------------------------------------------------------------
echo .
robocopy PortableGit %cd% /E /MOVE /np /nfl /ndl /njh /njs
if EXIST "%gitbash%"        goto :start-git-bash

echo ERROR: Fail to install git-for-windows. Press any key to exit.
exit 1


:start-git-bash
echo INFO: start git-bash "%gitbash%"

if NOT EXIST "%USERPROFILE%\.x-cmd.root\X" (
    echo INFO: x-cmd init
    echo [ -f "$HOME/.x-cmd.root/X" ] ^|^| eval "$(curl https://get.x-cmd.com)" >%USERPROFILE%\.x-cmd.init.bash
    "%gitbash%" %USERPROFILE%\.x-cmd.init.bash
    del %USERPROFILE%\.x-cmd.init.bash
)

"%gitbash%" %*
