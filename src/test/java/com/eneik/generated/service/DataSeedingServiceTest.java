package com.eneik.generated.service;

import com.eneik.generated.domain.AvailabilitySlot;
import com.eneik.generated.domain.AvailabilitySlotRepository;
import com.eneik.generated.domain.BookingRepository;
import com.eneik.generated.domain.MasterProfile;
import com.eneik.generated.domain.MasterProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class DataSeedingServiceTest {

    @Autowired
    private DataSeedingService dataSeedingService;

    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        availabilitySlotRepository.deleteAll();
        masterProfileRepository.deleteAll();
    }

    @Test
    @DisplayName("Given fresh deployment, When seed scripts run, Then realistic Georgian profiles and slots are created")
    void testSeedRealisticGeorgianProfiles() {
        List<MasterProfile> seeded = dataSeedingService.seedRealisticGeorgianProfiles();

        assertThat(seeded).hasSize(3);

        List<MasterProfile> tbilisiMasters = masterProfileRepository.findByCity("Tbilisi");
        assertThat(tbilisiMasters).isNotEmpty();
        MasterProfile tbilisiMaster = tbilisiMasters.get(0);
        assertThat(tbilisiMaster.getPhoneNumber()).startsWith("+995");
        assertThat(tbilisiMaster.getFullName()).contains("Giorgi");

        List<MasterProfile> batumiMasters = masterProfileRepository.findByCity("Batumi");
        assertThat(batumiMasters).isNotEmpty();

        List<MasterProfile> kutaisiMasters = masterProfileRepository.findByCity("Kutaisi");
        assertThat(kutaisiMasters).isNotEmpty();

        List<AvailabilitySlot> tbilisiSlots = availabilitySlotRepository.findByMasterId(tbilisiMaster.getId());
        assertThat(tbilisiSlots).isNotEmpty();

        AvailabilitySlot slot = tbilisiSlots.get(0);
        assertThat(slot.getPriceGEL()).isGreaterThan(0.0);
        assertThat(slot.getStatus()).isEqualTo("AVAILABLE");
        assertThat(slot.isBooked()).isFalse();
    }
}
