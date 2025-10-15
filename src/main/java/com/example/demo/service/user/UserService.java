package com.example.demo.service.user;

import com.example.demo.dto.UserDto;
import com.example.demo.exeptions.AlreadyExistsException;
import com.example.demo.exeptions.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.requests.user.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return convertUserToDto(user);
    }

    @Override
    public UserDto createUser(CreateUserRequest request) {
        User usr = Optional.of(request) //can be null
                .filter(user -> !userRepository.existsByEmail(request.getEmail())) //Checks that user is not in the database
                .map(req -> {
                    //Create the user
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    return userRepository.save(user); // save user

                }).orElseThrow(() -> new AlreadyExistsException(request.getEmail() + " already exists"));

        return convertUserToDto(usr);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository::delete,
                () -> {
                    throw new ResourceNotFoundException("User not found");
                });
    }

    //Helper method to convert user data into save dto data
    //to send to the frontend.
    private UserDto convertUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
