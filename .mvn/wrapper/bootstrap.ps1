$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$version = '3.9.11'
$distribution = Join-Path $PSScriptRoot ('dists\apache-maven-' + $version)
$maven = Join-Path $distribution 'bin\mvn.cmd'
if (-not (Test-Path $maven)) {
    $zip = Join-Path $env:TEMP ('apache-maven-' + $version + '-bin.zip')
    $url = 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/' + $version + '/apache-maven-' + $version + '-bin.zip'
    New-Item -ItemType Directory -Force -Path (Join-Path $PSScriptRoot 'dists') | Out-Null
    Invoke-WebRequest -Uri $url -OutFile $zip
    Expand-Archive -Path $zip -DestinationPath (Join-Path $PSScriptRoot 'dists') -Force
    Remove-Item $zip
}
Push-Location $root
try {
    & $maven @args
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}
