package com.dlshomies.fluffyplushies.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import java.net.URI;
import java.util.Collection;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class User extends BaseEntity implements UserDetails {

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String phone;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;

    @NotBlank
    private String encodedPassword;

    @NotNull
    private Role role = Role.USER;

    private URI imgUrl;

    // Required by Spring UserDetails which is used for authentication and authorization
    // AuthorityList is a derived in-memory view that changes based on role
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(this.role.name());
    }

    @Override
    public String getPassword() {
        return this.encodedPassword;
    }
}
