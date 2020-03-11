package innometircs.gitlab.agent.controler;

import innometircs.gitlab.agent.domain.Commit;
import innometircs.gitlab.agent.domain.Event;
import innometircs.gitlab.agent.domain.Issue;
import innometircs.gitlab.agent.domain.Project;
import innometircs.gitlab.agent.repo.CommitRepo;
import innometircs.gitlab.agent.repo.EventRepo;
import innometircs.gitlab.agent.repo.IssueRepo;
import innometircs.gitlab.agent.repo.ProjectRepo;
import innometircs.gitlab.agent.service.RESTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class RESTController {
    @Autowired
    private RESTService service;


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
    public Set<Event> getProjectEvents(@PathVariable Long projectId){
        Set<Event> events = service.getEvents(projectId);
        return events;
    }

    @GetMapping("/projects/{projectId}/commits")
    public Set<Commit> getProjectCommits(@PathVariable Long projectId){
        Set<Commit> commits = service.getCommits(projectId);
        return commits;
    }
    @GetMapping("/projects/{projectId}/issues")
    public Set<Issue> getProjectIssues(@PathVariable Long projectId){
        Set<Issue> issues = service.getIssues(projectId);
        return issues;
    }

}
