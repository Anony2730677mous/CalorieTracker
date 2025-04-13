package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.example.entity.enums.Gender;
import org.example.entity.enums.Goal;

@RequiredArgsConstructor
public class UserDTO {
    private Long id;
    @NotNull(message = "Name cannot be null")
    private String name;

    @Email(message = "Invalid email format")
    @NotNull(message = "Email cannot be null")
    private String email;

    @Min(value = 18, message = "Age must be 18 or older")
    @Max(value = 120, message = "Age must be 120 or younger")
    private int age;

    @Min(value = 30, message = "Weight must be 30 kg or more")
    @Max(value = 300, message = "Weight must be 300 kg or less")
    private double weight;

    @Min(value = 100, message = "Height must be 100 cm or more")
    @Max(value = 250, message = "Height must be 250 cm or less")
    private double height;

    @ValidGender
    private Gender gender;

    @NotNull(message = "Goal cannot be null")
    private Goal goal;

    public User toEntity() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setAge(this.age);
        user.setWeight(this.weight);
        user.setHeight(this.height);
        user.setGender(this.gender);
        user.setGoal(this.goal);
        return user;
    }

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setGender(user.getGender());
        dto.setGoal(user.getGoal());
        return dto;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}

