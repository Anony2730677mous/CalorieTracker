package org.example.service.impl;

import jakarta.validation.Valid;
import org.example.entity.User;
import org.example.exception.UserNotFoundException;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    @Transactional
    public User createUser(@Valid User user) {
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(Long id, @Valid User user) {
        User existingUser = getUserByIdOrThrow(id);
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());
        existingUser.setWeight(user.getWeight());
        existingUser.setHeight(user.getHeight());
        existingUser.setGoal(user.getGoal());
        existingUser.setGender(user.getGender());

        User updatedUser = userRepository.save(existingUser);
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }
        userRepository.deleteById(id);
    }
}