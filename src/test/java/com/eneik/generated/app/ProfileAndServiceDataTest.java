package com.eneik.generated.app;

import com.eneik.generated.app.domain.Professional;
import com.eneik.generated.app.domain.Service;
import com.eneik.generated.app.repository.ProfessionalRepository;
import com.eneik.generated.app.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=true"
})
@Transactional
class ProfileAndServiceDataTest {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    void givenNewProfessional_whenSaved_thenDatabaseStoresFbAndPhoneIdentifiers() {
        Professional professional = new Professional("Nino Beridze", "fb_user_99283", "+995599123456");
        professional.setDescription("Master hair stylist in Tbilisi");
        professional.setAddress("Rustaveli Ave 12, Tbilisi");

        Professional saved = professionalRepository.save(professional);

        assertThat(saved.getId()).isNotNull();

        Optional<Professional> foundByFb = professionalRepository.findByFacebookId("fb_user_99283");
        assertThat(foundByFb).isPresent();
        assertThat(foundByFb.get().getPhoneNumber()).isEqualTo("+995599123456");

        Optional<Professional> foundByPhone = professionalRepository.findByPhoneNumber("+995599123456");
        assertThat(foundByPhone).isPresent();
        assertThat(foundByPhone.get().getFacebookId()).isEqualTo("fb_user_99283");
    }

    @Test
    void givenServiceAddition_whenSaved_thenLinkedToProfessionalWithGelPricing() {
        Professional professional = new Professional("Giorgi Tsereteli", "fb_user_88201", "+995577987654");
        Professional savedProfessional = professionalRepository.save(professional);

        Service haircut = new Service("Haircut & Styling", new BigDecimal("45.00"), 45);
        haircut.setDescription("Standard men haircut and styling");
        savedProfessional.addService(haircut);

        professionalRepository.save(savedProfessional);

        List<Service> services = serviceRepository.findByProfessionalId(savedProfessional.getId());
        assertThat(services).hasSize(1);

        Service savedService = services.get(0);
        assertThat(savedService.getName()).isEqualTo("Haircut & Styling");
        assertThat(savedService.getPriceGel()).isEqualByComparingTo(new BigDecimal("45.00"));
        assertThat(savedService.getDurationMinutes()).isEqualTo(45);
        assertThat(savedService.getProfessional().getId()).isEqualTo(savedProfessional.getId());
    }
}
