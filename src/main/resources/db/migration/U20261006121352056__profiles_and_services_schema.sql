-- Undo Migration / Manual Recovery Instructions: Profiles and Services Schema
-- Version: U20261006121352056
-- NOTE: Flyway Community Edition does not execute undo files automatically.
-- This file serves as the tested manual recovery script for disaster recovery scenarios.

DROP INDEX IF EXISTS idx_master_services_master_id;
DROP TABLE IF EXISTS master_services;

DROP INDEX IF EXISTS idx_master_profiles_phone;
DROP INDEX IF EXISTS idx_master_profiles_fb_id;
DROP TABLE IF EXISTS master_profiles;
