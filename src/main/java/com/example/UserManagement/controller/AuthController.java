package com.example.UserManagement.controller;

import com.example.UserManagement.dto.AuthRequest;
import com.example.UserManagement.dto.LoginResponse;
import com.example.UserManagement.dto.SignupRequest;
import com.example.UserManagement.entity.User;
import com.example.UserManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody AuthRequest request){
        try{
            LoginResponse user = userService.verifyUser(request);
            return ResponseEntity.ok(Map.of(
                    "message","Login successfully",
                    "token",user.getToken(),
                    "username",user.getUsername(),
                    "email",user.getEmail()

            ));
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid Credentials"));
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<Map<String,String>> signup( @RequestBody SignupRequest request){
        try{
            User user = userService.registerUser(request);
            return ResponseEntity.ok(Map.of(
                    "message","Registered Successfully"
            ));
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error",e.getMessage()
            ));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String,Boolean>> checkEmail(@RequestParam("email") String email){
            boolean exists = userService.isEmailExists(email);
            return ResponseEntity.ok(Map.of(
                    "exists",exists
            ));
    }

}
