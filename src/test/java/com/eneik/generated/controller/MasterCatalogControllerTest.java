package com.eneik.generated.controller;

import com.eneik.generated.dto.MasterServiceRequest;
import com.eneik.generated.dto.MasterServiceResponse;
import com.eneik.generated.service.MasterCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MasterCatalogControllerTest {

    private MasterCatalogService masterCatalogService;
    private MasterCatalogController controller;

    @BeforeEach
    void setUp() {
        masterCatalogService = mock(MasterCatalogService.class);
        controller = new MasterCatalogController(masterCatalogService);
    }

    @Test
    void shouldGetServices() {
        MasterServiceResponse response = new MasterServiceResponse();
        response.setName("Haircut");
        when(masterCatalogService.getServices(1L, "en")).thenReturn(List.of(response));

        ResponseEntity<List<MasterServiceResponse>> result = controller.getServices(1L, "en");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("Haircut", result.getBody().get(0).getName());
    }

    @Test
    void shouldAddService() {
        MasterServiceRequest request = new MasterServiceRequest();
        MasterServiceResponse response = new MasterServiceResponse();
        response.setId(10L);

        when(masterCatalogService.addService(eq(1L), any(MasterServiceRequest.class))).thenReturn(response);

        ResponseEntity<?> result = controller.addService(1L, request);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(10L, ((MasterServiceResponse) result.getBody()).getId());
    }

    @Test
    void shouldReturnBadRequestWhenAddServiceFails() {
        MasterServiceRequest request = new MasterServiceRequest();
        when(masterCatalogService.addService(eq(1L), any(MasterServiceRequest.class)))
                .thenThrow(new IllegalArgumentException("Price invalid"));

        ResponseEntity<?> result = controller.addService(1L, request);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Price invalid", ((Map<?, ?>) result.getBody()).get("error"));
    }
}
