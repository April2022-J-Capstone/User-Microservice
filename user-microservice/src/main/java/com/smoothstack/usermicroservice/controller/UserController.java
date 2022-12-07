package com.smoothstack.usermicroservice.controller;

import com.smoothstack.common.exceptions.*;
import com.smoothstack.common.models.Location;
import com.smoothstack.common.models.User;
import com.smoothstack.common.models.UserInformation;
import com.smoothstack.common.models.UserRole;
import com.smoothstack.common.repositories.LocationRepository;
import com.smoothstack.usermicroservice.data.UserInformationBuild;
import com.smoothstack.usermicroservice.data.rest.ResetPasswordBody;
import com.smoothstack.usermicroservice.data.rest.SendConfirmEmailBody;
import com.smoothstack.usermicroservice.data.rest.SendResetPasswordBody;
import com.smoothstack.usermicroservice.service.EmailConfirmationService;

import com.smoothstack.usermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("user")
public class UserController {
    
    @Autowired
    EmailConfirmationService emailConfirmationService;
    @Autowired
    UserService userService;

    /**
     * Gets a list of all users and user ids in the database
     */
    @GetMapping("all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Gets a list of all users + userInformation and user ids in the database
     */
    @GetMapping("allUserInformation")
    public List<UserInformationBuild> getAllUserInformation() {
        return userService.getAllUserInformation();
    }




    @GetMapping("username/{username}")
    public User getUserByUserName(@PathVariable(name = "username") String username) {
        try {
            User user = userService.getUserByUsername(username);
            return user;
        } catch(UserNotFoundException e ){
            System.out.println("username: " + username);
            return null;
        }
    }

    @GetMapping("exists/username/{username}")
    public ResponseEntity getUserExists(@PathVariable String username) {
        try {
            return ResponseEntity.ok().body(userService.usernameExists(username));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("exists/email/{email}")
    public ResponseEntity getEmailExists(@PathVariable String email) {
        try {
            return ResponseEntity.ok().body(userService.emailExists(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * The url used to get a usersInformation
     *
     * @param userId
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @GetMapping("userInformation/id/{userId}")
    public UserInformationBuild getUserInformationByUserId(@PathVariable(name="userId") Integer userId){
        try {
            UserInformationBuild userInformationBuild = userService.getUserInformationById(userId);
            return userInformationBuild;
        } catch(Exception e){
            System.out.print("userId: " + userId);
        }
        return null;
    }

    @GetMapping("userInformation/username/{username}")
    public ResponseEntity getUserInformationByUsername(@PathVariable String username){
        try {
            return ResponseEntity.ok().body(userService.getUserInformationByUsername(username));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{userId}/locations/saved")
    public ResponseEntity getAllUserSavedLocations(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok().body(userService.getAllUsersSavedLocations(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{userId}/reviews/restaurant/{restaurantId}/exists")
    public ResponseEntity getUserReviewRestaurantExist(@PathVariable Integer userId, @PathVariable Integer restaurantId) {
        try {
            return ResponseEntity.ok().body(userService.userReviewedRestaurant(userId, restaurantId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{userId}/reviews/driver")
    public ResponseEntity getAllUserDriverReviews(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok().body(userService.findAllUserReviews(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Not finished
    /*@GetMapping("{userId}/reviews/restaurants")
    public ResponseEntity getAllUserRestaurantReviews(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok().body(userService.findAllUserRestaurantReviews(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }*/



    @GetMapping("id/{userId}")
    public User getUserByUserId(@PathVariable(name = "userId") Integer userId) {
        //TODO
        return null;
    }

    /**
     * The url used to create a user
     *
     * @param user
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @PostMapping(value = "create-user/{roleName}")
    public ResponseEntity createUser(@RequestBody User user, @PathVariable String roleName) {
        try {
            Integer createdId = userService.createUser(user, roleName);
            return ResponseEntity.accepted().body("User created with id:" + createdId);
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * The url used to create a user + userInformation
     *
     * @param userInformationBuild
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @PostMapping(value = "create-user-information")
    public ResponseEntity createUserInformation(@RequestBody UserInformationBuild userInformationBuild) {
        try {
            System.out.println(userInformationBuild);
            Integer createdId = userService.createUserInformation(userInformationBuild);
            return ResponseEntity.accepted().body("User created with id:" + createdId);
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value="update-user")
    public ResponseEntity updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return ResponseEntity.accepted().body("Updated user");
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * The url used to update a user + userInformation
     *
     * @param userInformationBuild
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @PostMapping(value = "update-user-information")
    public ResponseEntity updateUserInformation(@RequestBody UserInformationBuild userInformationBuild) {
        try {
            userService.updateUserInformation(userInformationBuild);
            return ResponseEntity.accepted().body("Updated userInformation");
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value="delete-user")
    public ResponseEntity deleteUser(@RequestBody User user) {
        //TODO
        return null;
    }

    /**
     * The url used to update a user + userInformation
     *
     * @param userInformationBuild
     * @return 201 on success, 400 on invalid parameters, or 500 in case of database error
     */
    @PostMapping(value = "set-user-active-information")
    public ResponseEntity setUserActiveInformation(@RequestBody UserInformationBuild userInformationBuild) {
        try {
            userService.setUserActiveInformation(userInformationBuild);
            return ResponseEntity.accepted().body("User account status updated.");
        } catch (InsufficientInformationException | UsernameTakenException | InsufficientPasswordException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping(value = "confirmationMessage")
    public ResponseEntity<String> confirmationMessage(@RequestBody SendConfirmEmailBody body) {
        try {
            emailConfirmationService.sendConfirmEmail(body);
            return ResponseEntity.status(HttpStatus.OK).body("Sent successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (SendMsgFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send");
        }
    }

    @PostMapping(value = "resetPasswordMessage")
    public ResponseEntity<String> resetPasswordMessage(@RequestBody SendResetPasswordBody body) {
        try {
            emailConfirmationService.sendResetPassword(body);
            return ResponseEntity.status(HttpStatus.OK).body("Sent successfully");
        } catch (SendMsgFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send");
        }
    }

    @PutMapping(value = "confirmation")
    public ResponseEntity<String> confirmation(@RequestParam(name = "token") String token) {
        try {
            emailConfirmationService.confirmEmail(token);
            return ResponseEntity.status(HttpStatus.OK).body("Email confirmed");
        } catch (TokenInvalidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }
    }

    @PostMapping(value = "update-password")
    public ResponseEntity updatePassword(@RequestBody UserInformationBuild userInformationBuild) {
        try{
            return ResponseEntity.accepted().body(userService.updatePassword(userInformationBuild));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "resetPassword")
    public ResponseEntity<String> resetPassword(
            @RequestParam(name = "token") String token,
            @RequestBody ResetPasswordBody body) {
        try {
            emailConfirmationService.resetPassword(token, body);
            return ResponseEntity.status(HttpStatus.OK).body("Password set successfully");
        } catch (TokenInvalidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        } catch (InsufficientPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password does not meet requirements");
        } catch (MsgInvalidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid or expired");
        }
    }

    @PostMapping(value="/{username}/add-role")
    public ResponseEntity addUserRole(@PathVariable String username, @RequestBody UserRole userRole) {
        try {
            return ResponseEntity.ok().body(userService.updateUserByUsername(username, userRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("{userId}/locations/saved")
    public ResponseEntity postUserNewSavedLocation(@PathVariable Integer userId, @RequestBody Location location) {
        try {
            return ResponseEntity.ok().body(userService.addUserNewSavedLocation(userId, location));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
