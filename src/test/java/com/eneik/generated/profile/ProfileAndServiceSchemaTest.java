package com.eneik.generated.profile;

import com.eneik.generated.profile.model.MasterProfile;
import com.eneik.generated.profile.model.MasterService;
import com.eneik.generated.profile.repository.MasterProfileRepository;
import com.eneik.generated.profile.repository.MasterServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ProfileAndServiceSchemaTest {

    @Autowired
    private MasterProfileRepository profileRepository;

    @Autowired
    private MasterServiceRepository serviceRepository;

    @Test
    void testSaveProfileWithFacebookAndPhoneIdentifiers() {
        MasterProfile profile = new MasterProfile(
                "fb-user-12345",
                "fb-page-67890",
                "+995555123456",
                "Giorgi Beridze",
                "TBILISI",
                "Rustaveli Ave 12",
                "Professional Hair Stylist in Tbilisi",
                "https://georgianmasters.ge/masters/giorgi-beridze"
        );

        MasterProfile saved = profileRepository.save(profile);
        assertNotNull(saved.getId());

        MasterProfile retrievedByFb = profileRepository.findByFacebookId("fb-user-12345").orElseThrow();
        assertEquals("+995555123456", retrievedByFb.getPhoneNumber());
        assertEquals("Giorgi Beridze", retrievedByFb.getFullName());
        assertEquals("TBILISI", retrievedByFb.getCity());

        MasterProfile retrievedByPhone = profileRepository.findByPhoneNumber("+995555123456").orElseThrow();
        assertEquals("fb-user-12345", retrievedByPhone.getFacebookId());
    }

    @Test
    void testAddServicesLinkedToProfessionalWithGelPricing() {
        MasterProfile profile = new MasterProfile(
                "fb-user-999",
                "fb-page-888",
                "+995599887766",
                "Nino Tkeshelashvili",
                "BATUMI",
                "Gamsakhurdia St 5",
                "Nail artist and cosmetologist",
                "https://georgianmasters.ge/masters/nino-t"
        );

        MasterService service1 = new MasterService(
                "Haircut & Styling",
                "Classic haircut and styling with natural products",
                new BigDecimal("50.00"),
                45
        );

        MasterService service2 = new MasterService(
                "Manicure Gel Polish",
                "Full hardware manicure with gel coating",
                new BigDecimal("40.50"),
                60
        );

        profile.addService(service1);
        profile.addService(service2);

        MasterProfile savedProfile = profileRepository.save(profile);
        assertNotNull(savedProfile.getId());
        assertEquals(2, savedProfile.getServices().size());

        List<MasterService> servicesByMaster = serviceRepository.findByMasterProfileId(savedProfile.getId());
        assertEquals(2, servicesByMaster.size());

        MasterService retrievedService = servicesByMaster.stream()
                .filter(s -> s.getName().equals("Haircut & Styling"))
                .findFirst()
                .orElseThrow();

        assertEquals(0, new BigDecimal("50.00").compareTo(retrievedService.getPriceGel()));
        assertEquals(45, retrievedService.getDurationMinutes());
        assertEquals(savedProfile.getId(), retrievedService.getMasterProfile().getId());
    }
}
