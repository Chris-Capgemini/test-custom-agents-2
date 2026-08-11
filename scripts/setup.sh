#!/bin/bash

# GenRevive Development Setup Script
# Optional setup for enhanced development experience

set -e

echo "🛠️  GenRevive Development Setup"
echo "================================"
echo ""

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 1. Check dependencies
echo -e "${BLUE}Checking development dependencies...${NC}"

# Check Git
if command -v git &> /dev/null; then
  echo -e "${GREEN}✓ Git found: $(git --version)${NC}"
else
  echo -e "${YELLOW}⚠ Git not found${NC}"
fi

# Check Node.js
if command -v node &> /dev/null; then
  echo -e "${GREEN}✓ Node.js found: $(node --version)${NC}"
else
  echo -e "${YELLOW}⚠ Node.js not found (required for some tasks)${NC}"
fi

# Check Java
if command -v java &> /dev/null; then
  echo -e "${GREEN}✓ Java found: $(java -version 2>&1 | head -1)${NC}"
else
  echo -e "${YELLOW}⚠ Java not found (required for backend development)${NC}"
fi

# 2. Install development tools
echo ""
echo -e "${BLUE}Installing development tools...${NC}"

# Install ESLint if Node is available
if command -v npm &> /dev/null; then
  if [ -f "package.json" ]; then
    npm install --save-dev eslint prettier 2>/dev/null || true
    echo -e "${GREEN}✓ ESLint and Prettier configured${NC}"
  fi
fi

# 3. Configure IDE
echo ""
echo -e "${BLUE}Configuring IDE settings...${NC}"

# Create .vscode/settings.json if it doesn't exist
mkdir -p .vscode
if [ ! -f ".vscode/settings.json" ]; then
  cat > .vscode/settings.json << 'EOF'
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "vscode.ipynb",
  "editor.tabSize": 2,
  "files.exclude": {
    "**/.git": true,
    "**/.DS_Store": true,
    "**/node_modules": true,
    "**/target": true
  },
  "search.exclude": {
    "**/node_modules": true,
    "**/target": true,
    ".git": true
  }
}
EOF
  echo -e "${GREEN}✓ VS Code settings configured${NC}"
fi

# 4. Setup Git config
echo ""
echo -e "${BLUE}Setting up Git configuration...${NC}"

# Configure user if not already set in repo
if ! git config --local user.name &> /dev/null; then
  # Try environment variable first, then prompt if interactive
  if [ -z "$GIT_USER_NAME" ]; then
    if [ -t 0 ]; then
      # Interactive mode
      read -p "Enter your name for commits: " GIT_USER_NAME
    else
      # Non-interactive mode - use default
      GIT_USER_NAME="GenRevive Developer"
    fi
  fi
  git config --local user.name "$GIT_USER_NAME"
fi

if ! git config --local user.email &> /dev/null; then
  # Try environment variable first, then prompt if interactive
  if [ -z "$GIT_USER_EMAIL" ]; then
    if [ -t 0 ]; then
      # Interactive mode
      read -p "Enter your email for commits: " GIT_USER_EMAIL
    else
      # Non-interactive mode - use default
      GIT_USER_EMAIL="dev@genrevive.local"
    fi
  fi
  git config --local user.email "$GIT_USER_EMAIL"
fi

echo -e "${GREEN}✓ Git configuration complete${NC}"

# 5. Display summary
echo ""
echo -e "${GREEN}✓ Development setup complete!${NC}"
echo ""
echo "Your workspace is ready for:"
echo "  - Code editing with VS Code"
echo "  - Git version control"
echo "  - Node.js and JavaScript development"
echo "  - Java backend development"
echo ""
echo "Pro tips:"
echo "  - Use 'npm run dev' for frontend development"
echo "  - Use 'mvn spring-boot:run' for backend"
echo "  - Check config/modernization-config.json for project settings"
echo ""
