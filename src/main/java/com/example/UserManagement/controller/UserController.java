package com.example.UserManagement.controller;

import com.example.UserManagement.dto.UserResponse;
import com.example.UserManagement.service.UserService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    @Autowired
    UserService userService;
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> fetchData(@RequestParam("email") String email){
        try{
            UserResponse userResponse =  userService.fetchData(email);
            return ResponseEntity.ok(userResponse);
        }catch(UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PostMapping("/profile/update-detail")
    public ResponseEntity<Map<String,String>> uploadImage(@RequestParam(name = "image",required = false)MultipartFile file,
                                                          @RequestParam("oldEmail") String oldEmail,
                                                          @RequestParam("username") String username,
                                                          @RequestParam("newEmail") String newEmail){
        String url="";
        if(file != null)
        {
            url =  userService.uploadImage(file);
        }
        try{
            String token = userService.updateUser(username,newEmail,oldEmail,url);
            return ResponseEntity.ok(Map.of(
                    "url",url,
                    "token",token
            ));
        }
        catch(UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "error","Api request has some problem"
            ));
        }

    }
}
