package kg.salym.usersmanagmentsystem.repository;

import kg.salym.usersmanagmentsystem.entity.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<OurUsers, Integer> {

    Optional<OurUsers> findByEmail(String email);

    List<OurUsers> findAllByRole(String role);
}
