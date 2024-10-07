package com.jbazann.customers.user;

import com.jbazann.customers.user.exceptions.InvalidUserException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * User ID provider. Values generated by this method should be
     * expected to be unique and valid for registering new User instances.
     * @return a unique user entity identifier.
     */
    public UUID generateUserId() {
        return UUID.randomUUID(); // TODO replace with safe alternative
    }

    /**
     * Persist a new valid user on the database.
     * @param user a user with an ID that is not already used.
     * @return the persisted instance.
     */
    public User newUser(@Valid @NotNull User user) {
        if (userRepository.existsById(user.id())) {
            throw new InvalidUserException("User with id " + user.id() + " already exists.");
        }
        return userRepository.save(user);
    }

    /**
     * Set a user as allowed for a given customer. No customer ID validation will be performed (so
     * invalid references may exist)
     * @param customerId a customer ID that is presumed, but not required to be valid.
     * @param userId a valid user ID.
     * @return the updated {@link User} instance.
     */
    @Transactional
    public User addAllowedUser(@NotNull UUID customerId, @NotNull UUID userId) {
        final @NotNull User user = fetchUser(userId);
        if(isEnabledFor(user, customerId)) {
            /* do not throw exception until decoupled from CustomerService
            throw new InvalidOperationException("User " + userId + " is already enabled for client " + customerId + '.') ;
            TODO log
             */
        }
        return userRepository.save(user.customer(customerId));
    }

    /**
     * Find a list of up to 5 users matching the provided example.
     * This was made this way for simplicity's sake, only intended for queries
     * expected to match with a single user.
     * @param user an example user with null fields, except for those intended to be matched against.
     * @return a size-limited list of matching results.
     */
    public List<User> findUsersByExample(User user) {
        return userRepository.findAll(Example.of(user), Pageable.ofSize(5)).toList();
    }

    private User fetchUser(@NotNull UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new InvalidUserException("User " + userId + " not found.")
        );
    }

    private boolean isEnabledFor(User user, UUID customerId) {
        return user.enabledForCustomerId(customerId);
    }

}