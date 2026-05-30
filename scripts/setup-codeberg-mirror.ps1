# Wrapper for the global Codeberg mirror setup script.
# Usage: .\scripts\setup-codeberg-mirror.ps1

param(
    [string]$RepoPath = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [switch]$WhatIf
)

$globalScript = Join-Path $env:USERPROFILE ".git-hooks\setup-codeberg-mirror.ps1"
if (-not (Test-Path $globalScript)) {
    throw "Global mirror script not found at $globalScript"
}

$params = @{
    RepoPath         = $RepoPath
    InstallWorkflow  = $true
}
if ($WhatIf) { $params.WhatIf = $true }

& $globalScript @params
