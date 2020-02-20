package innometircs.gitlab.agent.repo;

import innometircs.gitlab.agent.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {
}
