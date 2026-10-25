package com.eneik.generated.service;

import com.eneik.generated.dto.MasterServiceRequest;
import com.eneik.generated.dto.MasterServiceResponse;
import com.eneik.generated.model.MasterService;
import com.eneik.generated.repository.MasterServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MasterCatalogServiceTest {

    private MasterServiceRepository masterServiceRepository;
    private MasterCatalogService masterCatalogService;

    @BeforeEach
    void setUp() {
        masterServiceRepository = mock(MasterServiceRepository.class);
        masterCatalogService = new MasterCatalogService(masterServiceRepository);
    }

    @Test
    void shouldAddServiceSuccessfully() {
        MasterServiceRequest request = new MasterServiceRequest();
        request.setNameEn("Haircut");
        request.setPriceGel(50.0);
        request.setDurationMinutes(60);

        MasterService saved = new MasterService(1L, "Haircut", null, null, null, null, null, 50.0, 60, OffsetDateTime.now());
        saved.setId(10L);

        when(masterServiceRepository.save(any(MasterService.class))).thenReturn(saved);

        MasterServiceResponse response = masterCatalogService.addService(1L, request);

        assertEquals(10L, response.getId());
        assertEquals("Haircut", response.getName());
        assertEquals(Double.doubleToLongBits(50.0), Double.doubleToLongBits(response.getPriceGel()));
        assertEquals(60, response.getDurationMinutes());
    }

    @Test
    void shouldThrowWhenPriceIsNegative() {
        MasterServiceRequest request = new MasterServiceRequest();
        request.setPriceGel(-10.0);
        request.setDurationMinutes(60);

        assertThrows(IllegalArgumentException.class, () -> masterCatalogService.addService(1L, request));
    }

    @Test
    void shouldReturnLocalizedServices() {
        MasterService service = new MasterService(1L, "Haircut", "თმის შეჭრა", "Стрижка", "Desc En", "Desc Ka", "Desc Ru", 50.0, 60, OffsetDateTime.now());
        service.setId(10L);

        when(masterServiceRepository.findByMasterId(1L)).thenReturn(List.of(service));

        List<MasterServiceResponse> enServices = masterCatalogService.getServices(1L, "en");
        assertEquals("Haircut", enServices.get(0).getName());
        assertEquals("Desc En", enServices.get(0).getDescription());

        List<MasterServiceResponse> kaServices = masterCatalogService.getServices(1L, "ka");
        assertEquals("თმის შეჭრა", kaServices.get(0).getName());
        assertEquals("Desc Ka", kaServices.get(0).getDescription());

        List<MasterServiceResponse> ruServices = masterCatalogService.getServices(1L, "ru");
        assertEquals("Стрижка", ruServices.get(0).getName());
        assertEquals("Desc Ru", ruServices.get(0).getDescription());
    }
}
