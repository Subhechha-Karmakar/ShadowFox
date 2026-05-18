<# ─────────────────────────────────────────────────────────────────
   Student Information System — Build & Run Script (PowerShell)
   Downloads iText 7 JARs (if missing), compiles, and launches.
   ───────────────────────────────────────────────────────────────── #>

$ErrorActionPreference = "Stop"
$root   = $PSScriptRoot
$libDir = Join-Path $root "lib"
$outDir = Join-Path $root "out"
$srcDir = Join-Path $root "src"

# ── iText 7 dependencies (kernel + io + layout + commons + slf4j) ──
$mavenBase = "https://repo1.maven.org/maven2"
$deps = @(
    @{ g="com/itextpdf";       a="kernel";          v="8.0.5" },
    @{ g="com/itextpdf";       a="io";              v="8.0.5" },
    @{ g="com/itextpdf";       a="layout";          v="8.0.5" },
    @{ g="com/itextpdf";       a="commons";         v="8.0.5" },
    @{ g="org/slf4j";          a="slf4j-api";       v="2.0.13" },
    @{ g="org/slf4j";          a="slf4j-simple";    v="2.0.13" }
)

# ── Step 1: Download dependencies ──────────────────────────────────
if (!(Test-Path $libDir)) { New-Item -ItemType Directory -Path $libDir | Out-Null }

foreach ($dep in $deps) {
    $jarName = "$($dep.a)-$($dep.v).jar"
    $jarPath = Join-Path $libDir $jarName
    if (!(Test-Path $jarPath)) {
        $url = "$mavenBase/$($dep.g)/$($dep.a)/$($dep.v)/$jarName"
        Write-Host "  Downloading $jarName ..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $url -OutFile $jarPath -UseBasicParsing
    }
}
Write-Host "[OK] All dependencies ready." -ForegroundColor Green

# ── Step 2: Compile ────────────────────────────────────────────────
if (Test-Path $outDir) { Remove-Item -Recurse -Force $outDir }
New-Item -ItemType Directory -Path $outDir | Out-Null

$classpath = (Get-ChildItem -Path $libDir -Filter "*.jar" | ForEach-Object { $_.FullName }) -join ";"
$sources   = Get-ChildItem -Path $srcDir -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }

Write-Host "  Compiling $($sources.Count) source files ..." -ForegroundColor Cyan
$sourceList = Join-Path $root "sources.txt"
$sources | Out-File -FilePath $sourceList -Encoding UTF8

javac -encoding UTF-8 -cp "$classpath" -d "$outDir" "@$sourceList" 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "[FAIL] Compilation failed." -ForegroundColor Red
    exit 1
}
Remove-Item $sourceList -ErrorAction SilentlyContinue
Write-Host "[OK] Compilation succeeded." -ForegroundColor Green

# ── Step 3: Run ────────────────────────────────────────────────────
Write-Host "  Launching Student Information System ..." -ForegroundColor Yellow
$runCp = "$outDir;$classpath"
java -cp "$runCp" com.studentmgmt.App
