package uz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import uz.app.entity.enums.Role;

@Data
@AllArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private int birthYear;
    private String phoneNumber;
    private Role role;
}
