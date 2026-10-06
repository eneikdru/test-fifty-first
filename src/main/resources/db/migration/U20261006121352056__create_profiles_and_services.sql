-- MANUAL RECOVERY / UNDO INSTRUCTION FILE
-- Note: Flyway Community Edition does not execute undo migrations automatically.
-- This file provides manual SQL commands for disaster recovery.

DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS professionals;
