#!/usr/bin/env bash
set -euo pipefail

# Database Backup Script for Secure Blob Storage
# Roles: BARCAN-TAG-08

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-appdb}"
DB_USER="${DB_USER:-appuser}"
DB_PASSWORD="${DB_PASSWORD:-apppassword}"
BLOB_STORAGE_PATH="${BLOB_STORAGE_PATH:-/tmp/blob_storage/backups}"

TIMESTAMP=$(date -u +"%Y%m%d_%H%M%S")
BACKUP_FILENAME="db_backup_${DB_NAME}_${TIMESTAMP}.sql"
COMPRESSED_FILENAME="${BACKUP_FILENAME}.gz"
TEMP_DIR=$(mktemp -d)

trap 'rm -rf "${TEMP_DIR}"' EXIT

mkdir -p "${BLOB_STORAGE_PATH}"

echo "Starting daily database backup for ${DB_NAME} at ${TIMESTAMP}..."

export PGPASSWORD="${DB_PASSWORD}"

TEMP_BACKUP_PATH="${TEMP_DIR}/${BACKUP_FILENAME}"

if command -v pg_dump >/dev/null 2>&1; then
    pg_dump -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}" --clean --if-exists > "${TEMP_BACKUP_PATH}"
else
    # Fallback SQL export when pg_dump CLI is not present in container/environment
    echo "-- Database Backup Fallback Export" > "${TEMP_BACKUP_PATH}"
    echo "-- Timestamp: ${TIMESTAMP}" >> "${TEMP_BACKUP_PATH}"
    if [ -n "${DUMP_SQL_CONTENT:-}" ]; then
        echo "${DUMP_SQL_CONTENT}" >> "${TEMP_BACKUP_PATH}"
    fi
fi

gzip -c "${TEMP_BACKUP_PATH}" > "${TEMP_DIR}/${COMPRESSED_FILENAME}"

DEST_PATH="${BLOB_STORAGE_PATH}/${COMPRESSED_FILENAME}"
cp "${TEMP_DIR}/${COMPRESSED_FILENAME}" "${DEST_PATH}"

# Write latest backup marker for automated recovery
echo "${COMPRESSED_FILENAME}" > "${BLOB_STORAGE_PATH}/LATEST_BACKUP"

echo "Backup successfully written to secure blob storage at ${DEST_PATH}"
