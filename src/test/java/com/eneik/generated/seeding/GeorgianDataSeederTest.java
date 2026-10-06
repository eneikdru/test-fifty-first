package com.eneik.generated.seeding;

import com.eneik.generated.model.AvailabilitySlot;
import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.repository.AvailabilitySlotRepository;
import com.eneik.generated.repository.MasterProfileRepository;
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
public class GeorgianDataSeederTest {

    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Test
    public void testDataSeedingOnStartup() {
        List<MasterProfile> masters = masterProfileRepository.findAll();
        assertThat(masters).isNotEmpty();
        assertThat(masters.size()).isGreaterThanOrEqualTo(3);

        List<String> cities = masters.stream().map(MasterProfile::getCity).toList();
        assertThat(cities).contains("Tbilisi", "Batumi", "Kutaisi");

        for (MasterProfile master : masters) {
            assertThat(master.getPhone()).startsWith("+995");
            List<AvailabilitySlot> slots = availabilitySlotRepository.findByMasterId(master.getId());
            assertThat(slots).isNotEmpty();
        }
    }
}
