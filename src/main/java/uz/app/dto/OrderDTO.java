package uz.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private List<OrderProductDTO> products;
    private String customerPhoneNumber;
    private String customerFullName;
}
