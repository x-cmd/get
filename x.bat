@echo off
setlocal
REM Copyright 2022-? Li Junhao (l@x-cmd.com). Licensed under the GNU AFFERO GENERAL PUBLIC LICENSE, Version 3.

@echo Check if Git-For-Windows is installed

set gitbash=git-bash
where.exe "%gitbash%" >nul 2>&1
if %errorlevel% == 0        goto :start-git-bash

set gitbash=%USERPROFILE%\.x-cmd.root\data\git-for-windows\bin\bash.exe
if EXIST "%gitbash%"        goto :start-git-bash

set gitbash=%PROGRAMFILES%\Git\bin\bash.exe
if EXIST "%gitbash%"        goto :start-git-bash

mkdir %USERPROFILE%\.x-cmd.root\data\git-for-windows
set gitbash=%USERPROFILE%\.x-cmd.root\data\git-for-windows\bin\bash.exe
C:
cd %USERPROFILE%\.x-cmd.root\data\git-for-windows
curl -L -o git-for-windows.7z.exe https://github.com/x-cmd-build/git-for-windows/releases/download/v2.41.0/git-for-windows.7z.exe
git-for-windows.7z.exe -y
robocopy PortableGit %cd% /E /MOVE /np /nfl /ndl /njh /njs
if EXIST "%gitbash%"        goto :start-git-bash

@echo Fail to install git-for-windows. Exit.
exit 1

:start-git-bash
echo start git-bash "%gitbash%"

set x_str="if [ -f $HOME/.x-cmd.root/X ]; then . $HOME/.x-cmd.root/X; else eval \"$(curl https://get.x-cmd.com)\"; fi"
findstr /c:%x_str% %USERPROFILE%\.bashrc >nul 2>&1
if %errorlevel% neq 0       echo if [ -f $HOME/.x-cmd.root/X ]; then . $HOME/.x-cmd.root/X; else eval "$(curl https://get.x-cmd.com)"; fi >>%USERPROFILE%\.bashrc

"%gitbash%"
