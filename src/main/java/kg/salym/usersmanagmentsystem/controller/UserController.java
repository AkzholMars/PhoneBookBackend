package kg.salym.usersmanagmentsystem.controller;


import kg.salym.usersmanagmentsystem.dto.ReqRes;
import kg.salym.usersmanagmentsystem.entity.OurUsers;
import kg.salym.usersmanagmentsystem.service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UsersManagementService usersManagementService;


    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reqRes) {
        return ResponseEntity.ok(usersManagementService.register(reqRes));
    }
    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes reqRes) {
        return ResponseEntity.ok(usersManagementService.login(reqRes));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes reqRes) {
        return ResponseEntity.ok(usersManagementService.refreshToken(reqRes));
    }

    @PostMapping("/public/add-users")
    public ResponseEntity<ReqRes> addUsers(@RequestBody List<ReqRes> reqRes) {
        return ResponseEntity.ok(usersManagementService.addUsers(reqRes));
    }

    @GetMapping("/public/get-users")
    public ResponseEntity<ReqRes> getOurUsers(){
        return ResponseEntity.ok(usersManagementService.getAllOurUsers());
    }



    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }


    @GetMapping("/admin/get-user/{userId}")
    public ResponseEntity<ReqRes> getUserById(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.getUserById(userId));
    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody OurUsers reqRes){
        return ResponseEntity.ok(usersManagementService.updateUser(userId, reqRes));
    }

    @PutMapping("/admin/reset-password/{userId}")
    public ResponseEntity<ReqRes> resetPassword(@PathVariable Integer userId, @RequestBody ReqRes reqRes){
        return ResponseEntity.ok(usersManagementService.resetPassword(userId, reqRes));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(usersManagementService.getMyInfo(email));
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }




}
