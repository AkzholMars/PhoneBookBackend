package kg.salym.usersmanagmentsystem.service;

import kg.salym.usersmanagmentsystem.dto.ReqRes;
import kg.salym.usersmanagmentsystem.entity.OurUsers;
import kg.salym.usersmanagmentsystem.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;



    public ReqRes register(ReqRes registerReq) {
        ReqRes resp = new ReqRes();

        try {
            Optional userOptional = usersRepository.findByEmail(registerReq.getEmail());
            if (!userOptional.isPresent()) {
                OurUsers ourUser = new OurUsers();
                ourUser.setEmail(registerReq.getEmail());
                ourUser.setName(registerReq.getName());
                ourUser.setDepartment(registerReq.getDepartment());
                ourUser.setPhone(registerReq.getPhone());
                ourUser.setPosition(registerReq.getPosition());
                ourUser.setRole(registerReq.getRole());
                ourUser.setPassword(passwordEncoder.encode(registerReq.getPassword()));
                OurUsers ourUsersResult = usersRepository.save(ourUser);

                if (ourUsersResult.getId() > 0) {
                    resp.setOurUsers((ourUsersResult));
                    resp.setMessage("User Saved Successfully");
                    resp.setStatusCode(200);
                }
            } else {
                resp.setStatusCode(500);
                resp.setMessage("This email is already registered");
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            return resp;
        }
        return resp;
    }

    public ReqRes login(ReqRes loginReq) {
        ReqRes resp = new ReqRes();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginReq.getEmail(),
                    loginReq.getPassword()));
            var user = usersRepository.findByEmail(loginReq.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            resp.setStatusCode(200);
            resp.setToken(jwt);
            resp.setRole(user.getRole()
            );
            resp.setRefreshToken(refreshToken);
            resp.setExpirationTime("24Hrs");
            resp.setMessage("Successfully Logged In");

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Login " + e.getMessage());
            return resp;
        }
        return resp;
    }


    public ReqRes refreshToken(ReqRes refreshTokenReq) {
        ReqRes resp = new ReqRes();
        try {
            String ourEmail = jwtUtils.extractUsername(refreshTokenReq.getToken());
            OurUsers user = usersRepository.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReq.getToken(), user)) {
                var jwt = jwtUtils.generateToken(user);
                resp.setStatusCode(200);
                resp.setToken(jwt);
                resp.setRefreshToken(refreshTokenReq.getToken());
                resp.setExpirationTime("24Hr");
                resp.setMessage("Successfully Refreshed Token");
            }
            resp.setStatusCode(200);
            return resp;

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            return resp;
        }

    }

    public ReqRes getAllUsers() {
        ReqRes resp = new ReqRes();
        try {
            List<OurUsers> result = usersRepository.findAll();
            if (!result.isEmpty()) {
                resp.setOurUsersList(result);
                resp.setStatusCode(200);
                resp.setMessage("Successfully");
            } else {
                resp.setStatusCode(404);
                resp.setMessage("No users found");
            }

            return resp;

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error occurred: " + e.getMessage());
            return resp;
        }

    }

    public ReqRes getAllOurUsers() {
        ReqRes resp = new ReqRes();
        try {
            List<OurUsers> result = usersRepository.findAllByRole("USER");
            if (!result.isEmpty()) {
                resp.setOurUsersList(result);
                resp.setStatusCode(200);
                resp.setMessage("Successfully");
            } else {
                resp.setStatusCode(404);
                resp.setMessage("No users found");
            }

            return resp;

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error occurred: " + e.getMessage());
            return resp;
        }

    }



    public ReqRes getUserById (Integer id) {
        ReqRes resp = new ReqRes();
        try {
            OurUsers userById = usersRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
            resp.setOurUsers(userById);
            resp.setStatusCode(200);
            resp.setMessage("User with id " + id + " found successfully");
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error occurred: " + e.getMessage());
            return resp;
        }
        return resp;
    }

    public ReqRes deleteUser(Integer id) {
        ReqRes resp = new ReqRes();
        try {
            Optional<OurUsers> deletedUser = usersRepository.findById(id);
            if (deletedUser.isPresent()){
                usersRepository.deleteById(id);
                resp.setStatusCode(200);
                resp.setMessage("User delete successfully");
            } else {
                resp.setStatusCode(404);
                resp.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error occurred: " + e.getMessage());
            return resp;
        }
        return resp;
    }

    public ReqRes updateUser (Integer userId, OurUsers updatedUser){
        ReqRes resp = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepository.findById(userId);
            if (userOptional.isPresent()) {
                OurUsers existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setPosition(updatedUser.getPosition());
                existingUser.setDepartment(updatedUser.getDepartment());
                existingUser.setPhone(updatedUser.getPhone());
                existingUser.setRole(updatedUser.getRole());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()){
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OurUsers savedUser = usersRepository.save(existingUser);
                resp.setOurUsers(savedUser);
                resp.setStatusCode(200);
                resp.setMessage("User updated successfully");
            } else {
                resp.setStatusCode(404);
                resp.setMessage("User not found for update");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error occurred: " + e.getMessage());
            return resp;
        }
        return resp;
    }

    public ReqRes getMyInfo (String email) {
        ReqRes resp = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                resp.setOurUsers(userOptional.get());
                resp.setStatusCode(200);
                resp.setMessage("successfully");
            } else {
                resp.setStatusCode(404);
                resp.setMessage("User not found");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error occurred: " + e.getMessage());
            return resp;
        }
        return resp;
        }

    public ReqRes resetPassword(Integer userId, ReqRes reqRes) {
        ReqRes resp = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepository.findById(userId);
            if (userOptional.isPresent()) {
                OurUsers existingUser = userOptional.get();
                existingUser.setPassword(passwordEncoder.encode(reqRes.getPassword()));
                OurUsers savedUser = usersRepository.save(existingUser);
                resp.setOurUsers(savedUser);
                resp.setStatusCode(200);
                resp.setMessage("User updated successfully");
            } else {
                resp.setStatusCode(404);
                resp.setMessage("User not found for update");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error occurred: " + e.getMessage());
            return resp;
        }
        return resp;
    }

    public ReqRes addUsers(List<ReqRes> reqRes) {
        ReqRes resp = new ReqRes();
        List<String> messages = new ArrayList<>();
        try {
            for(ReqRes res: reqRes) {
                Optional userOptional = usersRepository.findByEmail(res.getEmail());
                if (!userOptional.isPresent()) {
                    OurUsers ourUser = new OurUsers();
                    ourUser.setEmail(res.getEmail());
                    ourUser.setName(res.getName());
                    ourUser.setDepartment(res.getDepartment());
                    ourUser.setPhone(res.getPhone());
                    ourUser.setPosition(res.getPosition());
                    ourUser.setRole(res.getRole());
                    ourUser.setPassword(passwordEncoder.encode(res.getPassword()));
                    OurUsers ourUsersResult = usersRepository.save(ourUser);

                    if (ourUsersResult.getId() > 0) {
                        resp.setOurUsers((ourUsersResult));
                        resp.setMessage("User Saved Successfully");
                        resp.setStatusCode(200);
                    }

                } else {
                    resp.setStatusCode(500);
                    messages.add("This email is already registered");
                }
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            return resp;
        }
        return resp;

    }
}
