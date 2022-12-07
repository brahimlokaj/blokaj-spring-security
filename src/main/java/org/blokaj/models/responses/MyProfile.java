package org.blokaj.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyProfile {

    private Long id;

    private String firstname;

    private String lastname;

    private String email;

    private String username;

    private String role;
}
