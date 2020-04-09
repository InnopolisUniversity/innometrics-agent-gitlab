package innometircs.gitlab.agent.controler;

import innometircs.gitlab.agent.domain.Commit;
import innometircs.gitlab.agent.domain.Event;
import innometircs.gitlab.agent.domain.Issue;
import innometircs.gitlab.agent.domain.Project;
import innometircs.gitlab.agent.service.RESTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController()
public class RESTController {
    @Autowired
    private RESTService service;


//    @GetMapping("/projects")
//    public List<Project> getProjects(){
//        return service.getProjects();
//    }
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

    @PostMapping("projects")
    public void fetchProject(@RequestParam(name = "auth_token") String authToken,@RequestParam(name = "repo_name", required = false) String repoName) throws Exception {
        service.fetchRepo(authToken, repoName);
    }
    @GetMapping("/projects")
    public List<Project> getProjects(@RequestParam(name = "auth_token", required = true) String authToken, @RequestParam(name = "fetched") String fetched) throws IOException {
        if (fetched.equals("true")){
            return service.getProjectsByToken(authToken);
        }
        else {
            return service.getProjects(authToken);

        }
    }

}
