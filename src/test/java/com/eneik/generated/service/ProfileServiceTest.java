package com.eneik.generated.service;

import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.repository.MasterProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private MasterProfileRepository repository;
    private ProfileService profileService;
    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-10-07T12:00:00Z"), ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        repository = mock(MasterProfileRepository.class);
        profileService = new ProfileService(repository, fixedClock);
    }

    @Test
    @DisplayName("Given valid phone number, When provisioning phone account, Then new MasterProfile is saved and returned")
    void testProvisionPhoneAccountNewProfile() {
        when(repository.findByPhone("+995599123456")).thenReturn(Optional.empty());
        when(repository.save(any(MasterProfile.class))).thenAnswer(invocation -> {
            MasterProfile master = invocation.getArgument(0);
            master.setId(10L);
            return master;
        });

        MasterProfile result = profileService.provisionPhoneAccount("+995599123456", "Giorgi", "Batumi");

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getPhone()).isEqualTo("+995599123456");
        assertThat(result.getName()).isEqualTo("Giorgi");
        assertThat(result.getCity()).isEqualTo("Batumi");
        assertThat(result.getCreatedAt()).isEqualTo(OffsetDateTime.now(fixedClock));

        verify(repository).save(any(MasterProfile.class));
    }

    @Test
    @DisplayName("Given existing phone number, When submitting phone login, Then existing MasterProfile is updated and returned")
    void testProvisionPhoneAccountExistingProfile() {
        MasterProfile existing = new MasterProfile("Old Name", "Tbilisi", "+995599123456", null, null, OffsetDateTime.now(fixedClock));
        existing.setId(5L);

        when(repository.findByPhone("+995599123456")).thenReturn(Optional.of(existing));
        when(repository.save(any(MasterProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MasterProfile result = profileService.provisionPhoneAccount("+995599123456", "Giorgi Updated", "Batumi");

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Giorgi Updated");
        assertThat(result.getCity()).isEqualTo("Batumi");
    }

    @Test
    @DisplayName("Given valid FB token, When importing Facebook profile, Then photos and descriptions are fetched and saved")
    void testImportFacebookProfileValidToken() {
        MasterProfile existing = new MasterProfile("Nino", "Tbilisi", "+995599888777", null, null, OffsetDateTime.now(fixedClock));
        existing.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(MasterProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MasterProfile result = profileService.importFacebookProfile("valid-fb-token-xyz", "page-batumi-salon", 1L, "+995599888777");

        assertThat(result.getFacebookPageId()).isEqualTo("page-batumi-salon");
        assertThat(result.getDescription()).contains("Imported Facebook Business Page Description");
        assertThat(result.getAddress()).isEqualTo("Chavchavadze Ave 12, Tbilisi");
        assertThat(result.getPhotos()).contains("https://graph.facebook.com/v18.0/page-batumi-salon/photo1.jpg");
    }

    @Test
    @DisplayName("Given invalid FB token, When importing Facebook profile, Then IllegalArgumentException is thrown")
    void testImportFacebookProfileInvalidToken() {
        assertThatThrownBy(() -> profileService.importFacebookProfile("invalid-token", "page-101", 1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid Facebook access token");
    }
}
