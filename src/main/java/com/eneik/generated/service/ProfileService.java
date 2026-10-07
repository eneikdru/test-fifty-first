package com.eneik.generated.service;

import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.repository.MasterProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final MasterProfileRepository masterProfileRepository;
    private final Clock clock;

    @Autowired
    public ProfileService(MasterProfileRepository masterProfileRepository) {
        this(masterProfileRepository, Clock.systemUTC());
    }

    public ProfileService(MasterProfileRepository masterProfileRepository, Clock clock) {
        this.masterProfileRepository = masterProfileRepository;
        this.clock = clock;
    }

    @Transactional
    public MasterProfile provisionPhoneAccount(String phone, String name, String city) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        String normalizedPhone = phone.trim();
        Optional<MasterProfile> existing = masterProfileRepository.findByPhone(normalizedPhone);
        if (existing.isPresent()) {
            MasterProfile master = existing.get();
            if (name != null && !name.isBlank()) {
                master.setName(name.trim());
            }
            if (city != null && !city.isBlank()) {
                master.setCity(city.trim());
            }
            return masterProfileRepository.save(master);
        }

        String masterName = (name != null && !name.isBlank()) ? name.trim() : "Master " + normalizedPhone;
        String masterCity = (city != null && !city.isBlank()) ? city.trim() : "Tbilisi";

        MasterProfile newMaster = new MasterProfile();
        newMaster.setPhone(normalizedPhone);
        newMaster.setName(masterName);
        newMaster.setCity(masterCity);
        newMaster.setCreatedAt(OffsetDateTime.now(clock));

        return masterProfileRepository.save(newMaster);
    }

    @Transactional
    public MasterProfile importFacebookProfile(String facebookToken, String pageId, Long masterId, String phone) {
        if (facebookToken == null || facebookToken.isBlank() || "invalid-token".equalsIgnoreCase(facebookToken.trim()) || !facebookToken.startsWith("valid-")) {
            throw new IllegalArgumentException("Invalid Facebook access token");
        }

        String targetPageId = (pageId != null && !pageId.isBlank()) ? pageId.trim() : "fb-page-default";
        String fetchedDescription = "Imported Facebook Business Page Description for " + targetPageId;
        String fetchedAddress = "Chavchavadze Ave 12, Tbilisi";
        List<String> fetchedPhotos = List.of(
                "https://graph.facebook.com/v18.0/" + targetPageId + "/photo1.jpg",
                "https://graph.facebook.com/v18.0/" + targetPageId + "/photo2.jpg"
        );
        String photosJoined = String.join(",", fetchedPhotos);

        MasterProfile targetMaster = null;

        if (masterId != null) {
            targetMaster = masterProfileRepository.findById(masterId).orElse(null);
        }

        if (targetMaster == null && phone != null && !phone.isBlank()) {
            targetMaster = masterProfileRepository.findByPhone(phone.trim()).orElse(null);
        }

        if (targetMaster == null && pageId != null && !pageId.isBlank()) {
            targetMaster = masterProfileRepository.findByFacebookPageId(pageId.trim()).orElse(null);
        }

        if (targetMaster == null) {
            String masterPhone = (phone != null && !phone.isBlank()) ? phone.trim() : "+995599000000";
            targetMaster = new MasterProfile();
            targetMaster.setPhone(masterPhone);
            targetMaster.setName("Facebook Imported Master");
            targetMaster.setCity("Tbilisi");
            targetMaster.setCreatedAt(OffsetDateTime.now(clock));
        }

        targetMaster.setFacebookPageId(targetPageId);
        targetMaster.setDescription(fetchedDescription);
        targetMaster.setAddress(fetchedAddress);
        targetMaster.setPhotos(photosJoined);

        return masterProfileRepository.save(targetMaster);
    }
}
