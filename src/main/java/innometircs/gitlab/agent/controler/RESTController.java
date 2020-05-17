package innometircs.gitlab.agent.controler;

import innometircs.gitlab.agent.domain.Commit;
import innometircs.gitlab.agent.domain.Event;
import innometircs.gitlab.agent.domain.Issue;
import innometircs.gitlab.agent.domain.Project;
import innometircs.gitlab.agent.service.RESTService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(
            value = "Get certain project that is fetched",
            notes = "Method returns projects(and data related with that project) that are fetched"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Successful",
                    response = Project.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "Some problem arrived, message will contain information",
                    response = ResponseStatusException.class
            )
    })
    @GetMapping("/projects/{projectId}")
    public Project getProjectById(@PathVariable Long projectId){
        Project project = service.getProjectById(projectId);
        return project;
    }


    @ApiOperation(
            value = "Get events for certain projects",
            notes = "Method returns events for certain project that are fetched"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Successful",
                    response = Event.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "Some problem arrived, message will contain information",
                    response = ResponseStatusException.class
            )
    })
    @GetMapping("/projects/{projectId}/events")
    public List<Event> getProjectEvents(@PathVariable Long projectId){
        return service.getEvents(projectId);
    }


    @ApiOperation(
            value = "Get commits for certain projects",
            notes = "Method returns commits for certain project that are fetched"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Successful",
                    response = Commit.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "Some problem arrived, message will contain information",
                    response = ResponseStatusException.class
            )
    })
    @GetMapping("/projects/{projectId}/commits")
    public List<Commit> getProjectCommits(@PathVariable Long projectId){
        return service.getCommits(projectId);
    }

    @ApiOperation(
            value = "Get issues for certain projects",
            notes = "Method returns issues for certain project that are fetched"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Successful",
                    response = Issue.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "Some problem arrived, message will contain information",
                    response = ResponseStatusException.class
            )
    })
    @GetMapping("/projects/{projectId}/issues")
    public List<Issue> getProjectIssues(@PathVariable Long projectId){
        return service.getIssues(projectId);
    }

    @ApiOperation(
            value = "Set agent to listen for certain repo",
            notes = "In order to agent to listen gitlab server and fetch data from certain repo, to this method auth_token(from gitlab) and repo name has to be send"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = ""
            ),
            @ApiResponse(
                    code = 400,
                    message = "Some problem arrived, message will contain information",
                    response = ResponseStatusException.class
            )
    })
    @PostMapping("projects")
    public void fetchProject(@RequestParam(name = "auth_token") String authToken,@RequestParam(name = "repo_name", required = false) String repoName) throws Exception {
        service.fetchRepo(authToken, repoName);
    }


    @ApiOperation(
            value = "Get available repos for that token(user private and public repositoriess)",
            notes = "Method return repos. Note that reques param \"fetched\" specify either only fetched ones or all availabe repos would be returned.\nIf fetched is TRUE, then return only that repos that are fetched(see method POST /projects)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Successful",
                    response = Project.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "Some problem arrived, message will contain information",
                    response = ResponseStatusException.class
            )
    })
    @GetMapping("/projects")
    public List<Project> getProjects(@RequestParam(name = "auth_token") String authToken, @RequestParam(name = "fetched") Boolean fetched) throws IOException {
        if (fetched){
            return service.getProjectsByToken(authToken);
        }
        else {
            return service.getProjects(authToken);

        }
    }

}
