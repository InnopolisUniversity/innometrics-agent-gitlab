package innometircs.gitlab.agent;

import innometircs.gitlab.agent.controler.RESTController;
import innometircs.gitlab.agent.service.RESTService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

//@RunWith(SpringRunner.class)
@SpringBootTest
//@WebMvcTest(RESTController.class)
class AgentApplicationTests {

	@Autowired
	RESTController restController;

	@Test
	void test() throws IOException {
		String validToken = "valid";
//		when(restService.getProjectsByToken(validToken)).thenReturn(new ArrayList<>());
//		when(restService.getProjectsByToken(any())).thenThrow(ResponseStatusException.class);
		Exception exception = assertThrows(ResponseStatusException.class,
				() -> restController.getProjects("kEUX1NcEmfzzZ2GcX9LW","f"));
		System.out.println(exception);



	}


}
