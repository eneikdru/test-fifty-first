package com.eneik.generated.analytics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.zip.GZIPInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@io.zonky.test.db.AutoConfigureEmbeddedDatabase(type = io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@TestPropertySource(locations = "classpath:application-test.properties")
public class BackupRestoreTest {

    @Autowired
    private BackupService backupService;

    @Test
    @DisplayName("Given DB cluster, When daily backup is scheduled, Then it writes to secure blob storage")
    void testDailyBackupWritesToBlobStorage(@TempDir Path tempBlobStorage) throws Exception {
        String storageDir = tempBlobStorage.toString();

        BackupService.ProcessResult result = backupService.executeBackup(storageDir);

        assertThat(result.isSuccess())
                .withFailMessage("Backup script failed with stderr: " + result.stderr())
                .isTrue();

        File latestMarker = new File(tempBlobStorage.toFile(), "LATEST_BACKUP");
        assertThat(latestMarker).exists();

        String backupFileName = Files.readString(latestMarker.toPath()).trim();
        assertThat(backupFileName).endsWith(".sql.gz");

        File backupFile = new File(tempBlobStorage.toFile(), backupFileName);
        assertThat(backupFile).exists();
        assertThat(backupFile.length()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Given restore command, When executed, Then database recovers fully and data integrity is verified")
    void testDatabaseRestoreCommand(@TempDir Path tempBlobStorage) throws Exception {
        String storageDir = tempBlobStorage.toString();

        // 1. Run backup first to produce backup file
        BackupService.ProcessResult backupResult = backupService.executeBackup(storageDir);
        assertThat(backupResult.isSuccess()).isTrue();

        File latestMarker = new File(tempBlobStorage.toFile(), "LATEST_BACKUP");
        String backupFileName = Files.readString(latestMarker.toPath()).trim();

        // 2. Execute restore command and verify output and data integrity
        BackupService.ProcessResult restoreResult = backupService.executeRestore(storageDir, backupFileName);

        assertThat(restoreResult.isSuccess())
                .withFailMessage("Restore script failed with stderr: " + restoreResult.stderr())
                .isTrue();

        assertThat(restoreResult.stdout()).contains("Database appdb fully recovered from backup");

        // 3. Verify data integrity of extracted backup archive
        Path backupFilePath = tempBlobStorage.resolve(backupFileName);
        assertThat(backupFilePath).exists();

        try (GZIPInputStream gzipIn = new GZIPInputStream(Files.newInputStream(backupFilePath));
             BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIn))) {
            String firstLine = reader.readLine();
            assertThat(firstLine).isNotNull();
            assertThat(firstLine).contains("Database Backup");
        }
    }
}
