package com.example.UserManagement.controller;

import com.example.UserManagement.dto.UserResponse;
import com.example.UserManagement.service.UserService;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    UserService userService;


    @GetMapping("/get-all-user")
    public List<UserResponse> getUsers(){
        return userService.getUsers();
    }

    @PostMapping("/profile/update-detail")
    public ResponseEntity<Map<String,String>> updateUser(@RequestParam("username") String username,
                                                         @RequestParam("email") String email,
                                                         @RequestParam("id") String id,
                                                         @RequestParam("password") String password){
        try{
            userService.updateUserAdmin(username,email,Long.parseLong(id),password);
            return ResponseEntity.ok(Map.of(
                    "message","Updated successfully"
            ));
        }
        catch(UsernameNotFoundException e ){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "error","User details not updated"
            ));
        }
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<Map<String,String>> deleteUser(@RequestParam("id") Long id){
        try{
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of(
                    "message","Deleted successfully"
            ));
        }
        catch(UsernameNotFoundException e ){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "error","User not found"
            ));
        }
    }

    @PutMapping("/block-user")
    public ResponseEntity<Map<String,String>> blockUser(@RequestParam("id") Long id){
        try{
            userService.blockUser(id);
            return ResponseEntity.ok(Map.of(
                    "message","User blocked successfully"
            ));
        }
        catch(UsernameNotFoundException e ){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "error","User not found"
            ));
        }
    }

    @PostMapping("/add-user")
    public ResponseEntity<Map<String,String>> addUser(@RequestParam("username") String username,
                                                      @RequestParam("email") String email,
                                                      @RequestParam("password") String password){
        try{
            userService.addUser(username,email,password);
            return ResponseEntity.ok(Map.of(
                    "message","New user created successfully"
            ));
        }
        catch(UsernameNotFoundException e ){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "error","Adding new user failed"
            ));
        }
    }



}

