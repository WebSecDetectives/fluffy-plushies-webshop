package com.dlshomies.fluffyplushies.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.net.URI;
import java.util.UUID;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper=true)
@Entity
public class UserHistory extends BaseEntity {
    private UUID userId;
    private String username;
    private String email;
    private String phone;
    private UUID addressId;
    private String encodedPassword;
    private Role role;
    private URI imgUrl;
}
