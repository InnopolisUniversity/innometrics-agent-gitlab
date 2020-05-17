package innometircs.gitlab.agent.repo;

import innometircs.gitlab.agent.domain.Hook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HookRepository extends JpaRepository<Hook, Long> {
}
