package innometircs.gitlab.agent;

import innometircs.gitlab.agent.service.RESTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AgentApplication {
	@Autowired
	static RESTService restService;
	public static void main(String[] args) {
		SpringApplication.run(AgentApplication.class, args);
	//	System.out.println("HOST IP IS" + restService.getHOST_IP());
	}

}
