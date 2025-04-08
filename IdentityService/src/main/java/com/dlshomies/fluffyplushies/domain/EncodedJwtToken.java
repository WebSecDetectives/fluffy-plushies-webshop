package com.dlshomies.fluffyplushies.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncodedJwtToken {
    private String token;
    private long expiresAt;
}
