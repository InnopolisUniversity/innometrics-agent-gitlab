package innometircs.gitlab.agent.repo;

import innometircs.gitlab.agent.domain.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitRepo extends JpaRepository<Commit,String> {
}
