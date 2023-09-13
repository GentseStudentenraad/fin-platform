package be.ugent.gsr.financien.model;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public abstract class AbstractAuditDTO {

    @NotNull
    protected String createdBy;

    @NotNull
    protected String lastModifiedBy;

    @NotNull
    protected OffsetDateTime dateCreated;

    @NotNull
    protected OffsetDateTime lastUpdated;

}
