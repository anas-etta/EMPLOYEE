package com.example.batch.Mapper;

import com.example.batch.DTO.BatchTraitementDTO;
import com.example.batch.Entities.BatchTraitement;

public class BatchTraitementMapper {
    public static BatchTraitement toEntity(BatchTraitementDTO dto) {
        if (dto == null) return null;
        return new BatchTraitement(
                dto.getNomFichier(),
                dto.getNbrLigne(),
                dto.getNbrLigneValide(),
                dto.getNbrLigneInvalide(),
                dto.getStartTime(),
                dto.getStopTime()
        );
    }
    public static BatchTraitementDTO toDTO(BatchTraitement entity) {
        if (entity == null) return null;
        return new BatchTraitementDTO(
                entity.getId(),
                entity.getNomFichier(),
                entity.getNbrLigne(),
                entity.getNbrLigneValide(),
                entity.getNbrLigneInvalide(),
                entity.getStartTime(),
                entity.getStopTime()
        );
    }
}