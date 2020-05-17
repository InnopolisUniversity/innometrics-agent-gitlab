package innometircs.gitlab.agent.repo;

import innometircs.gitlab.agent.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {

     List<Project> findAllByToken(String token);
     Optional<Project> findByName(String name);
     Optional<Project> findByNameAndToken(String name, String token);

}
