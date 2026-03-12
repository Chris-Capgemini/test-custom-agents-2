#!/bin/bash
# GenInsights File Discovery Script
# Discovers and categorizes all relevant files for code analysis

set -e

# Default to current directory if no path provided
ROOT_PATH="${1:-.}"

# Output file
OUTPUT_FILE=".geninsights/discovered_files.json"

# Create output directory
mkdir -p "$(dirname "$OUTPUT_FILE")"

# Exclusion patterns
EXCLUDE_DIRS="node_modules|\.git|\.svn|\.hg|dist|build|target|out|bin|obj|\.idea|\.vscode|\.vs|__pycache__|\.pytest_cache|\.mypy_cache|\.tox|\.eggs|coverage|\.nyc_output|\.next|\.nuxt|vendor|Pods|\.gradle|\.m2|logs|tmp|temp|cache|\.cache"

# Source code extensions
SOURCE_EXTS="java|py|js|jsx|mjs|ts|tsx|cs|c|cpp|h|hpp|go|rs|rb|php|kt|kts|scala|swift"

# Config file patterns
CONFIG_PATTERNS="pom\.xml|build\.gradle|settings\.gradle|package\.json|requirements\.txt|setup\.py|pyproject\.toml|Pipfile|Gemfile|Cargo\.toml|go\.mod|CMakeLists\.txt|Makefile|Dockerfile|docker-compose.*\.yml|tsconfig\.json|\.eslintrc.*|webpack\.config\.js|vite\.config\."

# Application config patterns  
APP_CONFIG_PATTERNS="application.*\.(properties|yml|yaml)|bootstrap.*\.(properties|yml|yaml)|config.*\.(properties|yml|yaml)|appsettings.*\.json|web\.config|app\.config|\.env"

# Count files
echo "Discovering files in: $ROOT_PATH"
echo "========================================"

# Find source files
echo "Finding source files..."
SOURCE_FILES=$(find "$ROOT_PATH" -type f -regextype posix-extended -regex ".*\.($SOURCE_EXTS)$" | grep -vE "($EXCLUDE_DIRS)" | grep -vE "(test|tests|spec|__tests__|Test\.|Tests\.|_test\.|\.test\.|\.spec\.)" 2>/dev/null || true)
SOURCE_COUNT=$(echo "$SOURCE_FILES" | grep -c "." 2>/dev/null || echo "0")

# Find test files
echo "Finding test files..."
TEST_FILES=$(find "$ROOT_PATH" -type f -regextype posix-extended -regex ".*\.($SOURCE_EXTS)$" | grep -vE "($EXCLUDE_DIRS)" | grep -E "(test|tests|spec|__tests__|Test\.|Tests\.|_test\.|\.test\.|\.spec\.)" 2>/dev/null || true)
TEST_COUNT=$(echo "$TEST_FILES" | grep -c "." 2>/dev/null || echo "0")

# Find config files
echo "Finding config files..."
CONFIG_FILES=$(find "$ROOT_PATH" -type f -regextype posix-extended -regex ".*($CONFIG_PATTERNS).*" | grep -vE "($EXCLUDE_DIRS)" 2>/dev/null || true)
APP_CONFIG_FILES=$(find "$ROOT_PATH" -type f -regextype posix-extended -regex ".*($APP_CONFIG_PATTERNS)" | grep -vE "($EXCLUDE_DIRS)" 2>/dev/null || true)
ALL_CONFIG_FILES=$(echo -e "$CONFIG_FILES\n$APP_CONFIG_FILES" | sort -u | grep "." 2>/dev/null || true)
CONFIG_COUNT=$(echo "$ALL_CONFIG_FILES" | grep -c "." 2>/dev/null || echo "0")

# Find SQL/database files
echo "Finding database files..."
DB_FILES=$(find "$ROOT_PATH" -type f \( -name "*.sql" -o -name "persistence.xml" -o -name "hibernate.cfg.xml" -o -name "database.yml" -o -name "ormconfig.json" \) | grep -vE "($EXCLUDE_DIRS)" 2>/dev/null || true)
# Also find entity files
ENTITY_FILES=$(find "$ROOT_PATH" -type f -regextype posix-extended -regex ".*\.($SOURCE_EXTS)$" -path "*/entity/*" -o -path "*/entities/*" -o -path "*/model/*" -o -path "*/models/*" -o -path "*/domain/*" | grep -vE "($EXCLUDE_DIRS)" 2>/dev/null || true)
ALL_DB_FILES=$(echo -e "$DB_FILES\n$ENTITY_FILES" | sort -u | grep "." 2>/dev/null || true)
DB_COUNT=$(echo "$ALL_DB_FILES" | grep -c "." 2>/dev/null || echo "0")

