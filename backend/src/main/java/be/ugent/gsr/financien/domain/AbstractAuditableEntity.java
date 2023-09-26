package be.ugent.gsr.financien.domain;

import be.ugent.gsr.financien.model.AbstractAuditableDTO;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public abstract class AbstractAuditableEntity {

    @CreatedBy
    @Column(nullable = false, updatable = false)
    protected String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    protected String lastModifiedBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    protected OffsetDateTime lastUpdated;


    public AbstractAuditableEntity(AbstractAuditableDTO abstractAuditableDTO) {
        // do nothing since the fields above should be updated automatically.
    }
}
