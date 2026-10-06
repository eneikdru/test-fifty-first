package com.eneik.generated.service;

import com.eneik.generated.domain.AvailabilitySlot;
import com.eneik.generated.domain.AvailabilitySlotRepository;
import com.eneik.generated.domain.MasterProfile;
import com.eneik.generated.domain.MasterProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataSeedingService {

    private static final Logger log = LoggerFactory.getLogger(DataSeedingService.class);

    private final MasterProfileRepository masterProfileRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final Clock clock;

    public DataSeedingService(MasterProfileRepository masterProfileRepository,
                              AvailabilitySlotRepository availabilitySlotRepository) {
        this(masterProfileRepository, availabilitySlotRepository, Clock.systemUTC());
    }

    @Autowired
    public DataSeedingService(MasterProfileRepository masterProfileRepository,
                              AvailabilitySlotRepository availabilitySlotRepository,
                              Clock clock) {
        this.masterProfileRepository = masterProfileRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    @Transactional
    public List<MasterProfile> seedRealisticGeorgianProfiles() {
        if (masterProfileRepository.count() > 0) {
            log.info("Database already contains master profiles. Skipping seeding.");
            return masterProfileRepository.findAll();
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        List<MasterProfile> seededProfiles = new ArrayList<>();

        // Profile 1: Giorgi Beridze in Tbilisi
        MasterProfile giorgi = new MasterProfile(
                "master-tbilisi-01",
                "Giorgi Beridze",
                "Tbilisi",
                "+995599112233",
                "https://facebook.com/giorgi.barber.tbilisi",
                "Master barber with 8 years of experience in central Tbilisi.",
                now
        );
        masterProfileRepository.save(giorgi);
        seededProfiles.add(giorgi);

        availabilitySlotRepository.save(new AvailabilitySlot(
                giorgi.getId(), "Men's Haircut & Styling", 40.0, 45,
                now.plusHours(2), now.plusHours(2).plusMinutes(45), false, "AVAILABLE"
        ));
        availabilitySlotRepository.save(new AvailabilitySlot(
                giorgi.getId(), "Beard Grooming", 25.0, 30,
                now.plusHours(4), now.plusHours(4).plusMinutes(30), false, "AVAILABLE"
        ));

        // Profile 2: Nino Kapanadze in Batumi
        MasterProfile nino = new MasterProfile(
                "master-batumi-01",
                "Nino Kapanadze",
                "Batumi",
                "+995598445566",
                "https://facebook.com/nino.beauty.batumi",
                "Professional cosmetologist and nail artist in Batumi.",
                now
        );
        masterProfileRepository.save(nino);
        seededProfiles.add(nino);

        availabilitySlotRepository.save(new AvailabilitySlot(
                nino.getId(), "Gel Manicure", 50.0, 60,
                now.plusHours(3), now.plusHours(4), false, "AVAILABLE"
        ));
        availabilitySlotRepository.save(new AvailabilitySlot(
                nino.getId(), "Facial Care & Spa", 85.0, 75,
                now.plusHours(5), now.plusHours(6).plusMinutes(15), false, "AVAILABLE"
        ));

        // Profile 3: Luka Tsereteli in Kutaisi
        MasterProfile luka = new MasterProfile(
                "master-kutaisi-01",
                "Luka Tsereteli",
                "Kutaisi",
                "+995597778899",
                "https://facebook.com/luka.massage.kutaisi",
                "Certified massage therapist specializing in sports and deep tissue massage.",
                now
        );
        masterProfileRepository.save(luka);
        seededProfiles.add(luka);

        availabilitySlotRepository.save(new AvailabilitySlot(
                luka.getId(), "Deep Tissue Massage", 70.0, 60,
                now.plusHours(1), now.plusHours(2), false, "AVAILABLE"
        ));

        log.info("Successfully seeded {} realistic Georgian master profiles and slots.", seededProfiles.size());
        return seededProfiles;
    }
}
