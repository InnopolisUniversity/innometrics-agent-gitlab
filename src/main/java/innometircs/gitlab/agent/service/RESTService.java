package innometircs.gitlab.agent.service;

import innometircs.gitlab.agent.domain.Commit;
import innometircs.gitlab.agent.domain.Event;
import innometircs.gitlab.agent.domain.Issue;
import innometircs.gitlab.agent.domain.Project;
import innometircs.gitlab.agent.repo.CommitRepo;
import innometircs.gitlab.agent.repo.EventRepo;
import innometircs.gitlab.agent.repo.IssueRepo;
import innometircs.gitlab.agent.repo.ProjectRepo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class RESTService {
    private String REPO = "https://gitlab.com";
    private String BASE_URL = REPO + "/api/v4/";

    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private IssueRepo issueRepo;
    @Autowired
    private CommitRepo commitRepo;


    public List<Project> getProjects(String private_token) throws IOException {


        List<Project> projects = new ArrayList<>();

        JSONArray json = get_JSONArray(BASE_URL + "projects" + attributes("visibility=private", "private_token" + "=" + private_token, "membership=true"));

        for (Object o : json) {
            JSONObject projectJson = (JSONObject) o;

            projects.add(new Project(projectJson, private_token));
        }
        json = get_JSONArray(BASE_URL + "projects" + attributes("visibility=public", "private_token" + "=" + private_token, "membership=true"));

        for (Object o : json) {
            JSONObject projectJson = (JSONObject) o;

            projects.add(new Project(projectJson, private_token));
        }

        return projects;
    }
    public void fetchRepo( String private_token, String repoName) throws IOException {
        validateToken(private_token);


        JSONArray json = get_JSONArray(BASE_URL + "projects" + attributes("visibility=private", "private_token" + "=" + private_token, "membership=true"));

        for (Object o : json) {
            JSONObject projectJson = (JSONObject) o;

            if (projectJson.getString("name").equals(repoName)){
                Project project = new Project(projectJson, private_token);

                projectRepo.saveAndFlush(project);

                JSONArray eventsJson = get_JSONArray(projectJson.getJSONObject("_links").getString("events") + attributes("private_token" + "=" + private_token));
                eventsJson.forEach(x -> eventRepo.save(new Event((JSONObject) x, project.getProjectId())));

                JSONArray issuesJson = get_JSONArray(projectJson.getJSONObject("_links").getString("issues") + attributes("private_token" + "=" + private_token));
                issuesJson.forEach(x -> issueRepo.save(new Issue((JSONObject) x, project.getProjectId())));

                JSONArray commitsJson = get_JSONArray(BASE_URL + "projects/" + project.getProjectId().toString() + "/repository/commits" + attributes("private_token" + "=" + private_token));
                commitsJson.forEach(x -> commitRepo.save(new Commit((JSONObject) x, project.getProjectId())));

                projectRepo.save(project);
                return;
            }
        }

        json = get_JSONArray(BASE_URL + "projects" + attributes("visibility=public", "private_token" + "=" + private_token, "membership=true"));

        for (Object o : json) {
            JSONObject projectJson = (JSONObject) o;

            if (projectJson.getString("name").equals(repoName)){
                Project project = new Project(projectJson, private_token);

                projectRepo.saveAndFlush(project);

                JSONArray eventsJson = get_JSONArray(projectJson.getJSONObject("_links").getString("events") + attributes("private_token" + "=" + private_token));
                eventsJson.forEach(x -> eventRepo.save(new Event((JSONObject) x, project.getProjectId())));

                JSONArray issuesJson = get_JSONArray(projectJson.getJSONObject("_links").getString("issues") + attributes("private_token" + "=" + private_token));
                issuesJson.forEach(x -> issueRepo.save(new Issue((JSONObject) x, project.getProjectId())));

                JSONArray commitsJson = get_JSONArray(BASE_URL + "projects/" + project.getProjectId().toString() + "/repository/commits" + attributes("private_token" + "=" + private_token));
                commitsJson.forEach(x -> commitRepo.save(new Commit((JSONObject) x, project.getProjectId())));

                projectRepo.save(project);
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid repository name");

    }
    public Project getProjectById(Long projectId) {
        Project project = projectRepo.findById(projectId).orElseThrow();
        return project;
    }

    public List<Project> getProjectsByToken(String token) throws IOException {
        validateToken(token);
        if (!tokenStored(token)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no repositories fetched by this token");
        }
        return projectRepo.findAllByToken(token);
    }

    public List<Event> getEvents(Long projectId) {

        return eventRepo.findAllByProjectId(projectId);
    }

    public List<Commit> getCommits(Long projectId) {
        return commitRepo.findAllByProjectId(projectId);
    }

    public List<Issue> getIssues(Long projectId) {
        return issueRepo.findAllByProjectId(projectId);
    }


    private void validateToken(String token) {
        try {
            get_JSONArray(BASE_URL + "projects" + attributes("visibility=private", "private_token" + "=" + token, "membership=true"));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid private token");
        }
    }



    private Boolean tokenStored(String token) {
        return projectRepo.findAllByToken(token).size() != 0;
    }

    private Boolean repoStored(String repoName, String token) {
        return projectRepo.findByNameAndToken(repoName, token).isPresent();
    }
//    public void fetchByToken(String private_token, String) throws IOException {
//        fetch(BASE_URL + "projects" +attributes("visibility=private","private_token"+"="+private_token, "membership=true"), private_token);
//        fetch(BASE_URL + "projects" +attributes("visibility=public","private_token"+"="+private_token, "membership=true"), private_token);
//    }


    //    private void fetch(String url, String private_token) throws IOException {
//
//
//        JSONArray json = get_JSONArray(url);
//
//        for (Object o : json) {
//            JSONObject projectJson = (JSONObject) o;
//
//            Project project = new Project(projectJson, private_token);
//            projectRepo.saveAndFlush(project);
//
//            JSONArray eventsJson = get_JSONArray(projectJson.getJSONObject("_links").getString("events") + attributes("private_token" + "=" + private_token));
//            eventsJson.forEach(x -> eventRepo.save(new Event((JSONObject) x, project.getProjectId())));
//
//            JSONArray issuesJson = get_JSONArray(projectJson.getJSONObject("_links").getString("issues") + attributes("private_token" + "=" + private_token));
//            issuesJson.forEach(x -> issueRepo.save(new Issue((JSONObject) x, project.getProjectId())));
//
//            JSONArray commitsJson = get_JSONArray(BASE_URL + "projects/" + project.getProjectId().toString() + "/repository/commits" + attributes("private_token" + "=" + private_token));
//            commitsJson.forEach(x -> commitRepo.save(new Commit((JSONObject) x, project.getProjectId())));
//
//            projectRepo.save(project);
//        }
//
//    }


    private String attributes(String... attributes) {
        StringBuilder output = new StringBuilder();
        output.append("?");
        for (String attribute : attributes) {
            output.append(attribute).append("&");
        }
        output.setLength(output.length() - 1);
        return output.toString();

    }

    JSONArray get_JSONArray(String url) throws IOException {

        return new JSONArray(
                new JSONTokener(
                        new URL(url)
                                .openStream()));
    }
}
