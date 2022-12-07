package org.blokaj.repositories;

import org.blokaj.models.entities.User;
import org.blokaj.models.responses.MyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email=?1 or u.username=?1")
    Optional<User> findUserByEmailOrUsername(String usernameOrEmail);

    @Query("select new org.blokaj.models.responses.MyProfile(u.id,u.firstname, u.lastname, u.email, u.username, u.role.name) from User u where u.id=?1")
    MyProfile getMyProfile(Long id);
}
