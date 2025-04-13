package service;

import org.example.entity.User;
import org.example.entity.enums.Gender;
import org.example.entity.enums.Goal;
import org.example.exception.UserNotFoundException;
import org.example.repository.UserRepository;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserServiceImpl userService;

    private User user;

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);

        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setAge(30);
        user.setWeight(70.0);
        user.setHeight(180.0);
        user.setGoal(Goal.WEIGHT_LOSS);
        user.setGender(Gender.MALE);
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = List.of(user);
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<User> returnedUsers = userService.getAllUsers();

        Assertions.assertNotNull(returnedUsers, "Список пользователей не должен быть null");
        Assertions.assertEquals(1, returnedUsers.size(), "Ожидается 1 пользователь");
        Assertions.assertEquals(user.getName(), returnedUsers.get(0).getName(), "Имя пользователя должно совпадать");
    }

    @Test
    void getUserById_Success() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        Assertions.assertTrue(foundUser.isPresent(), "Пользователь должен быть найден");
        Assertions.assertEquals("John Doe", foundUser.get().getName(), "Имя пользователя должно совпадать");
    }

    @Test
    void getUserByIdOrThrow_Success() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByIdOrThrow(1L);

        Assertions.assertNotNull(foundUser, "Найденный пользователь не должен быть null");
        Assertions.assertEquals("john.doe@example.com", foundUser.getEmail(), "Email должен совпадать");
    }

    @Test
    void getUserByIdOrThrow_NotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserByIdOrThrow(1L)
        );
        Assertions.assertEquals("Пользователь с ID 1 не найден", exception.getMessage(), "Сообщение об ошибке должно соответствовать");
    }

    @Test
    void createUser_Success() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        Assertions.assertNotNull(createdUser, "Созданный пользователь не должен быть null");
        Assertions.assertEquals("John Doe", createdUser.getName(), "Имя пользователя должно совпадать");
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void updateUser_Success() {
        User updateData = new User();
        updateData.setName("Jane Doe");
        updateData.setEmail("jane.doe@example.com");
        updateData.setAge(28);
        updateData.setWeight(65.0);
        updateData.setHeight(170.0);
        updateData.setGoal(Goal.MAINTENANCE);
        updateData.setGender(Gender.FEMALE);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUser(1L, updateData);

        Assertions.assertNotNull(updatedUser, "Обновленный пользователь не должен быть null");
        Assertions.assertEquals("Jane Doe", updatedUser.getName(), "Имя пользователя должно обновиться");
        Assertions.assertEquals("jane.doe@example.com", updatedUser.getEmail(), "Email должен обновиться");
        Assertions.assertEquals(28, updatedUser.getAge(), "Возраст должен обновиться");
        Assertions.assertEquals(65.0, updatedUser.getWeight(), "Вес должен обновиться");
        Assertions.assertEquals(170.0, updatedUser.getHeight(), "Рост должен обновиться");
        Assertions.assertEquals(Goal.MAINTENANCE, updatedUser.getGoal(), "Цель должна обновиться");
        Assertions.assertEquals(Gender.FEMALE, updatedUser.getGender(), "Пол должен обновиться");

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(updatedUser);
    }

    @Test
    void deleteUser_Success() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(1L)
        );
        Assertions.assertEquals("Пользователь с ID 1 не найден", exception.getMessage(), "Сообщение об ошибке должно соответствовать");
    }
}
