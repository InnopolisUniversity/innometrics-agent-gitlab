package innometircs.gitlab.agent.repo;

import innometircs.gitlab.agent.domain.Commit;
import innometircs.gitlab.agent.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitRepo extends JpaRepository<Commit,String> {

    List<Commit> findAllByProjectId(Long projectId);
}
