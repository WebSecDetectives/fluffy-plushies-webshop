package com.dlshomies.fluffyplushies.service;

import com.dlshomies.fluffyplushies.entity.Address;
import com.dlshomies.fluffyplushies.entity.Role;
import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.entity.UserHistory;
import com.dlshomies.fluffyplushies.exception.UserAlreadyExistsException;
import com.dlshomies.fluffyplushies.exception.UserDeletedException;
import com.dlshomies.fluffyplushies.exception.UserNotFoundException;
import com.dlshomies.fluffyplushies.repository.AddressRepository;
import com.dlshomies.fluffyplushies.repository.UserHistoryRepository;
import com.dlshomies.fluffyplushies.repository.UserRepository;
import com.dlshomies.fluffyplushies.util.SoftDeleteUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public User registerAdminUser(User user, String password) {
        return registerWithRole(user, password, Role.ADMIN);
    }

    public User registerUser(User user, String password) {
        return registerWithRole(user, password, Role.USER);
    }

    private User registerWithRole(User user, String password, Role role) {
        if(userExists(user)) {
            throw new UserAlreadyExistsException("username/email", user.getUsername() + " / " + user.getEmail());
        }
        user.setRole(role);
        setAddress(user);
        encodeAndSetPassword(user, password);

        return userRepository.save(user);
    }

    private Boolean userExists(User user) {
        return SoftDeleteUtil.executeWithoutSoftDeleteFilter(entityManager, () ->
                userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())
        );
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

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User updateUser(UUID currentUserId, User user) {
        Optional<User> existingOptional = SoftDeleteUtil.executeWithoutSoftDeleteFilter(entityManager, () ->
                userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail()));

        if(existingOptional.isEmpty()) {
            throw new UserNotFoundException();
        }
        User existingUser = existingOptional.get();

        if(existingUser.isDeleted()) {
            throw new UserDeletedException();
        }
        // create new instance of user history based on the old one
        // persist user history as snapshot
        UserHistory snapshot = modelMapper.map(existingUser, UserHistory.class);
        userHistoryRepository.save(snapshot);

        // update permitted fields explicitly to avoid background magic errors
        updatePermittedFields(user, existingUser);

        // save updated user and return
        return userRepository.save(existingUser);
    }

    private void updatePermittedFields(User user, User existingUser) {
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getPassword() != null) {
            encodeAndSetPassword(existingUser, user.getPassword());
        }
    }
}
