package innometircs.gitlab.agent.domain;

import org.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Commit {
    @Id
    private String commitId;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;


    private String authorName;
    private String committedDate;
    private String createdAt;
    private String title;
    private String message;

    private Commit(){}

    public Commit(JSONObject jsonObject, Project project) {
        this.commitId = jsonObject.get("id").toString();
        this.authorName = jsonObject.get("author_name").toString();
        this.committedDate = jsonObject.get("committed_date").toString();
        this.createdAt = jsonObject.get("created_at").toString();
        this.title = jsonObject.get("title").toString();
        this.message = jsonObject.get("message").toString();
        this.project = project;
    }

    public String getCommitId() {
        return commitId;
    }

    public Project getProject() {
        return project;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getCommittedDate() {
        return committedDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
