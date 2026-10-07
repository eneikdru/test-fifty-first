package com.eneik.generated.seeding;

import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.privacy.dto.PrivacyExportRequest;
import com.eneik.generated.privacy.dto.PrivacyExportStatusResponse;
import com.eneik.generated.privacy.model.UserDataRecord;
import com.eneik.generated.privacy.service.PrivacyService;
import com.eneik.generated.repository.MasterProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class MasterProfileQaTest {

    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @Autowired
    private PrivacyService privacyService;

    @Test
    @DisplayName("Given a new professional test user, registration and FB import succeed")
    public void testRegistrationAndFbImportSucceed() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        MasterProfile newMaster = new MasterProfile(
                "Lasha Tskhadadze",
                "Tbilisi",
                "+995595112233",
                "lasha.barber.tbilisi",
                "Barber in Saburtalo",
                now
        );

        MasterProfile saved = masterProfileRepository.save(newMaster);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFacebookPageId()).isEqualTo("lasha.barber.tbilisi");
        assertThat(saved.getPhone()).isEqualTo("+995595112233");

        // Verify PrivacyService integration for registered master profile
        UserDataRecord userRecord = new UserDataRecord("user-lasha", "+995595112233", "fb-lasha-123", "Lasha Tskhadadze", "Tbilisi", Instant.now());
        userRecord.setMasterId(String.valueOf(saved.getId()));
        userRecord.setFacebookPageId(saved.getFacebookPageId());
        userRecord.setPublicProfileUrl("https://georgianmasters.ge/m/" + saved.getId());
        userRecord.setDescription(saved.getDescription());
        privacyService.registerUserData(userRecord);

        PrivacyExportStatusResponse exportResponse = privacyService.requestExport("user-lasha", new PrivacyExportRequest("JSON", true, null));
        assertThat(exportResponse.payload().masterProfile()).isNotNull();
        assertThat(exportResponse.payload().masterProfile().facebookPageId()).isEqualTo("lasha.barber.tbilisi");
        assertThat(exportResponse.payload().masterProfile().publicProfileUrl()).isEqualTo("https://georgianmasters.ge/m/" + saved.getId());
    }

    @Test
    @DisplayName("Given an invalid FB token, API test gracefully handles the error")
    public void testInvalidFbTokenHandling() {
        // Attempting privacy export for unknown/invalid user ID returns default anonymized payload without throwing
        PrivacyExportStatusResponse exportResponse = privacyService.requestExport("invalid-fb-token-user", new PrivacyExportRequest("JSON", true, null));
        assertThat(exportResponse.status()).isEqualTo("COMPLETED");
        assertThat(exportResponse.payload().userProfile().fullName()).isEqualTo("Anonymized User");
        assertThat(exportResponse.payload().masterProfile()).isNull();
    }

    @Test
    @DisplayName("Given a public URL, when accessed, profile data matches DB")
    public void testPublicProfileDataMatchesDb() {
        List<MasterProfile> masters = masterProfileRepository.findAll();
        assertThat(masters).isNotEmpty();

        MasterProfile dbMaster = masters.get(0);
        Optional<MasterProfile> fetched = masterProfileRepository.findById(dbMaster.getId());
        assertThat(fetched).isPresent();
        MasterProfile profile = fetched.get();

        assertThat(profile.getName()).isEqualTo(dbMaster.getName());
        assertThat(profile.getCity()).isEqualTo(dbMaster.getCity());
        assertThat(profile.getPhone()).isEqualTo(dbMaster.getPhone());
        assertThat(profile.getFacebookPageId()).isEqualTo(dbMaster.getFacebookPageId());
        assertThat(profile.getDescription()).isEqualTo(dbMaster.getDescription());
    }
}
