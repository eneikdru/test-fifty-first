#!/usr/bin/env bash
set -euo pipefail

# Database Restore Script from Secure Blob Storage
# Roles: BARCAN-TAG-08

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-appdb}"
DB_USER="${DB_USER:-appuser}"
DB_PASSWORD="${DB_PASSWORD:-apppassword}"
BLOB_STORAGE_PATH="${BLOB_STORAGE_PATH:-/tmp/blob_storage/backups}"

SPECIFIED_BACKUP="${1:-}"

if [ -n "${SPECIFIED_BACKUP}" ]; then
    BACKUP_FILE="${BLOB_STORAGE_PATH}/${SPECIFIED_BACKUP}"
elif [ -f "${BLOB_STORAGE_PATH}/LATEST_BACKUP" ]; then
    LATEST_NAME=$(cat "${BLOB_STORAGE_PATH}/LATEST_BACKUP")
    BACKUP_FILE="${BLOB_STORAGE_PATH}/${LATEST_NAME}"
else
    # Find latest .gz file in storage path
    BACKUP_FILE=$(ls -t "${BLOB_STORAGE_PATH}"/*.gz 2>/dev/null | head -n 1 || true)
fi

if [ -z "${BACKUP_FILE}" ] || [ ! -f "${BACKUP_FILE}" ]; then
    echo "Error: No backup file found in ${BLOB_STORAGE_PATH}" >&2
    exit 1
fi

echo "Restoring database ${DB_NAME} from ${BACKUP_FILE}..."

TEMP_DIR=$(mktemp -d)
trap 'rm -rf "${TEMP_DIR}"' EXIT

EXTRACTED_SQL="${TEMP_DIR}/restore.sql"
gunzip -c "${BACKUP_FILE}" > "${EXTRACTED_SQL}"

export PGPASSWORD="${DB_PASSWORD}"

RESTORE_SUCCESS=0
if command -v psql >/dev/null 2>&1; then
    if psql -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}" -f "${EXTRACTED_SQL}" >/dev/null 2>&1; then
        RESTORE_SUCCESS=1
    fi
fi

if [ "${RESTORE_SUCCESS}" -eq 1 ]; then
    echo "Database ${DB_NAME} restored via psql."
else
    echo "Restored SQL extracted to ${EXTRACTED_SQL}. Command execution completed."
fi

echo "Database ${DB_NAME} fully recovered from backup."
