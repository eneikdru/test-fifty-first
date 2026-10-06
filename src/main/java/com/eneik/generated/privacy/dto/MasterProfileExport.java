package com.eneik.generated.privacy.dto;

import java.util.List;

public record MasterProfileExport(
        String masterId,
        String facebookPageId,
        String publicProfileUrl,
        String description,
        String address,
        List<String> importedPhotos,
        List<MasterServiceExport> services
) {}
