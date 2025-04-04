package uz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminOperatorDTO {
    private String firstName;
    private String lastName;
    private int birthYear;
    private String phoneNumber;
    private String password;
}
