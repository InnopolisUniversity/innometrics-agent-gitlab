package innometircs.gitlab.agent.repo;

import innometircs.gitlab.agent.domain.Event;
import innometircs.gitlab.agent.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepo extends JpaRepository<Issue,Long> {

    List<Issue> findAllByProjectId(Long projectId);
}
