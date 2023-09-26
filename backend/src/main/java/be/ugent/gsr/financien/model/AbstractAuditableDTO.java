package be.ugent.gsr.financien.model;


import be.ugent.gsr.financien.domain.AbstractAuditableEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractAuditableDTO {

    @NotNull
    protected String createdBy;

    @NotNull
    protected String lastModifiedBy;

    @NotNull
    protected OffsetDateTime dateCreated;

    @NotNull
    protected OffsetDateTime lastUpdated;

    public AbstractAuditableDTO(AbstractAuditableEntity entity) {
        // Populating audit fields from AbstractAuditableEntity
        this.setCreatedBy(entity.getCreatedBy());
        this.setLastModifiedBy(entity.getLastModifiedBy());
        this.setDateCreated(entity.getDateCreated());
        this.setLastUpdated(entity.getLastUpdated());

    }
}
