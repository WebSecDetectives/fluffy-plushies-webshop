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
        saveAddress(user);
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

    private void saveAddress(User user) {
        var savedAddress = getOrCreateAddress(user.getAddress());
        user.setAddress(savedAddress);
    }

    private Address getOrCreateAddress(Address reqAddress) {
        Optional<Address> existingAddress = SoftDeleteUtil.executeWithoutSoftDeleteFilter(entityManager, () ->
                addressRepository.findByStreetAndPostalCodeAndCityAndCountry(reqAddress.getStreet(), reqAddress.getPostalCode(), reqAddress.getCity(), reqAddress.getCountry())
        );

        if (existingAddress.isPresent()) {
            var activatedAddress = existingAddress.get();
            activatedAddress.setDeleted(false);
            return activatedAddress;
        }
        return addressRepository.save(reqAddress);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User updateUser(UUID currentUserId, User patch) {
        User existingUser = getExistingUser(currentUserId);

        createAndPersistSnapshot(existingUser);

        updatePhone(existingUser, patch.getPhone());

        updateAddress(existingUser, patch.getAddress());

        return userRepository.save(existingUser);
    }

    public User updatePassword(UUID currentUserId, String password) {
        User existingUser = getExistingUser(currentUserId);

        createAndPersistSnapshot(existingUser);

        encodeAndSetPassword(existingUser, password);

        return userRepository.save(existingUser);
    }

    private User getExistingUser(UUID currentUserId) {
        Optional<User> existingOptional = SoftDeleteUtil.executeWithoutSoftDeleteFilter(entityManager, () ->
                userRepository.findById(currentUserId));

        if(existingOptional.isEmpty()) {
            throw new UserNotFoundException();
        }
        User existingUser = existingOptional.get();

        if(existingUser.isDeleted()) {
            throw new UserDeletedException();
        }
        return existingUser;
    }

    private void createAndPersistSnapshot(User existingUser) {
        userHistoryRepository.save(modelMapper.map(existingUser, UserHistory.class));
    }

    private void updateAddress(User existingUser, Address patch) {
        if (patch != null) {
            existingUser.setAddress(patch);
            saveAddress(existingUser);
        }
    }

    private void updatePhone(User existingUser, String patch) {
        if (patch != null) {
            existingUser.setPhone(patch);
        }
    }
}
