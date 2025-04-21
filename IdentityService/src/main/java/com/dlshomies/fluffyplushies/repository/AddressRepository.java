package com.dlshomies.fluffyplushies.repository;

import com.dlshomies.fluffyplushies.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    Optional<Address> findByStreetAndPostalCodeAndCityAndCountry(String street, String postalCode, String city, String country);
}
