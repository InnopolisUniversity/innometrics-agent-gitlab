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
    private String REPO = "https://gitlab.com";
    private String BASE_URL = REPO + "/api/v4/";

    private String HOST_IP = System.getenv("HOST_IP");

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


    public List<Project> getProjects(String private_token) throws IOException {
        validateToken(private_token);

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

    public void fetchRepo(String private_token, String repoName) throws Exception {
        validateToken(private_token);

        Boolean flag = false;
        Project project = null;
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

        if (flag) {
            JSONArray hooks = get_JSONArray(BASE_URL + "projects/"+project.getProjectId()+"/hooks"+attributes("private_token="+private_token));
            if ( hooks.toList().stream().filter(x -> ((HashMap<String,Object>) x).get("url").equals(HOST_IP)).collect(Collectors.toList()).size() == 0){
                sendPost(BASE_URL + "projects/"+project.getProjectId()+"/hooks","private_token="+private_token,"push_events=true","issues_events=true","enable_ssl_verification=false", "url="+HOST_IP);
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid repository name");
        }

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


    private static HttpURLConnection con;
    private void sendGet() throws Exception {

//        HttpRequest request = HttpRequest.newBuilder()
//                .GET()
//                .uri(URI.create("https://httpbin.org/get"))
//                .setHeader("User-Agent", "Java 11 HttpClient Bot")
//                .build();
//
//        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//        // print status code
//        System.out.println(response.statusCode());
//
//        // print response body
//        System.out.println(response.body());

    }

    private void sendPost(String url, String... params) throws Exception {
        var values = new HashMap<String, String>() {{
//            for (String param : params) {
//                put(param.split(":")[0], param.split(":")[1]);
//            }
//            put("name", "John Doe");
//            put ("occupation", "gardener");
        }};

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
//        var url = "https://httpbin.org/post";
//        var urlParameters = attributes(params);
//        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
//
//        try {
//
//            var myurl = new URL(url);
//            con = (HttpURLConnection) myurl.openConnection();
//
//            con.setDoOutput(true);
//            con.setRequestMethod("POST");
//            con.setRequestProperty("User-Agent", "Java client");
//            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//            try (var wr = new DataOutputStream(con.getOutputStream())) {
//
//                wr.write(postData);
//            }
//
//            StringBuilder content;
//
//            try (var br = new BufferedReader(
//                    new InputStreamReader(con.getInputStream()))) {
//
//                String line;
//                content = new StringBuilder();
//
//                while ((line = br.readLine()) != null) {
//                    content.append(line);
//                    content.append(System.lineSeparator());
//                }
//            }
//
//            System.out.println(content.toString());
//
//        } finally {
//
//            con.disconnect();
//        }

    }
}
