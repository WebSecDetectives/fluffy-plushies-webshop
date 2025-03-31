package com.dlshomies.fluffyplushies.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class Address extends BaseEntity {

    @NotNull
    private String street;

    @NotNull
    private int postalCode;

    @NotNull
    private String city;

    @NotNull
    private String country;

    @OneToMany(mappedBy = "address")
    private List<User> users;
}
