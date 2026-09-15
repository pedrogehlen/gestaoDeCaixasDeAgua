param(
    [switch]$SomenteCompilar,
    [string]$JavaHome = $env:JAVA_HOME,
    [string]$KotlinHome = 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.1\plugins\Kotlin\kotlinc',
    [string]$Driver = 'D:\drivePostgres\postgresql-42.7.13.jar'
)
$ErrorActionPreference = 'Stop'
if (-not $JavaHome) { $JavaHome = Join-Path $env:USERPROFILE '.jdks\ms-21.0.10' }
$java = Join-Path $JavaHome 'bin\java.exe'
$compilador = Join-Path $KotlinHome 'bin\kotlinc.bat'
if (-not (Test-Path -LiteralPath $java)) { throw 'JDK não encontrado. Informe -JavaHome com a pasta do JDK.' }
if (-not (Test-Path -LiteralPath $compilador)) { throw 'Kotlin não encontrado. Informe -KotlinHome com a pasta do compilador.' }
if (-not (Test-Path -LiteralPath $Driver)) { throw 'Driver JDBC não encontrado. Informe -Driver com o arquivo postgresql.jar.' }
$env:JAVA_HOME = $JavaHome
$saida = Join-Path $PSScriptRoot '.execucao'
New-Item -ItemType Directory -Path $saida -Force | Out-Null
$jar = Join-Path $saida 'aplicacao.jar'
# Compila os fontes atuais, sem utilizar os arquivos antigos de out.
& $compilador (Join-Path $PSScriptRoot 'src') -include-runtime -d $jar
if ($LASTEXITCODE -ne 0) { throw 'A compilação falhou.' }
Write-Host 'Compilação concluída.'
if (-not $SomenteCompilar) {
    & $java '-Dfile.encoding=UTF-8' '-Dstdout.encoding=UTF-8' '-Dstderr.encoding=UTF-8' -cp "$jar;$Driver" MainKt
    if ($LASTEXITCODE -ne 0) { throw 'A aplicação foi encerrada com erro.' }
}
