#!/bin/bash

# Clean up old structure and organize properly
echo "Cleaning up folder structure..."

# Remove unnecessary files
rm -rf target/
rm -rf node_modules/
rm -f package*.json
rm -f pyproject.toml
rm -f uv.lock

# Create proper documentation structure
mkdir -p docs/architecture
mkdir -p docs/api
mkdir -p docs/testing

# Move documentation files
mv architecture_compliance_report.md docs/architecture/
mv migration_test_report.md docs/testing/
mv test_apis.sh scripts/

echo "Structure cleanup complete"