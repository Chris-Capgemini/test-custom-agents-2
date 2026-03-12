---
name: discover-files
description: Discovers all relevant files in a codebase for analysis. Returns categorized lists of source code, config, database, test, and documentation files while automatically excluding build artifacts and irrelevant directories. Use this skill when you need to find files to analyze.
---

# Discover Files Skill

This skill provides deterministic file discovery for code analysis. It categorizes files and excludes irrelevant directories automatically.

## How to Use

Run the discovery script to get a complete, categorized list of files:

```bash
.github/skills/discover-files/discover.sh [path-to-analyze]
```

If the script is not available, use the patterns below with the `search` tool.

## File Categories

### 1. Source Code Files

**Extensions by Language:**

| Language | Extensions |
|----------|------------|
| Java | `.java` |
| Python | `.py` |
| JavaScript | `.js`, `.jsx`, `.mjs` |
| TypeScript | `.ts`, `.tsx` |
| C# | `.cs` |
| C/C++ | `.c`, `.cpp`, `.h`, `.hpp` |
| Go | `.go` |
| Rust | `.rs` |
| Ruby | `.rb` |
| PHP | `.php` |
| Kotlin | `.kt`, `.kts` |
| Scala | `.scala` |
| Swift | `.swift` |

### 2. Configuration Files

**Build & Dependency Files:**
- `pom.xml`, `build.gradle`, `build.gradle.kts`, `settings.gradle`
- `package.json`, `package-lock.json`, `yarn.lock`
- `requirements.txt`, `setup.py`, `pyproject.toml`, `Pipfile`
- `Gemfile`, `*.gemspec`
- `Cargo.toml`, `go.mod`, `go.sum`
- `*.csproj`, `*.sln`, `nuget.config`
- `CMakeLists.txt`, `Makefile`

**Application Config:**
- `application*.properties`, `application*.yml`, `application*.yaml`
- `bootstrap*.properties`, `bootstrap*.yml`
- `config*.properties`, `config*.yml`, `config*.yaml`
- `appsettings*.json`, `web.config`, `app.config`
- `.env`, `.env.*`

**Framework Config:**
- `docker-compose*.yml`, `Dockerfile*`
- `kubernetes/*.yaml`, `k8s/*.yaml`
- `nginx.conf`, `apache.conf`
- `webpack.config.js`, `vite.config.*`, `rollup.config.*`
- `tsconfig.json`, `jsconfig.json`
- `.eslintrc*`, `.prettierrc*`, `babel.config.*`

### 3. Database Files

**SQL & Migrations:**
- `*.sql`
- `db/migrate/*.rb` (Rails)
- `migrations/*.py` (Django/Alembic)
- `src/main/resources/db/migration/*.sql` (Flyway)
- `liquibase/*.xml`, `liquibase/*.yaml`

**Entity/Model Definitions:**
- Files containing `@Entity`, `@Table` (Java)
- Files in `models/`, `entities/`, `domain/` directories
- `*.entity.ts`, `*.model.ts`

**Database Config:**
- Files containing database connection strings
- `persistence.xml`, `hibernate.cfg.xml`
- `database.yml`, `ormconfig.json`

### 4. Test Files

**Patterns to identify (for exclusion or separate analysis):**
- `*Test.java`, `*Tests.java`, `*TestCase.java`
- `*_test.py`, `test_*.py`, `*_spec.py`
- `*.test.js`, `*.spec.js`, `*.test.ts`, `*.spec.ts`
- `*_test.go`
- `*Test.cs`, `*Tests.cs`
- Files in `test/`, `tests/`, `spec/`, `__tests__/` directories

### 5. Documentation Files

- `README*`, `CHANGELOG*`, `CONTRIBUTING*`, `LICENSE*`
- `*.md`, `*.rst`, `*.adoc`
- `docs/**/*`, `documentation/**/*`
- `*.txt` (in root or docs directories)
- `ARCHITECTURE*`, `DESIGN*`, `API*`

## Exclusion Patterns

**ALWAYS EXCLUDE these directories:**

```
node_modules/
.git/
.svn/
.hg/
dist/
build/
target/
out/
bin/
obj/
.idea/
.vscode/
.vs/
__pycache__/
*.pyc
.pytest_cache/
.mypy_cache/
.tox/
.eggs/
*.egg-info/
coverage/
.nyc_output/
.next/
.nuxt/
vendor/
Pods/
.gradle/
.m2/
logs/
*.log
tmp/
temp/
cache/
.cache/
```

**ALWAYS EXCLUDE these file patterns:**
- `*.min.js`, `*.min.css` (minified files)
- `*.map` (source maps)
- `*.lock` (except for dependency analysis)
- Binary files: `*.jar`, `*.war`, `*.ear`, `*.dll`, `*.exe`, `*.so`, `*.dylib`
- Media files: `*.png`, `*.jpg`, `*.gif`, `*.svg`, `*.ico`, `*.mp3`, `*.mp4`
- Archives: `*.zip`, `*.tar`, `*.gz`, `*.rar`

## Output Format

When reporting discovered files, use this JSON structure:

```json
{
  "discovery_timestamp": "2026-02-05T10:30:00Z",
  "root_path": "/path/to/project",
  "summary": {
    "total_files": 150,
    "source_files": 100,
    "config_files": 20,
    "database_files": 10,
    "test_files": 15,
    "doc_files": 5
  },
  "files": {
    "source": [
      {"path": "src/main/java/com/example/OrderService.java", "language": "java"},
      {"path": "src/main/java/com/example/PaymentService.java", "language": "java"}
    ],
    "config": [
      {"path": "pom.xml", "type": "build"},
      {"path": "src/main/resources/application.properties", "type": "application"}
    ],
    "database": [
      {"path": "src/main/resources/db/migration/V1__init.sql", "type": "migration"},
      {"path": "src/main/java/com/example/entity/Order.java", "type": "entity"}
    ],
    "test": [
      {"path": "src/test/java/com/example/OrderServiceTest.java", "language": "java"}
    ],
    "documentation": [
      {"path": "README.md", "type": "readme"},
      {"path": "docs/architecture.md", "type": "doc"}
    ]
  },
  "excluded_directories": ["node_modules", "target", ".git"],
  "languages_detected": ["java"]
}
```

## Quick Discovery Commands

For manual discovery using shell commands:

**Find all source files (example for Java):**
```bash
find . -name "*.java" -not -path "*/node_modules/*" -not -path "*/target/*" -not -path "*/.git/*" -not -path "*/test/*"
```

**Find config files:**
```bash
find . \( -name "pom.xml" -o -name "*.properties" -o -name "*.yml" -o -name "*.yaml" \) -not -path "*/node_modules/*" -not -path "*/target/*"
```

**Find SQL files:**
```bash
find . -name "*.sql" -not -path "*/node_modules/*" -not -path "*/target/*"
```
