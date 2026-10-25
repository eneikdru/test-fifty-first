package com.eneik.generated.service;

import com.eneik.generated.dto.MasterServiceRequest;
import com.eneik.generated.dto.MasterServiceResponse;
import com.eneik.generated.model.MasterService;
import com.eneik.generated.repository.MasterServiceRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MasterCatalogService {

    private final MasterServiceRepository masterServiceRepository;

    public MasterCatalogService(MasterServiceRepository masterServiceRepository) {
        this.masterServiceRepository = masterServiceRepository;
    }

    public List<MasterServiceResponse> getServices(Long masterId, String acceptLanguage) {
        List<MasterService> services = masterServiceRepository.findByMasterId(masterId);
        return services.stream().map(service -> mapToResponse(service, acceptLanguage)).collect(Collectors.toList());
    }

    public MasterServiceResponse addService(Long masterId, MasterServiceRequest request) {
        if (request.getPriceGel() == null || request.getPriceGel() < 0) {
            throw new IllegalArgumentException("Price in GEL must be non-negative");
        }
        if (request.getDurationMinutes() == null || request.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero");
        }

        MasterService service = new MasterService(
                masterId,
                request.getNameEn(),
                request.getNameKa(),
                request.getNameRu(),
                request.getDescriptionEn(),
                request.getDescriptionKa(),
                request.getDescriptionRu(),
                request.getPriceGel(),
                request.getDurationMinutes(),
                OffsetDateTime.now()
        );

        MasterService savedService = masterServiceRepository.save(service);
        return mapToResponse(savedService, "en");
    }

    private MasterServiceResponse mapToResponse(MasterService service, String acceptLanguage) {
        MasterServiceResponse response = new MasterServiceResponse();
        response.setId(service.getId());
        response.setMasterId(service.getMasterId());
        response.setPriceGel(service.getPriceGel());
        response.setDurationMinutes(service.getDurationMinutes());

        String lang = (acceptLanguage != null) ? acceptLanguage.toLowerCase() : "en";

        switch (lang) {
            case "ka":
                response.setName(service.getNameKa() != null ? service.getNameKa() : service.getNameEn());
                response.setDescription(service.getDescriptionKa() != null ? service.getDescriptionKa() : service.getDescriptionEn());
                break;
            case "ru":
                response.setName(service.getNameRu() != null ? service.getNameRu() : service.getNameEn());
                response.setDescription(service.getDescriptionRu() != null ? service.getDescriptionRu() : service.getDescriptionEn());
                break;
            default:
                response.setName(service.getNameEn());
                response.setDescription(service.getDescriptionEn());
                break;
        }

        return response;
    }
}
