package org.blokaj.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    @NotBlank()
    private String usernameOrEmail;

    @NotBlank()
    private String password;
}
