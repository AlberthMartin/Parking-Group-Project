package com.example.demo.service.user;

import com.example.demo.responseDtos.UserResponseDto;
import com.example.demo.requests.user.CreateUserRequest;

public interface IUserService {

    //Gets a user by provided id from the database
    //if not found throws ResourceNotFoundException(User not found)
    UserResponseDto getUserById(Long userId);

    // Method to creates a user and saves it into the database
    // Takes CreateUserRequest in JSON form the frontend
    // Returns a dto of the created user
    //Throws AlreadyExistsException if email already exists
    UserResponseDto createUser(CreateUserRequest request);

    // Removes the user from the database
    // Takes the user id as a parameter and returns nothing
    // Throws ResourceNotFoundException if the user is not found
    void deleteUserById(Long userId);

}
