package com.eneik.generated.seeding;

import com.eneik.generated.model.AvailabilitySlot;
import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.repository.AvailabilitySlotRepository;
import com.eneik.generated.repository.MasterProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class GeorgianDataSeeder implements CommandLineRunner {

    private final MasterProfileRepository masterProfileRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public GeorgianDataSeeder(MasterProfileRepository masterProfileRepository,
                              AvailabilitySlotRepository availabilitySlotRepository) {
        this.masterProfileRepository = masterProfileRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (masterProfileRepository.count() > 0) {
            return; // Data already exists
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        // Seed Realistic Georgian Profiles
        MasterProfile m1 = new MasterProfile(
                "Giga Beridze",
                "Tbilisi",
                "+995599123456",
                "gigaberidze.barber.tbilisi",
                "Professional barber in Old Tbilisi with 10 years experience",
                now
        );

        MasterProfile m2 = new MasterProfile(
                "Nino Kapanadze",
                "Batumi",
                "+995591987654",
                "nino.beauty.batumi",
                "Nail artist and cosmetologist near Batumi Boulevard",
                now
        );

        MasterProfile m3 = new MasterProfile(
                "Giorgi Dolidze",
                "Kutaisi",
                "+995577555444",
                "giorgi.auto.kutaisi",
                "Auto mechanic and detailing specialist in central Kutaisi",
                now
        );

        List<MasterProfile> savedMasters = masterProfileRepository.saveAll(List.of(m1, m2, m3));

        // Seed Availability Slots for each master
        for (MasterProfile master : savedMasters) {
            OffsetDateTime slot1Start = now.plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
            OffsetDateTime slot1End = slot1Start.plusHours(1);

            OffsetDateTime slot2Start = now.plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            OffsetDateTime slot2End = slot2Start.plusHours(1);

            AvailabilitySlot s1 = new AvailabilitySlot(master.getId(), slot1Start, slot1End, "AVAILABLE", now);
            AvailabilitySlot s2 = new AvailabilitySlot(master.getId(), slot2Start, slot2End, "AVAILABLE", now);

            availabilitySlotRepository.saveAll(List.of(s1, s2));
        }
    }
}
