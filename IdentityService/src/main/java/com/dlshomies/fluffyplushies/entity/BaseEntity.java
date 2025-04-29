package com.dlshomies.fluffyplushies.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@FilterDef(name = "softDeleteFilter", parameters = @ParamDef(name = "deleted", type = java.lang.Boolean.class))
@Filter(name = "softDeleteFilter", condition = "deleted = :deleted")
@MappedSuperclass
public class BaseEntity extends AbstractIdentifiable {
    private boolean deleted = false;
}