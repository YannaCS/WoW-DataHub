#!/bin/bash
# WoW DataHub Build Script for Linux/Mac
# This script compiles Java files and creates deployable WAR file

set -e  # Exit on any error

echo "==============================================="
echo "WoW DataHub Build Script"
echo "==============================================="

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
SRC_DIR="$PROJECT_ROOT/src/main/java"
BUILD_DIR="$PROJECT_ROOT/build"
WEBAPP_DIR="$PROJECT_ROOT/webapp"
LIB_DIR="$WEBAPP_DIR/WEB-INF/lib"
WAR_FILE="$PROJECT_ROOT/wow-datahub.war"

echo "Project Root: $PROJECT_ROOT"
echo "Build Directory: $BUILD_DIR"
echo ""

# Create build directory
echo "Creating build directory..."
mkdir -p "$BUILD_DIR"

# Check if Java is available
echo "Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "ERROR: Java 17 or higher is required"
    echo "Current Java version: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

echo "Java version check passed: $(java -version 2>&1 | head -n 1)"

# Check if required JARs exist
echo "Checking required dependencies..."
required_jars=(
    "mysql-connector-j-8.4.0.jar"
    "jackson-databind-2.15.2.jar"
    "jackson-core-2.15.2.jar"
    "jackson-annotations-2.15.2.jar"
    "httpclient-4.5.14.jar"
    "log4j-core-2.20.0.jar"
)

missing_jars=()
for jar in "${required_jars[@]}"; do
    if [ ! -f "$LIB_DIR/$jar" ]; then
        missing_jars+=("$jar")
    fi
done

if [ ${#missing_jars[@]} -gt 0 ]; then
    echo "ERROR: Missing required JAR files:"
    for jar in "${missing_jars[@]}"; do
        echo "  - $jar"
    done
    echo ""
    echo "Please download all required JARs as specified in the setup guide"
    exit 1
fi

echo "All required dependencies found"

# Compile Java files
echo ""
echo "Compiling Java source files..."
find "$SRC_DIR" -name "*.java" -print0 | xargs -0 javac -cp "$LIB_DIR/*" -d "$BUILD_DIR"

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "ERROR: Compilation failed"
    echo "Please check the error messages above"
    exit 1
fi

# Count compiled class files
class_count=$(find "$BUILD_DIR" -name "*.class" | wc -l)
echo "Compiled $class_count class files"

# Create WAR file
echo ""
echo "Creating WAR file..."
[ -f "$WAR_FILE" ] && rm "$WAR_FILE"

# Change to webapp directory to create proper WAR structure
cd "$WEBAPP_DIR"
jar -cvf "$WAR_FILE" . -C "$BUILD_DIR" .

if [ $? -eq 0 ]; then
    echo "WAR file created successfully: $WAR_FILE"
else
    echo "ERROR: WAR file creation failed"
    exit 1
fi

# Display build information
echo ""
echo "==============================================="
echo "Build Summary"
echo "==============================================="
echo "Source Directory: $SRC_DIR"
echo "Build Directory: $BUILD_DIR"
echo "WAR File: $WAR_FILE"
echo ""

# Check WAR file size
if [ -f "$WAR_FILE" ]; then
    war_size=$(du -h "$WAR_FILE" | cut -f1)
    echo "WAR File Size: $war_size"
fi

echo ""
echo "Build completed successfully!"
echo ""
echo "Next steps:"
echo "1. Deploy WAR file to Tomcat webapps directory:"
echo "   cp $WAR_FILE \$TOMCAT_HOME/webapps/"
echo "2. Start Tomcat server:"
echo "   \$TOMCAT_HOME/bin/startup.sh"
echo "3. Access application at http://localhost:8080/wow-datahub"
echo ""

# Make the WAR file executable (for some deployment scenarios)
chmod 644 "$WAR_FILE"

echo "Build script completed at $(date)"