# Find documentation files
echo "Finding documentation files..."
DOC_FILES=$(find "$ROOT_PATH" -type f \( -name "README*" -o -name "CHANGELOG*" -o -name "CONTRIBUTING*" -o -name "*.md" -o -name "*.rst" -o -name "*.adoc" \) | grep -vE "($EXCLUDE_DIRS)" 2>/dev/null || true)
DOC_COUNT=$(echo "$DOC_FILES" | grep -c "." 2>/dev/null || echo "0")

# Detect languages
echo "Detecting languages..."
LANGUAGES=""
echo "$SOURCE_FILES" | grep -q "\.java$" && LANGUAGES="$LANGUAGES\"java\", "
echo "$SOURCE_FILES" | grep -q "\.py$" && LANGUAGES="$LANGUAGES\"python\", "
echo "$SOURCE_FILES" | grep -qE "\.(js|jsx|mjs)$" && LANGUAGES="$LANGUAGES\"javascript\", "
echo "$SOURCE_FILES" | grep -qE "\.(ts|tsx)$" && LANGUAGES="$LANGUAGES\"typescript\", "
echo "$SOURCE_FILES" | grep -q "\.cs$" && LANGUAGES="$LANGUAGES\"csharp\", "
echo "$SOURCE_FILES" | grep -qE "\.(c|cpp|h|hpp)$" && LANGUAGES="$LANGUAGES\"cpp\", "
echo "$SOURCE_FILES" | grep -q "\.go$" && LANGUAGES="$LANGUAGES\"go\", "
echo "$SOURCE_FILES" | grep -q "\.rs$" && LANGUAGES="$LANGUAGES\"rust\", "
echo "$SOURCE_FILES" | grep -q "\.rb$" && LANGUAGES="$LANGUAGES\"ruby\", "
echo "$SOURCE_FILES" | grep -q "\.php$" && LANGUAGES="$LANGUAGES\"php\", "
echo "$SOURCE_FILES" | grep -qE "\.(kt|kts)$" && LANGUAGES="$LANGUAGES\"kotlin\", "
echo "$SOURCE_FILES" | grep -q "\.scala$" && LANGUAGES="$LANGUAGES\"scala\", "
echo "$SOURCE_FILES" | grep -q "\.swift$" && LANGUAGES="$LANGUAGES\"swift\", "
LANGUAGES=$(echo "$LANGUAGES" | sed 's/, $//')

TOTAL_COUNT=$((SOURCE_COUNT + TEST_COUNT + CONFIG_COUNT + DB_COUNT + DOC_COUNT))

# Generate JSON output
cat > "$OUTPUT_FILE" << EOF
{
  "discovery_timestamp": "$(date -Iseconds)",
  "root_path": "$ROOT_PATH",
  "summary": {
    "total_files": $TOTAL_COUNT,
    "source_files": $SOURCE_COUNT,
    "config_files": $CONFIG_COUNT,
    "database_files": $DB_COUNT,
    "test_files": $TEST_COUNT,
    "doc_files": $DOC_COUNT
  },
  "languages_detected": [$LANGUAGES],
  "files": {
    "source": [
$(echo "$SOURCE_FILES" | grep "." | head -500 | while read -r f; do echo "      \"$f\","; done | sed '$ s/,$//')
    ],
    "config": [
$(echo "$ALL_CONFIG_FILES" | grep "." | while read -r f; do echo "      \"$f\","; done | sed '$ s/,$//')
    ],
    "database": [
$(echo "$ALL_DB_FILES" | grep "." | while read -r f; do echo "      \"$f\","; done | sed '$ s/,$//')
    ],
    "test": [
$(echo "$TEST_FILES" | grep "." | head -200 | while read -r f; do echo "      \"$f\","; done | sed '$ s/,$//')
    ],
    "documentation": [
$(echo "$DOC_FILES" | grep "." | while read -r f; do echo "      \"$f\","; done | sed '$ s/,$//')
    ]
  }
}
EOF

echo "========================================"
echo "Discovery complete!"
echo ""
echo "Summary:"
echo "  Source files:  $SOURCE_COUNT"
echo "  Config files:  $CONFIG_COUNT"  
echo "  Database files: $DB_COUNT"
echo "  Test files:    $TEST_COUNT"
echo "  Doc files:     $DOC_COUNT"
echo "  -----------------------"
echo "  Total:         $TOTAL_COUNT"
echo ""
echo "Languages detected: $LANGUAGES"
echo ""
echo "Results saved to: $OUTPUT_FILE"
