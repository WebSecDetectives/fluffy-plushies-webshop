package com.dlshomies.fluffyplushies.dto.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInformationResponse {
    private String customerName;
    private String email;
    private Integer phone;
    private String street;
    private Integer postalCode;
    private String city;
    private String country;
}
