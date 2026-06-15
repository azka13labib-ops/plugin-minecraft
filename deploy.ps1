# deploy.ps1
# Script deployment otomatis untuk plugin Minecraft (demo)

# Hentikan eksekusi jika terjadi error kritis
$ErrorActionPreference = "Stop"

# Jalankan Maven build (clean package) menggunakan Maven lokal
Write-Host "Memulai proses build dengan Maven..." -ForegroundColor Cyan
& "c:\ngodink\heker\java\maven\apache-maven-3.9.6\bin\mvn.cmd" -f "$PSScriptRoot\pom.xml" clean package

# Periksa apakah proses build sukses
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build Maven GAGAL! Deployment dibatalkan." -ForegroundColor Red
    Exit $LASTEXITCODE
}

# =========================================================================
# UBAH VARIABEL DI BAWAH INI SESUAI DENGAN FOLDER PLUGINS SERVER MINECRAFT KAMU
# Contoh: "C:\MinecraftServer\plugins"
# =========================================================================
$serverPluginPath = "c:\ngodink\heker\java\MinecraftServer\plugins"

# Cek apakah folder plugins server valid, jika tidak ada buat otomatis
if (!(Test-Path -Path $serverPluginPath)) {
    Write-Host "Folder tujuan '$serverPluginPath' tidak ditemukan. Membuat folder..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $serverPluginPath -Force | Out-Null
}

# Menyalin file .jar hasil build ke folder plugins secara paksa (overwrite)
$sourceJar = Join-Path $PSScriptRoot "target\demo-1.0.jar"
Write-Host "Menyalin $sourceJar ke $serverPluginPath ..." -ForegroundColor Cyan

Copy-Item -Path $sourceJar -Destination $serverPluginPath -Force

Write-Host "`nDeploy Sukses! Silakan nyalakan/restart server Minecraft!" -ForegroundColor Green
