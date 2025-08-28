#!/bin/bash

# Playwright Test Runner Script
# Provides easy commands for different Playwright testing scenarios

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper function to print colored output
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Print usage information
usage() {
    echo "🎭 Playwright Test Runner"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  setup     Install Playwright browsers (required first time)"
    echo "  test      Run all Playwright tests (headless)"
    echo "  dev       Run tests in visual mode with slow motion"
    echo "  record    Run tests with video recording enabled"
    echo "  debug     Run single test in full debug mode"
    echo "  fast      Run tests in performance mode (maximum speed)"
    echo "  clean     Clean previous recordings and reports"
    echo ""
    echo "Examples:"
    echo "  $0 setup"
    echo "  $0 dev"
    echo "  $0 record"
    echo "  $0 debug LoginAndNavigationPlaywrightTest"
}

# Setup browsers
setup() {
    print_info "Installing Playwright browsers..."
    if command -v npx &> /dev/null; then
        npx playwright install chromium
        print_success "Playwright browsers installed successfully"
    else
        print_warning "npx not found. Installing via alternative method..."
        # Alternative installation method if npx is not available
        print_error "Please install Node.js and npm, then run: npx playwright install chromium"
        exit 1
    fi
}

# Run tests in headless mode
test() {
    print_info "Running Playwright tests in headless mode..."
    mvn test -Dtest="*Playwright*" \
        -Dplaywright.headless=true \
        -Dplaywright.recording=false \
        -Dplaywright.slowmo=50
}

# Development mode with visual browser
dev() {
    print_info "Running Playwright tests in development mode (visual + slow motion)..."
    mvn test -Dtest="*Playwright*" \
        -Dplaywright.headless=false \
        -Dplaywright.recording=false \
        -Dplaywright.slowmo=500
}

# Record test execution
record() {
    print_info "Running Playwright tests with video recording..."
    mkdir -p target/playwright-recordings
    mvn test -Dtest="*Playwright*" \
        -Dplaywright.headless=false \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=300 \
        -Dplaywright.recording.dir="target/playwright-recordings"
    
    if [ -d "target/playwright-recordings" ]; then
        print_success "Videos saved to target/playwright-recordings/"
        ls -la target/playwright-recordings/
    fi
}

# Debug mode for specific test
debug() {
    local test_class=${1:-"LoginAndNavigationPlaywrightTest"}
    print_info "Running $test_class in full debug mode..."
    mvn test -Dtest="$test_class" \
        -Dplaywright.headless=false \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=1000 \
        -X  # Maven debug output
}

# Performance/fast mode
fast() {
    print_info "Running Playwright tests in performance mode..."
    mvn test -Dtest="*Playwright*" \
        -Dplaywright.headless=true \
        -Dplaywright.recording=false \
        -Dplaywright.slowmo=0
}

# Clean artifacts
clean() {
    print_info "Cleaning Playwright artifacts..."
    rm -rf target/playwright-recordings/
    rm -rf target/surefire-reports/
    mvn clean -q
    print_success "Cleanup completed"
}

# Main script logic
case "${1:-}" in
    setup)
        setup
        ;;
    test)
        test
        ;;
    dev)
        dev
        ;;
    record)
        record
        ;;
    debug)
        debug "$2"
        ;;
    fast)
        fast
        ;;
    clean)
        clean
        ;;
    help|--help|-h)
        usage
        ;;
    "")
        usage
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        usage
        exit 1
        ;;
esac