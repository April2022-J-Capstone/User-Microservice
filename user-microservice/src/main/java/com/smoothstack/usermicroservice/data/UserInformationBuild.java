package com.smoothstack.usermicroservice.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.sql.Date;
import java.time.LocalDate;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserInformationBuild {
    private Integer users_Id;
    private String userName;
    private String password;
    private Boolean enabled;
    private String first_name;
    private String last_name;
    private String email;
    private String phone_number;
    private LocalDate birthdate;
    private Boolean veteran_status;
    private Boolean email_confirmed;
    private Integer communication_type_id;
    private Boolean account_active;
    private String userRole;
}
