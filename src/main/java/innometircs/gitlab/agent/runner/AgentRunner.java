package innometircs.gitlab.agent.runner;

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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class AgentRunner implements ApplicationRunner {
    private String REPO = "https://gitlab.com";
    private String BASE_URL = REPO + "/api/v4/";
    private String private_token = "Re5inpHzEspP_PfycjgD";
    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private IssueRepo issueRepo;
    @Autowired
    private CommitRepo commitRepo;

    @Override
    public void run(ApplicationArguments args)  {



        JSONArray json = get_JSONArray(BASE_URL + "projects" +attributes("visibility=public","private_token"+"="+private_token, "membership=true"));

        for (Object o : json) {
            JSONObject projectJson = (JSONObject) o;

            Project project = new Project(projectJson);
            projectRepo.saveAndFlush(project);


            JSONArray eventsJson = get_JSONArray(projectJson.getJSONObject("_links").getString("events") + attributes("private_token" + "=" + private_token));
            Set<Event> events = new LinkedHashSet<>();
            eventsJson.forEach(x -> events.add(new Event((JSONObject) x, project)));
            project.setEvents(events);
            events.forEach(e -> eventRepo.save(e));


            JSONArray issuesJson = get_JSONArray(projectJson.getJSONObject("_links").getString("issues") + attributes("private_token" + "=" + private_token));
            Set<Issue> issues = new LinkedHashSet<>();
            issuesJson.forEach(x -> issues.add(new Issue((JSONObject) x, project)));
            project.setIssues(issues);
            issues.forEach(issue -> issueRepo.save(issue));


            JSONArray commitsJson = get_JSONArray(BASE_URL + "projects/" + project.getProjectId().toString() + "/repository/commits" + attributes("private_token" + "=" + private_token));
            Set<Commit> commits = new LinkedHashSet<>();
            commitsJson.forEach(x -> commits.add(new Commit((JSONObject) x, project)));
            project.setCommits(commits);
            commits.forEach(commit -> commitRepo.save(commit));




            projectRepo.save(project);


        }
        System.out.println();

    }


    private String attributes(String... attributes){
        StringBuilder output = new StringBuilder();
        output.append("?");
        for (String attribute: attributes){
            output.append(attribute).append("&");
        }
        output.setLength(output.length() - 1);
        return output.toString();

    }
    JSONArray get_JSONArray(String url) {

        try {
            return new JSONArray(
                    new JSONTokener(
                            new URL(url)
                                    .openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONArray();

    }
}
