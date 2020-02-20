package innometircs.gitlab.agent.domain;

import org.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Issue {

    @Id
    @GeneratedValue
    private Long issueId;

    private Long projectId;

    private String state;

    private String title;

    private String description;

    private Issue(){}
    public Issue(JSONObject jsonObject)
    {
        this.projectId = (long) jsonObject.getInt("project_id");
        this.state = jsonObject.get("state").toString();
        this.title = jsonObject.get("title").toString();
        this.description = jsonObject.get("description").toString();
    }


}
