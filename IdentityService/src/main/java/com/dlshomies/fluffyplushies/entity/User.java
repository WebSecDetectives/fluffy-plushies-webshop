package com.dlshomies.fluffyplushies.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class User extends BaseEntity {

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    @Column(unique = true)
    @Email
    private String email;

    @NotNull
    private int phone;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    private String encodedPassword;
}
