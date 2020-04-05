package innometircs.gitlab.agent;

import innometircs.gitlab.agent.controler.RESTController;
import innometircs.gitlab.agent.domain.Commit;
import innometircs.gitlab.agent.domain.Event;
import innometircs.gitlab.agent.domain.Issue;
import innometircs.gitlab.agent.domain.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import java.io.IOException;
import java.util.List;


@SpringBootTest
class AgentApplicationTests {

	@Autowired
	RESTController restController;
//
//	@MockBean
//	RESTService restService;

	@Test
	void invalidToken() {
		Exception exception = assertThrows(ResponseStatusException.class,
				() -> restController.getProjects("invalid","f"));

		assertEquals(exception.getMessage(), "404 NOT_FOUND \"invalid private token\"");
	}
	@Test
	void validToken() throws IOException {

		List<Project> projects = restController.getProjects("kEUX1NcEmfzzZ2GcX9LW","false");
		assertNotNull(projects);

	}

	@Test
	void invalidRepoName(){
		String repoName = "invalid";
		String token = "kEUX1NcEmfzzZ2GcX9LW";

		Exception exception = assertThrows(ResponseStatusException.class,
				() -> restController.fetchProject(token,repoName));


		assertEquals(exception.getMessage(), "404 NOT_FOUND \"invalid repository name\"");

	}

	@Test
	void validReponameAndThenFetchData() throws IOException {

		String repoName = "gitlab.agent.spring";
		String token = "kEUX1NcEmfzzZ2GcX9LW";

		assertDoesNotThrow(()->restController.fetchProject(token,repoName));

		List<Project> fetchedProjects = restController.getProjects(token,"true");
		assertTrue(fetchedProjects.stream().map(p -> p.getName()).anyMatch(p -> p.equals(repoName)));

		Project fetchedProject = fetchedProjects.stream().filter(p->p.getName().equals(repoName)).findFirst().orElseThrow();

		List<Commit> commits = restController.getProjectCommits(fetchedProject.getProjectId());

		List<Event> events = restController.getProjectEvents(fetchedProject.getProjectId());

		List<Issue> issues = restController.getProjectIssues(fetchedProject.getProjectId());

		assertNotNull(commits);
		assertNotNull(events);
		assertNotNull(issues);
	}






}
