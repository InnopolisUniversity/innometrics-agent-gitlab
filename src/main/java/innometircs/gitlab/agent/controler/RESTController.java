package innometircs.gitlab.agent.controler;

import innometircs.gitlab.agent.domain.Commit;
import innometircs.gitlab.agent.domain.Event;
import innometircs.gitlab.agent.domain.Issue;
import innometircs.gitlab.agent.domain.Project;
import innometircs.gitlab.agent.repo.CommitRepo;
import innometircs.gitlab.agent.repo.EventRepo;
import innometircs.gitlab.agent.repo.IssueRepo;
import innometircs.gitlab.agent.repo.ProjectRepo;
import innometircs.gitlab.agent.runner.AgentRunner;
import innometircs.gitlab.agent.service.RESTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController()
public class RESTController {
    @Autowired
    private RESTService service;
    @Autowired
    private AgentRunner agentRunner;


    @GetMapping("/projects")
    public List<Project> getProjects(){
        return service.getProjects();
    }
    @GetMapping("/projects/{projectId}")
    public Project getProjectById(@PathVariable Long projectId){
        Project project = service.getProjectById(projectId);
        return project;
    }

    @GetMapping("/projects/{projectId}/events")
    public List<Event> getProjectEvents(@PathVariable Long projectId){
        return service.getEvents(projectId);
    }

    @GetMapping("/projects/{projectId}/commits")
    public List<Commit> getProjectCommits(@PathVariable Long projectId){
        return service.getCommits(projectId);
    }
    @GetMapping("/projects/{projectId}/issues")
    public List<Issue> getProjectIssues(@PathVariable Long projectId){
        return service.getIssues(projectId);
    }

    @PostMapping("/projects")
    public void fetchProjects(@RequestParam(name = "auth_token", required = true) String authToken){
        agentRunner.fetchByToken(authToken);
    }

}
