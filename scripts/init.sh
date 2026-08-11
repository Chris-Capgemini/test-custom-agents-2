#!/bin/bash

# GenRevive Workspace Initialization Script
# Sets up the clean modernization template workspace

set -e

echo "🚀 GenRevive Workspace Initialization"
echo "======================================"
echo ""

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

URL="https://gensuitestorage.blob.core.windows.net/github-agents-container/migrators/migrator_template_repository.zip?sp=r&st=2026-07-27T11:27:03Z&se=2027-10-01T19:42:03Z&spr=https&sv=2026-02-06&sr=b&sig=r17apx42Xqz8TwaKsmoPVbXXxmxcbXHEibgeEcDjQfo%3D"
DEST_DIR="migrator"
ZIP_PATH="$DEST_DIR/clean_template_repository.zip"

# Check if running in Codespace
if [ "$GITHUB_CODESPACES" = "true" ]; then
  echo -e "${BLUE}✓ Running in GitHub Codespace${NC}"
else
  echo -e "${YELLOW}⚠ Not running in GitHub Codespace (may be local dev)${NC}"
fi

# 1. Validate configuration
echo -e "${BLUE}Checking configuration...${NC}"
if [ ! -f "config/modernization-config.json" ]; then
  echo -e "${YELLOW}⚠ config/modernization-config.json not found - creating template${NC}"
  mkdir -p config
fi

# 2. Make scripts executable
echo -e "${BLUE}Setting up permissions...${NC}"
chmod +x scripts/*.sh
echo -e "${GREEN}✓ Scripts are executable${NC}"

# 3. Download and extract modernization template
echo -e "${BLUE}Downloading modernization template...${NC}"
mkdir -p "$DEST_DIR"

if command -v curl >/dev/null 2>&1; then
  curl -fL "$URL" -o "$ZIP_PATH"
elif command -v wget >/dev/null 2>&1; then
  wget -O "$ZIP_PATH" "$URL"
else
  echo "Error: neither curl nor wget is installed." >&2
  exit 1
fi

if ! command -v unzip >/dev/null 2>&1; then
  echo "Error: unzip is not installed." >&2
  exit 1
fi

unzip -o "$ZIP_PATH" -d "$DEST_DIR"
echo -e "${GREEN}✓ Template downloaded and extracted to $DEST_DIR${NC}"

# 3. Install Node.js dependencies if package.json exists
if [ -f "package.json" ]; then
  echo -e "${BLUE}Installing Node dependencies...${NC}"
  npm install
  echo -e "${GREEN}✓ Node dependencies installed${NC}"
fi

# 4. Install Python dependencies if requirements.txt exists
if [ -f "requirements.txt" ]; then
  echo -e "${BLUE}Installing Python dependencies...${NC}"
  pip install -r requirements.txt
  echo -e "${GREEN}✓ Python dependencies installed${NC}"
fi

# 5. Set up Git hooks
echo -e "${BLUE}Setting up Git hooks...${NC}"
if [ -d ".git/hooks" ]; then
  # Create a pre-commit hook placeholder
  cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash
# Pre-commit hook for modernization workspace
# Add your checks here
exit 0
EOF
  chmod +x .git/hooks/pre-commit
  echo -e "${GREEN}✓ Git hooks configured${NC}"
fi

# 6. Create necessary directories
echo -e "${BLUE}Setting up workspace structure...${NC}"
mkdir -p docs logs build

# 7. Initialize environment
if [ -f ".env.template" ]; then
  if [ ! -f ".env" ]; then
    cp .env.template .env
    echo -e "${YELLOW}⚠ Created .env from template - please configure${NC}"
  fi
fi

# 8. Display workspace info
echo ""
echo -e "${GREEN}✓ Workspace initialized successfully!${NC}"
echo ""
echo "Next steps:"
echo "  1. Update config/modernization-config.json with your project details"
echo "  2. Review README.md for documentation"
echo "  3. Run: bash scripts/setup.sh (optional, for development setup)"
echo ""
echo "For more information:"
echo "  - GenRevive Docs: https://github.com/genrevive/docs"
echo "  - Modernization Guide: ./docs/GETTING_STARTED.md"
echo ""
