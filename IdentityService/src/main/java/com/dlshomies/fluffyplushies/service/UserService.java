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

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing users, their addresses, roles, and associated operations.
 * Provides functionalities for handling user registration, updates, deletions,
 * password management, and snapshot history creation.
 * This class uses {@link UserRepository}, {@link UserHistoryRepository},
 * and {@link AddressRepository} for database access and operations.
 */
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

    /**
     * Registers a new user with an admin role in the system.
     * This method ensures the provided user is stored with a role of {@code ADMIN},
     * handles address saving, encodes the provided password, and persists the user to the database.
     *
     * @param user the {@link User} object containing user's details to be registered
     * @param password the raw password of the user which will be encoded and stored securely
     * @return the {@link User} object after being saved, containing the assigned admin role
     * @throws UserAlreadyExistsException if a user with the same username or email already exists
     */
    public User registerAdminUser(User user, String password) {
        return registerWithRole(user, password, Role.ADMIN);
    }

    /**
     * Registers a new user with a user role in the system.
     * This method stores the provided user with a role of {@code USER},
     * handles saving the user's address, encodes the given password,
     * and persists the user data in the database.
     *
     * @param user the {@link User} object containing the details of the user to be registered
     * @param password the raw password provided by the user, which will be encoded for secure storage
     * @return the {@link User} object after being saved with the assigned {@code USER} role
     * @throws UserAlreadyExistsException if a user with the same username or email already exists
     */
    public User registerUser(User user, String password) {
        return registerWithRole(user, password, Role.USER);
    }

    /**
     * Registers a new user in the system with the specified role.
     * This method sets the user's role, encodes the provided password, saves the user's address,
     * and persists the updated user to the database.
     *
     * @param user the {@link User} object containing the user's details to be registered
     * @param password the raw password of the user to be securely encoded and stored
     * @param role the {@link Role} to be assigned to the user (e.g., ADMIN, USER)
     * @return the {@link User} object after being saved, inclusive of the assigned role and encoded password
     * @throws UserAlreadyExistsException if a user with the same username or email already exists
     */
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

    /**
     * Retrieves a list of all users from the repository.
     *
     * @return a list of {@link User} objects representing all users in the system
     * except those who have been marked as soft deleted (deleted=true)
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a specific user from the repository based on their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return the {@link User} object representing the user with the specified identifier
     * @throws UserNotFoundException if no user is found with the given identifier
     */
    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    /**
     * Updates the details of an existing user identified by their unique identifier.
     * The method retrieves the current user, creates a snapshot of the existing user data,
     * updates applicable fields such as phone and address, and persists the changes.
     *
     * @param currentUserId the unique identifier of the user to update
     * @param patch the {@link User} object containing the updated details (e.g., phone or address)
     * @return the updated {@link User} object after changes are saved
     * @throws UserNotFoundException if no user is found with the given identifier
     * @throws UserDeletedException if the user has been marked as deleted
     */
    public User updateUser(UUID currentUserId, User patch) {
        var existingUser = getExistingUser(currentUserId);

        createAndPersistSnapshot(existingUser);

        updatePhone(existingUser, patch.getPhone());

        updateAddress(existingUser, patch.getAddress());

        updateImgUrl(existingUser, patch.getImgUrl());

        return userRepository.save(existingUser);
    }

    private void updateImgUrl(User existingUser, URI patch) {
        if (patch != null) {
            existingUser.setImgUrl(patch);
        }
    }

    public User updatePassword(UUID currentUserId, String password) {
        var existingUser = getExistingUser(currentUserId);

        createAndPersistSnapshot(existingUser);

        encodeAndSetPassword(existingUser, password);

        return userRepository.save(existingUser);
    }

    /**
     * Marks a user as deleted in the system by setting the "deleted" flag to true.
     * This method performs a soft delete operation, meaning the user data is retained
     * in the database but is flagged as no longer active or visible.
     *
     * @param id the unique identifier of the user to be marked as deleted
     * @throws UserNotFoundException if no user is found with the provided identifier
     */
    public void deleteUser(UUID id) {
        var existingUser = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        createAndPersistSnapshot(existingUser);

        existingUser.setDeleted(true);

        userRepository.save(existingUser);
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

    /**
     * Creates and saves a snapshot of the provided user in the user history repository.
     * This method maps the current state of the given {@link User} object to a {@link UserHistory} object
     * and persists the resulting snapshot for historical tracking purposes.
     *
     * @param existingUser the {@link User} object representing the user whose snapshot is to be taken and saved
     */
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
