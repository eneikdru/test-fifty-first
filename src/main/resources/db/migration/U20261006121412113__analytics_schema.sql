-- Undo Migration / Manual Recovery Instructions: Analytics Schema
-- Version: U20261006121412113
-- NOTE: Flyway Community Edition does not execute undo files automatically.
-- This file serves as the tested manual recovery script for disaster recovery scenarios.

DROP INDEX IF EXISTS idx_system_metrics_name_time;
DROP TABLE IF EXISTS system_metrics;

DROP INDEX IF EXISTS idx_analytics_events_type_time;
DROP TABLE IF EXISTS analytics_events;
