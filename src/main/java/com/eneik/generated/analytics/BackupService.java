package com.eneik.generated.analytics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.Clock;
import java.time.OffsetDateTime;

@Service
public class BackupService {

    private final Clock clock;
    private final String blobStoragePath;

    public BackupService(Clock clock, @Value("${app.backup.blob-storage-path:/tmp/blob_storage/backups}") String blobStoragePath) {
        this.clock = clock;
        this.blobStoragePath = blobStoragePath;
    }

    public ProcessResult executeBackup(String targetStoragePath) {
        String effectivePath = (targetStoragePath != null && !targetStoragePath.isBlank())
                ? targetStoragePath : blobStoragePath;
        return runScript("scripts/backup.sh", effectivePath, null);
    }

    public ProcessResult executeRestore(String targetStoragePath, String backupFileName) {
        String effectivePath = (targetStoragePath != null && !targetStoragePath.isBlank())
                ? targetStoragePath : blobStoragePath;
        return runScript("scripts/restore.sh", effectivePath, backupFileName);
    }

    public OffsetDateTime getCurrentTimestamp() {
        return OffsetDateTime.now(clock);
    }

    private ProcessResult runScript(String scriptPath, String storagePath, String extraArg) {
        try {
            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                return new ProcessResult(-1, "", "Script not found: " + scriptPath);
            }

            ProcessBuilder pb;
            if (extraArg != null && !extraArg.isBlank()) {
                pb = new ProcessBuilder(scriptFile.getAbsolutePath(), extraArg);
            } else {
                pb = new ProcessBuilder(scriptFile.getAbsolutePath());
            }

            pb.environment().put("BLOB_STORAGE_PATH", storagePath);

            Process process = pb.start();
            StringBuilder stdout = new StringBuilder();
            StringBuilder stderr = new StringBuilder();

            try (BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = outReader.readLine()) != null) {
                    stdout.append(line).append("\n");
                }
                while ((line = errReader.readLine()) != null) {
                    stderr.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            return new ProcessResult(exitCode, stdout.toString(), stderr.toString());
        } catch (Exception e) {
            return new ProcessResult(-1, "", e.getMessage());
        }
    }

    public record ProcessResult(int exitCode, String stdout, String stderr) {
        public boolean isSuccess() {
            return exitCode == 0;
        }
    }
}
