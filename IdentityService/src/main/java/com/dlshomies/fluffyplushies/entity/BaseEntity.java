package com.dlshomies.fluffyplushies.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;

import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.Instant;
import java.util.UUID;

@Data
@FilterDef(name = "softDeleteFilter", parameters = @ParamDef(name = "deleted", type = java.lang.Boolean.class))
@Filter(name = "softDeleteFilter", condition = "deleted = :deleted")
@MappedSuperclass
public class BaseEntity {

    @Id
    private UUID id;

    private Instant createdAt;

    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    private boolean deleted = false;

    @Column(unique = true)
    private UUID previousVersionId;
}