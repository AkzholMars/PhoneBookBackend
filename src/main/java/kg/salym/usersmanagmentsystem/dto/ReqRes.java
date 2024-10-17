package kg.salym.usersmanagmentsystem.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import kg.salym.usersmanagmentsystem.entity.OurUsers;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int statusCode;
    private String error;
    private String message;
    private List<String> messages;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String department;
    private String position;
    private String phone;
    private String role;
    private String email;
    private String password;
    private String confirmPassword;
    private OurUsers ourUsers;
    private List<OurUsers> ourUsersList;


}
