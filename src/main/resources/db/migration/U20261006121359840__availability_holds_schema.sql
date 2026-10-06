-- Manual Rollback Instructions for Migration V20261006121359840
-- Note: Flyway Community Edition does not execute undo migrations automatically.
-- Run the following SQL manually during disaster recovery if needed:

DROP TABLE IF EXISTS slot_status_history;
DROP TABLE IF EXISTS availability_slots;
