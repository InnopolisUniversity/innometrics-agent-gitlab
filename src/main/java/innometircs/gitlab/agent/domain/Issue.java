package innometircs.gitlab.agent.domain;

import org.json.JSONObject;

import javax.persistence.*;

@Entity
public class Issue {

    @Id
    private Long issueId;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private String description;
    private String state;
    private String title;
    private String updatedAt;
    private String createdAt;
    private String closedAt;

    private Issue(){}

    public Issue(JSONObject jsonObject,Project project) {
        this.description = jsonObject.get("description").toString();
        this.state = jsonObject.get("state").toString();
        this.title = jsonObject.get("title").toString();
        this.updatedAt = jsonObject.get("updated_at").toString();
        this.createdAt = jsonObject.get("created_at").toString();
        this.closedAt = jsonObject.get("closed_at").toString();

        this.issueId = jsonObject.getLong("id");
        this.project = project;
    }

    public Long getIssueId() {
        return issueId;
    }

    public Project getProject() {
        return project;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getClosedAt() {
        return closedAt;
    }

}
