package innometircs.gitlab.agent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RESTService {

    // CONFIGS
    private String REPO = "https://gitlab.com";

    private String BASE_URL = REPO + "/api/v4/";

    private String HOST_IP = System.getenv("HOST_IP") + "/hook";

    public String getHOST_IP() {
        return HOST_IP;
    }

    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private IssueRepo issueRepo;
    @Autowired
    private CommitRepo commitRepo;


    /**
     * Get all repos from gitlab server by provided private_token(auth_token)
     * @param private_token
     * @return list of projects(repos)
     * @throws IOException
     */
    public List<Project> getProjects(String private_token) throws IOException {
        validateToken(private_token);

        List<Project> projects = new ArrayList<>();

        // fetch all private repos
        JSONArray json = get_JSONArray(BASE_URL + "projects" + attributes("visibility=private", "private_token" + "=" + private_token, "membership=true"));

        for (Object o : json) {
            JSONObject projectJson = (JSONObject) o;

            projects.add(new Project(projectJson, private_token));
        }

        // fetch all public repos

        json = get_JSONArray(BASE_URL + "projects" + attributes("visibility=public", "private_token" + "=" + private_token, "membership=true"));

        for (Object o : json) {
            JSONObject projectJson = (JSONObject) o;

            projects.add(new Project(projectJson, private_token));
        }

        return projects;
    }


    /**
     * Fetch all data from gitlab and then store it locally to db.
     * Also set up webhook
     * @param private_token
     * @param repoName
     * @throws Exception
     */
    public void fetchRepo(String private_token, String repoName) throws Exception {
        validateToken(private_token);

        Boolean flag = false;
        Project project = null;

        // Get all repos, select the one that we want to fetch (repoName).
        // Section for private repos

        JSONArray json = get_JSONArray(BASE_URL + "projects" + attributes("visibility=private", "private_token" + "=" + private_token, "membership=true"));
        for (Object o : json) {
            JSONObject projectJson = (JSONObject) o;

            if (projectJson.getString("name").equals(repoName)) {
                project = new Project(projectJson, private_token);

                projectRepo.saveAndFlush(project);

                JSONArray eventsJson = get_JSONArray(projectJson.getJSONObject("_links").getString("events") + attributes("private_token" + "=" + private_token));
                Project finalProject = project;
                eventsJson.forEach(x -> eventRepo.save(new Event((JSONObject) x, finalProject.getProjectId())));

                JSONArray issuesJson = get_JSONArray(projectJson.getJSONObject("_links").getString("issues") + attributes("private_token" + "=" + private_token));
                Project finalProject1 = project;
                issuesJson.forEach(x -> issueRepo.save(new Issue((JSONObject) x, finalProject1.getProjectId())));

                JSONArray commitsJson = get_JSONArray(BASE_URL + "projects/" + project.getProjectId().toString() + "/repository/commits" + attributes("private_token" + "=" + private_token));
                Project finalProject2 = project;
                commitsJson.forEach(x -> commitRepo.save(new Commit((JSONObject) x, finalProject2.getProjectId())));

                projectRepo.save(project);
                flag = true;
            }
        }

        // Section for public repos
        json = get_JSONArray(BASE_URL + "projects" + attributes("visibility=public", "private_token" + "=" + private_token, "membership=true"));

        for (Object o : json) {
            JSONObject projectJson = (JSONObject) o;

            if (projectJson.getString("name").equals(repoName)) {
                project = new Project(projectJson, private_token);

                projectRepo.saveAndFlush(project);

                JSONArray eventsJson = get_JSONArray(projectJson.getJSONObject("_links").getString("events") + attributes("private_token" + "=" + private_token));
                Project finalProject3 = project;
                eventsJson.forEach(x -> eventRepo.save(new Event((JSONObject) x, finalProject3.getProjectId())));

                JSONArray issuesJson = get_JSONArray(projectJson.getJSONObject("_links").getString("issues") + attributes("private_token" + "=" + private_token));
                Project finalProject4 = project;
                issuesJson.forEach(x -> issueRepo.save(new Issue((JSONObject) x, finalProject4.getProjectId())));

                JSONArray commitsJson = get_JSONArray(BASE_URL + "projects/" + project.getProjectId().toString() + "/repository/commits" + attributes("private_token" + "=" + private_token));
                Project finalProject5 = project;
                commitsJson.forEach(x -> commitRepo.save(new Commit((JSONObject) x, finalProject5.getProjectId())));

                projectRepo.save(project);
                flag = true;
            }
        }
        // if found, then set up hook(send post request to gilab
        if (flag) {
            JSONArray hooks = get_JSONArray(BASE_URL + "projects/"+project.getProjectId()+"/hooks"+attributes("private_token="+private_token));
            if ( hooks.toList().stream().filter(x -> ((HashMap<String,Object>) x).get("url").equals(HOST_IP)).collect(Collectors.toList()).size() == 0){
                sendPost(BASE_URL + "projects/"+project.getProjectId()+"/hooks","private_token="+private_token,"push_events=true","issues_events=true","enable_ssl_verification=false", "url="+HOST_IP);
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid repository name");
        }

    }


    /**
     * Get certain repo from db
     * @param projectId
     * @return
     */
    public Project getProjectById(Long projectId) {
        Project project = projectRepo.findById(projectId).orElseThrow();
        return project;
    }

    /**
     * Get all repos that are stored in db
     * @param token
     * @return list of projects
     * @throws IOException
     */
    public List<Project> getProjectsByToken(String token) throws IOException {
        validateToken(token);
        if (!tokenStored(token)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no repositories fetched by this token");
        }
        return projectRepo.findAllByToken(token);
    }

    /**
     * get all events for that project
     * @param projectId
     * @return list of events
     */
    public List<Event> getEvents(Long projectId) {

        return eventRepo.findAllByProjectId(projectId);
    }
    /**
     * get all commits for that project
     * @param projectId
     * @return list of commits
     */
    public List<Commit> getCommits(Long projectId) {
        return commitRepo.findAllByProjectId(projectId);
    }
    /**
     * get all issues for that project
     * @param projectId
     * @return list of issues
     */
    public List<Issue> getIssues(Long projectId) {
        return issueRepo.findAllByProjectId(projectId);
    }

    /**
     * Validates provided token. Send test request to gitlab server. If request is not accepted (IOException is thrown, then auth token is invalid)
     * @param token
     */
    private void validateToken(String token) {
        try {
            get_JSONArray(BASE_URL + "projects" + attributes("visibility=private", "private_token" + "=" + token, "membership=true"));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid private token");
        }
    }

    /**
     * True if some repost were fetched for that auth toke
     * False otherwise
     * @param token
     * @return
     */
    private Boolean tokenStored(String token) {
        return projectRepo.findAllByToken(token).size() != 0;
    }

    /**
     * True if repo is stored
     * False otherwise
     * @param repoName
     * @param token
     * @return
     */
    private Boolean repoStored(String repoName, String token) {
        return projectRepo.findByNameAndToken(repoName, token).isPresent();
    }

    // UTIL FUNCTIONS
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



    private void sendPost(String url, String... params) throws Exception {
        var values = new HashMap<String, String>();

        var objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writeValueAsString(values);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + attributes(params)))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }
}
