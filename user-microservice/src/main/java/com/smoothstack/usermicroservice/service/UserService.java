package com.smoothstack.usermicroservice.service;

import com.smoothstack.common.exceptions.InsufficientInformationException;
import com.smoothstack.common.exceptions.InsufficientPasswordException;
import com.smoothstack.common.exceptions.UserNotFoundException;
import com.smoothstack.common.exceptions.UsernameTakenException;
import com.smoothstack.common.models.*;
import com.smoothstack.common.repositories.*;
import com.smoothstack.usermicroservice.data.UserInformationBuild;
import com.smoothstack.common.services.CommonLibraryTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserInformationRepository userInformationRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    CommunicationMethodRepository communicationMethodRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    ReviewRepository reviewRepository;

    /**
     * Returns a boolean depending on whether there is a user in the database with said username
     *
     * @param username the username of the user
     * @return true if a user with that username exists otherwise false
     */
    public boolean usernameExists(String username) {
        if (username == null)
            return false;

        return userRepository.findTopByUserName(username).isPresent();
    }

    /**
     * Returns a boolean depending on whether there is a user in the database with a provided id
     *
     * @param id the id of the perspective user
     * @return true if user exists with this id, otherwise false
     */
    public boolean userIdExists(Integer id) {
        if (id == null)
            return false;

        return userRepository.findById(id).isPresent();
    }

    /**
     * Returns the user in the database with the associated username
     *
     * @param username the username of the user to search
     * @return The user whose username matches the provided username
     * @throws UserNotFoundException if a user with the associated username cannot be found
     */
    public User getUserByUsername(String username) throws UserNotFoundException {
        if (usernameExists(username)) {
            return userRepository.findTopByUserName(username).get();
        }
        throw new UserNotFoundException("No user with username:" + username);
    }

    /**
     * Returns the user in the database with the associated user id
     *
     * @param id the users id
     * @return the user associated with an id
     * @throws UserNotFoundException if a user with the associated id is not found
     */
    public User getUserById(Integer id) throws  UserNotFoundException {
        if (userIdExists(id)) {
            return userRepository.findById(id).get();
        }
        throw new UserNotFoundException("No user with id:" + id);
    }

    /**
     * Returns a boolean value based on whether a given password is considered acceptable
     *
     * @param password
     * @return true if password is ok, false if password is not valid
     */
    public boolean validPassword(String password) {
        //TODO
        return true;
    }

    /**
     * Adds a given user to the database
     *
     * @param user the user to be added
     * @return the id of the created user
     */
    public Integer createUser(User user, String roleName) throws InsufficientInformationException, UsernameTakenException, InsufficientPasswordException {
        if (user == null) throw new InsufficientInformationException("User not provided");
        if (user.getUserName() == null) throw new InsufficientInformationException("Username not provided");
        if (user.getPassword() == null) throw new InsufficientInformationException("Password not provided");
        if (usernameExists(user.getUserName())) throw new UsernameTakenException("Username is taken");
        if (!validPassword(user.getPassword())) throw new InsufficientPasswordException("Password is insufficient");
        if(roleName == null) throw new RuntimeException("Role name not provided");

        Optional<UserRole> optionalUserRole = userRoleRepository.findTopByRoleName(roleName);
        if(optionalUserRole.isEmpty()) throw new RuntimeException("Role doesn't exist");

        user.addRole(optionalUserRole.get());
        UserInformation newInformation = new UserInformation();
        newInformation.setUser(user);
        user.setUserInformation(newInformation);
        return userRepository.save(user).getId();
    }

    /**
     * Adds a given user + userInformation to the database
     *
     * @param userInformationBuild the user to be added
     * @return the id of the created user
     */
    public Integer createUserInformation(UserInformationBuild userInformationBuild) throws InsufficientInformationException, UsernameTakenException, InsufficientPasswordException {
        User newUser = new User();
        UserInformation newUserInformation = new UserInformation();

        System.out.println("user data: " + userInformationBuild.getUserName());
        System.out.println("userInformation data: " + userInformationBuild.getFirst_name());

        if (userInformationBuild == null) throw new InsufficientInformationException("User not provided");
        if (userInformationBuild.getUserName() == null) throw new InsufficientInformationException("Username not provided");
        if (userInformationBuild.getPassword() == null) throw new InsufficientInformationException("Password not provided");
        if (usernameExists(userInformationBuild.getUserName())) throw new UsernameTakenException("Username is taken");
        if (!validPassword(userInformationBuild.getPassword())) throw new InsufficientPasswordException("Password is insufficient");
        if(userInformationBuild.getFirst_name() == null) throw new InsufficientInformationException("First Name not provided");
        if(userInformationBuild.getLast_name() == null) throw new InsufficientInformationException("Last Name not provided");
        if(userInformationBuild.getEmail() == null) throw new InsufficientInformationException("Email not provided");
        if(userInformationBuild.getPhone_number() == null) throw new InsufficientInformationException("Phone number not provided");
        if(userInformationBuild.getBirthdate() == null) throw new InsufficientInformationException("Birth date not provided");
        if(userInformationBuild.getUserRole() == null) throw  new RuntimeException("User role not provided");
        if(userInformationBuild.getCommunication_type_id() == null) throw new RuntimeException("Communication type id not provided");

        Optional<UserRole> userRoleOptional = userRoleRepository.findTopByRoleName(userInformationBuild.getUserRole());
        if(userRoleOptional.isPresent()) {
            newUser.addRole(userRoleOptional.get());
        } else {
            throw new RuntimeException("User role doesn't exist");
        }

        Optional<CommunicationMethod> communicationMethodOptional = communicationMethodRepository.findById(userInformationBuild.getCommunication_type_id());
        if(communicationMethodOptional.isPresent()) {
            newUserInformation.setCommunicationType(communicationMethodOptional.get());
        } else {
            throw new RuntimeException("Communication Method doesn't exist");
        }

        newUserInformation.setUser(newUser);
        newUser.setUserName(userInformationBuild.getUserName());
        newUser.setPassword(userInformationBuild.getPassword());
        newUserInformation.setFirstName(userInformationBuild.getFirst_name());
        newUserInformation.setLastName(userInformationBuild.getLast_name());
        newUserInformation.setEmail(userInformationBuild.getEmail());
        newUserInformation.setPhoneNumber(userInformationBuild.getPhone_number());
        newUserInformation.setBirthdate(userInformationBuild.getBirthdate());
        newUserInformation.setVeteranStatus(userInformationBuild.getVeteran_status());
        newUserInformation.setEmailConfirmed(userInformationBuild.getEmail_confirmed());
        newUserInformation.setAccount_active(userInformationBuild.getAccount_active());
        newUser.setUserInformation(newUserInformation);

        return userRepository.save(newUser).getId();
    }

    /**
     * updates a user based off of a provided userid
     *
     * @param user a user containing the userid of the user to update and the other fields to update
     */
    public void updateUser(User user) throws UserNotFoundException, InsufficientInformationException,
            UsernameTakenException, InsufficientPasswordException{
        if (user == null) throw new InsufficientInformationException("User not provided");
        if (user.getId() == null) throw new InsufficientInformationException("User Id not provided");
        User toUpdate = getUserById(user.getId());
        if (!user.getUserName().equals(toUpdate.getUserName())) {
            if (usernameExists(user.getUserName())) throw new UsernameTakenException("Username is taken");
            toUpdate.setUserName(user.getUserName());
        }
        if (!user.getPassword().equals(toUpdate.getPassword())) {
            if (!validPassword(user.getPassword())) throw new InsufficientPasswordException("Password is invalid");
            toUpdate.setPassword(user.getPassword());
        }

        userRepository.save(toUpdate);
    }


    /**
     * updates a user + userInformation based off of the userid provided in the userInformationBuild
     *
     * @param userInformationBuild a userInformationBuild containing the userid of the user to update and the other fields to update
     */
    public void updateUserInformation(UserInformationBuild userInformationBuild) throws InsufficientInformationException, UsernameTakenException, InsufficientPasswordException, UserNotFoundException {
        if (userInformationBuild == null) throw new InsufficientInformationException("User not provided");
        if (userInformationBuild.getUsers_Id() == null) throw new InsufficientInformationException("User Id not provided");
        User updateUser = getUserById(userInformationBuild.getUsers_Id());
        UserInformation updateUserInformation = userInformationRepository.getById(userInformationBuild.getUsers_Id());
        updateUserInformation.setUser(updateUser);
        updateUserInformation.setFirstName(userInformationBuild.getFirst_name());
        updateUserInformation.setLastName(userInformationBuild.getLast_name());
        updateUserInformation.setEmail(userInformationBuild.getEmail());
        updateUserInformation.setPhoneNumber(userInformationBuild.getPhone_number());
        updateUserInformation.setBirthdate(userInformationBuild.getBirthdate());
        updateUserInformation.setVeteranStatus(userInformationBuild.getVeteran_status());
        updateUserInformation.setEmailConfirmed(userInformationBuild.getEmail_confirmed());
        updateUserInformation.setAccount_active(userInformationBuild.getAccount_active());

        updateUser.setUserInformation(updateUserInformation);
        userRepository.save(updateUser);
    }

    /**
     * updates a user + userInformation based off of the userid provided in the userInformationBuild
     *
     * @param userInformationBuild a userInformationBuild containing the userid of the user to update and the other fields to update
     */
    public void setUserActiveInformation(UserInformationBuild userInformationBuild) throws InsufficientInformationException, UsernameTakenException, InsufficientPasswordException, UserNotFoundException {
        if (userInformationBuild == null) throw new InsufficientInformationException("User not provided");
        if (userInformationBuild.getUsers_Id() == null) throw new InsufficientInformationException("User Id not provided");
        User updateUser = getUserById(userInformationBuild.getUsers_Id());
        UserInformation updateUserInformation = userInformationRepository.getById(userInformationBuild.getUsers_Id());

        updateUserInformation.setUser(updateUser);
        updateUser.setEnabled(userInformationBuild.getEnabled());
        updateUserInformation.setAccount_active(userInformationBuild.getAccount_active());

        updateUser.setUserInformation(updateUserInformation);
        userRepository.save(updateUser);
    }



    /**
     * Deletes a user from the database
     *
     * @param userid: integer value of user id
     */
    public void deleteUser(Integer userid) {
        userRepository.deleteById(userid);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        for (User user: userRepository.findAll()) {
            users.add(User.builder()
                    .id(user.getId())
                    .userName(user.getUserName())
                    .build()
            );
        }

        return users;
    }

    public List<UserInformationBuild> getAllUserInformation() {
        List<UserInformationBuild> userInformationBuild = new ArrayList<>();

        for (UserInformation dbUserInformation: userInformationRepository.findAll()) {
                userInformationBuild.add(getUserInformationBuild(dbUserInformation.getId()));
                System.out.println("userInformation: " + dbUserInformation.getId());
        }
        return userInformationBuild;
    }


    public UserInformationBuild getUserInformationById(Integer userId) throws  UserNotFoundException {
        if (userIdExists(userId)) {
//        Optional<UserInformation> userInformation = userInformationRepository.findById(userId);
//        UserInformation userInformation1 = userInformation.get();
            UserInformationBuild userInformationBuild1 = getUserInformationBuild(userId);
        return userInformationBuild1;
        }
        throw new UserNotFoundException("No user with id:" + userId);
    }



    /**
     * build the userInformationBuild object for several functions listed above that work based off of userInformatoinBuild
     *
     * @param userId the user to be built
     * @return the userInformationBuild object created
     */
    public UserInformationBuild getUserInformationBuild(Integer userId){
        Optional<User> user = userRepository.findById(userId);
        User user1 = user.get();
        Optional<UserInformation> userInformation = userInformationRepository.findById(userId);
        UserInformation userInformation1 = userInformation.get();
        UserInformationBuild userInformationBuild = new UserInformationBuild();

        userInformationBuild.setUsers_Id(user1.getId());
        userInformationBuild.setUserName(user1.getUserName());
        userInformationBuild.setEnabled(user1.isEnabled());
        userInformationBuild.setFirst_name(userInformation1.getFirstName());
        userInformationBuild.setLast_name(userInformation1.getLastName());
        userInformationBuild.setEmail(userInformation1.getEmail());
        userInformationBuild.setPhone_number(userInformation1.getPhoneNumber());
        userInformationBuild.setBirthdate(userInformation1.getBirthdate());
        userInformationBuild.setVeteran_status(userInformation1.getVeteranStatus());
        userInformationBuild.setEmail_confirmed(userInformation1.getEmailConfirmed());
        userInformationBuild.setAccount_active(userInformation1.getAccount_active());
        return userInformationBuild;
    }


    /**
     * Returns a login pojo so the authentication microservice can authenticate a user
     *
     * @param username
     * @return
     */
    public User getLoginInfo(String username) {
        System.out.println("here foo");
        Optional<User> foundUser = userRepository.findTopByUserName(username);

        if (foundUser.isPresent()) {
            User user = foundUser.get();

            User.UserBuilder loginBuilder = User.builder()
                    .userName(user.getUserName())
                    .password(user.getPassword())
                    .enabled(user.isEnabled());

            if (!user.getUserRoles().isEmpty()) {
                List<UserRole> roles = new ArrayList<>();

                for (UserRole role: user.getUserRoles()) {
                    System.out.println(role);
                    roles.add(UserRole.builder()
                            .roleName(role.getRoleName())
                            .build()
                    );
                }

                loginBuilder.userRoles(roles);
            }

            System.out.println(loginBuilder.toString());
            return loginBuilder.build();
        }

        return null;
    }

    public Boolean emailExists(String email) {
        return userInformationRepository.existsByEmail(email);
    }

    public Boolean updateUserByUsername(String username, UserRole userRole) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findTopByUserName(username);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<UserRole> userRoleOptional = userRoleRepository.findTopByRoleName(userRole.getRoleName());
            if(userRoleOptional.isPresent()) {
                user.addRole(userRole);
                userRepository.saveAndFlush(user);
                return true;
            } else {
                throw new RuntimeException("User role doesn't exist");
            }

        } else {
            throw new UserNotFoundException("User doesn't exist");
        }
    }

    public UserInformationBuild getUserInformationByUsername(String username) {
        Optional<User> user = userRepository.findTopByUserName(username);
        User user1 = user.get();
        Optional<UserInformation> userInformation = userInformationRepository.findById(user1.getId());
        UserInformation userInformation1 = userInformation.get();
        UserInformationBuild userInformationBuild = new UserInformationBuild();

        userInformationBuild.setUsers_Id(user1.getId());
        userInformationBuild.setUserName(user1.getUserName());
        userInformationBuild.setPassword(user1.getPassword());
        userInformationBuild.setEnabled(user1.isEnabled());
        userInformationBuild.setFirst_name(userInformation1.getFirstName());
        userInformationBuild.setLast_name(userInformation1.getLastName());
        userInformationBuild.setEmail(userInformation1.getEmail());
        userInformationBuild.setPhone_number(userInformation1.getPhoneNumber());
        userInformationBuild.setBirthdate(userInformation1.getBirthdate());
        userInformationBuild.setVeteran_status(userInformation1.getVeteranStatus());
        userInformationBuild.setEmail_confirmed(userInformation1.getEmailConfirmed());
        userInformationBuild.setAccount_active(userInformation1.getAccount_active());
        return userInformationBuild;
    }

    public Boolean updatePassword(UserInformationBuild userInformationBuild) {
        Optional<User> userOptional = userRepository.findTopByUserName(userInformationBuild.getUserName());
        userOptional.get().setPassword(userInformationBuild.getPassword());
        userRepository.saveAndFlush(userOptional.get());
        return true;
    }

    public List<Location> getAllUsersSavedLocations(Integer userId) {
        User user = userRepository.getById(userId);
        return user.getSavedLocations();
    }

    @Transactional
    public Location addUserNewSavedLocation(Integer userId, Location location) {
        System.out.println(location.getLocationName());
        System.out.println(location.getAddress());
        System.out.println(location.getCity());
        System.out.println(location.getState());
        System.out.println(location.getZipCode());

        Location newLocation = locationRepository.saveAndFlush(location);
        User user = userRepository.getById(userId);
        user.getSavedLocations().add(location);
        user.setSavedLocations(user.getSavedLocations());
        userRepository.saveAndFlush(user);
        return newLocation;
    }

    public Boolean userReviewedRestaurant(Integer userId, Integer restaurantId) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
        if(restaurantOptional.isPresent()) {
            Review review = restaurantRepository.findByReviews_User_id(userId);
            return (review != null) ? true:false;
        } else {
            throw new RuntimeException(String.format("Restaurant with Id: %d doesn't exist", restaurantId));
        }
    }

    public List<Review> findAllUserReviews(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            return userOptional.get().getReviews();
        } else {
            throw new RuntimeException(String.format("User Id: %d does not exist"));
        }
    }

    /*public List<Review> findAllUserRestaurantReviews(Integer userId) {

    }*/
}
