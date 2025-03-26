package com.example.UserManagement.service;

import com.example.UserManagement.Enum.Role;
import com.example.UserManagement.dto.AuthRequest;
import com.example.UserManagement.dto.LoginResponse;
import com.example.UserManagement.dto.SignupRequest;
import com.example.UserManagement.dto.UserResponse;
import com.example.UserManagement.entity.User;
import com.example.UserManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtService jwtService;


    public User registerUser(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(),request.getEmail(),hashedPassword, Role.ROLE_USER);
        userRepository.save(user);
        return user;
    }

    public LoginResponse verifyUser(AuthRequest request)throws RuntimeException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
        );
        if(authentication.isAuthenticated())
        {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(()->new RuntimeException("Role not found"))
                    .getAuthority();
            String token = jwtService.generateToken(request.getEmail(),role);
            String username = userDetails.getUsername();
            return new LoginResponse(username,token,request.getEmail());
        }
        else
        {
            throw new RuntimeException("Invalid Credentials");
        }
    }

    public List<UserResponse> getUsers() {
        List<UserResponse> userResponses = new ArrayList<>();
        List<User> users =  userRepository.findAll();
        for(User user : users){
            userResponses.add(new UserResponse(user.getUsername(),user.getEmail(), user.getProfilePicture()));
        }
        return userResponses;
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserResponse fetchData(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserResponse(user.getUsername(),user.getEmail(),user.getProfilePicture());
    }

    public String uploadImage(MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();
        String apiKey = "7e0f38e7f46e9d156eda2788db00bb39";
        String IMGBB_URL = "https://api.imgbb.com/1/upload?key="+apiKey;
        try {
            // Create request body
            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("image", file.getResource());  // Correct way to send file in Multipart

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create HTTP entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Make POST request to ImgBB
            ResponseEntity<Map> response = restTemplate.exchange(IMGBB_URL, HttpMethod.POST, requestEntity, Map.class);

            // Extract URL from response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                return data.get("url").toString();  // Return uploaded image URL
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;  // Return null if upload fails
    }
}
