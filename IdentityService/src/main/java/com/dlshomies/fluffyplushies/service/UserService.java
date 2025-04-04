package com.dlshomies.fluffyplushies.service;

import com.dlshomies.fluffyplushies.entity.Address;
import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.exception.UserAlreadyExistsException;
import com.dlshomies.fluffyplushies.repository.AddressRepository;
import com.dlshomies.fluffyplushies.repository.UserRepository;
import com.dlshomies.fluffyplushies.util.SoftDeleteUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @PersistenceContext
    private EntityManager entityManager;

    public User registerUser(User user, String password) {
        boolean userExists = SoftDeleteUtil.executeWithoutSoftDeleteFilter(entityManager, () ->
            userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())
        );

        if(userExists) {
            throw new UserAlreadyExistsException("username/email", user.getUsername() + " / " + user.getEmail());
        }

        setAddress(user);

        encodeAndSetPassword(user, password);

        return userRepository.save(user);
    }

    private void encodeAndSetPassword(User user, String password) {
        user.setEncodedPassword(passwordEncoder.encode(password));
    }

    private void setAddress(User user) {
        Address reqAddress = user.getAddress();
        Optional<Address> existingAddress = SoftDeleteUtil.executeWithoutSoftDeleteFilter(entityManager, () ->
                addressRepository.findByStreetAndPostalCodeAndCityAndCountry(reqAddress.getStreet(), reqAddress.getPostalCode(), reqAddress.getCity(), reqAddress.getCountry())
        );

        if (existingAddress.isPresent()) {
            Address activatedAddress = existingAddress.get();
            activatedAddress.setDeleted(false);
            user.setAddress(activatedAddress);
        } else {
            user.setAddress(addressRepository.save(reqAddress));
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
