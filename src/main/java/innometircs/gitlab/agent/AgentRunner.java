package innometircs.gitlab.agent;

import innometircs.gitlab.agent.domain.Event;
import innometircs.gitlab.agent.domain.Issue;
import innometircs.gitlab.agent.domain.Project;
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
import java.util.HashMap;
import java.util.Iterator;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {


        String querry = "projects";

        JSONArray json = get_JSONArray(BASE_URL + querry + new Attributes<String, String>() {{
            put("visibility", "private");
            put("private_token", private_token);
        }}.toString());

        for (Iterator<Object> it = json.iterator(); it.hasNext(); ) {
            JSONObject projectJson = (JSONObject) it.next();

            JSONArray eventsJson = get_JSONArray(projectJson.getJSONObject("_links").getString("events") + new Attributes<String, String>() {{
                put("private_token", private_token);
            }}.toString());
            Set<Event> events = new LinkedHashSet<>();

            for (Iterator<Object> inner_it = eventsJson.iterator(); inner_it.hasNext(); ) {
                Event event = new Event((JSONObject)inner_it.next());
                eventRepo.save(event);
                events.add(event);
            }

            JSONArray issuesJson = get_JSONArray(projectJson.getJSONObject("_links").getString("issues") + new Attributes<String, String>() {{
                put("private_token", private_token);
            }}.toString());
            Set<Issue> issues = new LinkedHashSet<>();

            for (Iterator<Object> inner_it = issuesJson.iterator(); inner_it.hasNext(); ) {
                Issue issue = new Issue((JSONObject)inner_it.next());
                issueRepo.save(issue);
                issues.add(issue);
            }




            Project project = new Project(projectJson);
            if (events.size()!=0)
            {
                project.setEvents(events);
            }
            if (issues.size()!=0)
            {
                project.setIssues(issues);
            }



            projectRepo.save(project);

        }
        System.out.println();

    }


    private class Attributes<T, E> extends HashMap<T, E> {
        @Override
        public String toString() {
            StringBuilder output = new StringBuilder();
            output.append("?");
            for (T elem : super.keySet()) {
                output.append(elem.toString()).append("=").append(super.get(elem).toString()).append("&");
            }
            output.setLength(output.length() - 1);
            return output.toString();
        }
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
