package innometircs.gitlab.agent.repo;

import innometircs.gitlab.agent.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event,Long> {

    List<Event> findAllByProjectId(Long projectId);
}
