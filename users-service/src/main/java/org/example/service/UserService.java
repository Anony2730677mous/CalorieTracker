package org.example.service;

import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * Получение списка всех пользователей.
     *
     * @return Список пользователей.
     */
    List<User> getAllUsers();

    /**
     * Получение пользователя по ID.
     *
     * @param id ID пользователя.
     * @return Optional с пользователем.
     */
    Optional<User> getUserById(Long id);

    /**
     * Получение пользователя по ID или выброс исключения.
     *
     * @param id ID пользователя.
     * @return Пользователь.
     */
    User getUserByIdOrThrow(Long id);

    /**
     * Создание нового пользователя.
     *
     * @param user Данные нового пользователя.
     * @return Созданный пользователь.
     */
    User createUser(User user);

    /**
     * Обновление данных существующего пользователя.
     *
     * @param id   ID пользователя.
     * @param user Данные для обновления.
     * @return Обновленный пользователь.
     */
    User updateUser(Long id, User user);

    /**
     * Удаление пользователя по ID.
     *
     * @param id ID пользователя.
     */
    void deleteUser(Long id);
}
