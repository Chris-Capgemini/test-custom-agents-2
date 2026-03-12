# GenInsights File Discovery Script (PowerShell)
# Discovers and categorizes all relevant files for code analysis

param(
    [string]$RootPath = "."
)

# Output file
$OutputFile = ".geninsights/discovered_files.json"

# Create output directory
$OutputDir = Split-Path $OutputFile -Parent
if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
}

# Exclusion patterns
$ExcludeDirs = @(
    "node_modules", ".git", ".svn", ".hg", "dist", "build", "target", "out", 
    "bin", "obj", ".idea", ".vscode", ".vs", "__pycache__", ".pytest_cache",
    ".mypy_cache", ".tox", ".eggs", "coverage", ".nyc_output", ".next", 
    ".nuxt", "vendor", "Pods", ".gradle", ".m2", "logs", "tmp", "temp", "cache", ".cache"
)

# Test file patterns
$TestPatterns = @("*Test.java", "*Tests.java", "*TestCase.java", "*_test.py", "test_*.py", 
                  "*.test.js", "*.spec.js", "*.test.ts", "*.spec.ts", "*_test.go", "*Test.cs", "*Tests.cs")
$TestDirs = @("test", "tests", "spec", "__tests__")

Write-Host "Discovering files in: $RootPath"
Write-Host "========================================"

# Get all files excluding certain directories
function Get-FilteredFiles {
    param([string]$Path, [string[]]$Extensions)
    
    Get-ChildItem -Path $Path -Recurse -File -ErrorAction SilentlyContinue | 
        Where-Object { 
            $file = $_
            $exclude = $false
            foreach ($dir in $ExcludeDirs) {
                if ($file.FullName -like "*\$dir\*" -or $file.FullName -like "*/$dir/*") {
                    $exclude = $true
                    break
                }
            }
            -not $exclude -and ($Extensions.Count -eq 0 -or $Extensions -contains $file.Extension.ToLower())
        }
}

function Test-IsTestFile {
    param([System.IO.FileInfo]$File)
    
    # Check directory
    foreach ($testDir in $TestDirs) {
        if ($File.FullName -like "*\$testDir\*" -or $File.FullName -like "*/$testDir/*") {
            return $true
        }
    }
    # Check filename patterns
    foreach ($pattern in $TestPatterns) {
        if ($File.Name -like $pattern) {
            return $true
        }
    }
    return $false
}

# Source code extensions
$SourceExtensions = @(".java", ".py", ".js", ".jsx", ".mjs", ".ts", ".tsx", ".cs", 
                      ".c", ".cpp", ".h", ".hpp", ".go", ".rs", ".rb", ".php", 
                      ".kt", ".kts", ".scala", ".swift")

# Config file names
$ConfigFileNames = @("pom.xml", "build.gradle", "build.gradle.kts", "settings.gradle",
                     "package.json", "package-lock.json", "yarn.lock",
                     "requirements.txt", "setup.py", "pyproject.toml", "Pipfile",
                     "Gemfile", "Cargo.toml", "go.mod", "go.sum",
                     "CMakeLists.txt", "Makefile", "Dockerfile",
                     "tsconfig.json", "jsconfig.json", "webpack.config.js")

$ConfigPatterns = @("application*.properties", "application*.yml", "application*.yaml",
                    "bootstrap*.properties", "bootstrap*.yml", "bootstrap*.yaml",
                    "config*.properties", "config*.yml", "config*.yaml",
                    "appsettings*.json", "web.config", "app.config",
                    "docker-compose*.yml", "docker-compose*.yaml",
                    ".env", ".env.*", ".eslintrc*", ".prettierrc*")

Write-Host "Finding source files..."
$AllSourceFiles = Get-FilteredFiles -Path $RootPath -Extensions $SourceExtensions
$SourceFiles = $AllSourceFiles | Where-Object { -not (Test-IsTestFile $_) }
$TestFiles = $AllSourceFiles | Where-Object { Test-IsTestFile $_ }

Write-Host "Finding config files..."
$ConfigFiles = Get-FilteredFiles -Path $RootPath -Extensions @() | Where-Object {
    $file = $_
    $isConfig = $false
    
    # Check exact names
    if ($ConfigFileNames -contains $file.Name) {
        $isConfig = $true
    }
    
    # Check patterns
    if (-not $isConfig) {
        foreach ($pattern in $ConfigPatterns) {
            if ($file.Name -like $pattern) {
                $isConfig = $true
                break
            }
        }
    }
    
    $isConfig
}

