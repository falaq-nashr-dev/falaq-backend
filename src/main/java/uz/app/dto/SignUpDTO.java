package uz.app.dto;

import lombok.Data;

@Data
public class SignUpDTO {
    private String firstName;
    private String lastName;
    private int birthYear;
    private String phoneNumber;
    private String password;
}
