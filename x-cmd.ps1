function ___x_cmd____rcpwsh_setup_pwsh(){
    $xbatfile = "$HOME\x-cmd.bat"
    if (-not (Test-Path $xbatfile -PathType Leaf)) {
        $xbaturl = "https://get.x-cmd.com/x-cmd.bat"
        Write-Host "- I|x: Download the x-cmd x.bat script file from '$xbaturl' to '$xbatfile'"
        Invoke-WebRequest -Uri "$xbaturl" -OutFile "$xbatfile"
    }
    & $xbatfile "$HOME\.x-cmd.root\bin\x" pwsh --setup
}

___x_cmd____rcpwsh_setup_pwsh
$___X_CMD_PCPWSH_RCFILE = "$HOME\.x-cmd.root\local\data\pwsh\_index.ps1"
if (Test-Path $___X_CMD_PCPWSH_RCFILE) {
    Set-ExecutionPolicy Bypass -Scope Process
    . $___X_CMD_PCPWSH_RCFILE
    Write-Host "- I|x: Successfully loaded x-cmd"
} else {
    Write-Host "- E|x: Not found pwsh rcfile -> $___X_CMD_PCPWSH_RCFILE"
}