Write-Host "Finding database files..."
$DbFiles = Get-FilteredFiles -Path $RootPath -Extensions @(".sql") 
$DbFiles += Get-FilteredFiles -Path $RootPath -Extensions @() | Where-Object {
    $_.Name -in @("persistence.xml", "hibernate.cfg.xml", "database.yml", "ormconfig.json")
}
# Entity files
$EntityFiles = Get-FilteredFiles -Path $RootPath -Extensions $SourceExtensions | Where-Object {
    $_.FullName -like "*\entity\*" -or $_.FullName -like "*\entities\*" -or 
    $_.FullName -like "*\model\*" -or $_.FullName -like "*\models\*" -or 
    $_.FullName -like "*\domain\*"
}
$DbFiles = @($DbFiles) + @($EntityFiles) | Select-Object -Unique

Write-Host "Finding documentation files..."
$DocFiles = Get-FilteredFiles -Path $RootPath -Extensions @(".md", ".rst", ".adoc") 
$DocFiles += Get-FilteredFiles -Path $RootPath -Extensions @() | Where-Object {
    $_.Name -like "README*" -or $_.Name -like "CHANGELOG*" -or $_.Name -like "CONTRIBUTING*" -or $_.Name -like "LICENSE*"
}

# Detect languages
$Languages = @()
if ($SourceFiles | Where-Object { $_.Extension -eq ".java" }) { $Languages += "java" }
if ($SourceFiles | Where-Object { $_.Extension -eq ".py" }) { $Languages += "python" }
if ($SourceFiles | Where-Object { $_.Extension -in @(".js", ".jsx", ".mjs") }) { $Languages += "javascript" }
if ($SourceFiles | Where-Object { $_.Extension -in @(".ts", ".tsx") }) { $Languages += "typescript" }
if ($SourceFiles | Where-Object { $_.Extension -eq ".cs" }) { $Languages += "csharp" }
if ($SourceFiles | Where-Object { $_.Extension -in @(".c", ".cpp", ".h", ".hpp") }) { $Languages += "cpp" }
if ($SourceFiles | Where-Object { $_.Extension -eq ".go" }) { $Languages += "go" }
if ($SourceFiles | Where-Object { $_.Extension -eq ".rs" }) { $Languages += "rust" }
if ($SourceFiles | Where-Object { $_.Extension -eq ".rb" }) { $Languages += "ruby" }
if ($SourceFiles | Where-Object { $_.Extension -eq ".php" }) { $Languages += "php" }
if ($SourceFiles | Where-Object { $_.Extension -in @(".kt", ".kts") }) { $Languages += "kotlin" }
if ($SourceFiles | Where-Object { $_.Extension -eq ".scala" }) { $Languages += "scala" }
if ($SourceFiles | Where-Object { $_.Extension -eq ".swift" }) { $Languages += "swift" }

# Build output
$Output = @{
    discovery_timestamp = (Get-Date -Format "o")
    root_path = (Resolve-Path $RootPath).Path
    summary = @{
        total_files = @($SourceFiles).Count + @($ConfigFiles).Count + @($DbFiles).Count + @($TestFiles).Count + @($DocFiles).Count
        source_files = @($SourceFiles).Count
        config_files = @($ConfigFiles).Count
        database_files = @($DbFiles).Count
        test_files = @($TestFiles).Count
        doc_files = @($DocFiles).Count
    }
    languages_detected = $Languages
    files = @{
        source = @($SourceFiles | Select-Object -First 500 | ForEach-Object { $_.FullName })
        config = @($ConfigFiles | ForEach-Object { $_.FullName })
        database = @($DbFiles | ForEach-Object { $_.FullName })
        test = @($TestFiles | Select-Object -First 200 | ForEach-Object { $_.FullName })
        documentation = @($DocFiles | ForEach-Object { $_.FullName })
    }
}

# Save to JSON
$Output | ConvertTo-Json -Depth 5 | Set-Content -Path $OutputFile -Encoding UTF8

Write-Host "========================================"
Write-Host "Discovery complete!"
Write-Host ""
Write-Host "Summary:"
Write-Host "  Source files:   $(@($SourceFiles).Count)"
Write-Host "  Config files:   $(@($ConfigFiles).Count)"
Write-Host "  Database files: $(@($DbFiles).Count)"
Write-Host "  Test files:     $(@($TestFiles).Count)"
Write-Host "  Doc files:      $(@($DocFiles).Count)"
Write-Host "  -----------------------"
Write-Host "  Total:          $($Output.summary.total_files)"
Write-Host ""
Write-Host "Languages detected: $($Languages -join ', ')"
Write-Host ""
Write-Host "Results saved to: $OutputFile"
