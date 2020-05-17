package innometircs.gitlab.agent.domain;

import org.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.Id;
@Entity
public class Commit {
    @Id
    private String commitId;

    private Long projectId;

    private String authorName;
    private String committedDate;
    private String createdAt;
    private String title;
    private String message;

    public Commit(){}

    public Commit(JSONObject jsonObject, Long projectId) {
        this.commitId = jsonObject.get("id").toString();
        this.authorName = jsonObject.get("author_name").toString();
        this.committedDate = jsonObject.get("committed_date").toString();
        this.createdAt = jsonObject.get("created_at").toString();
        this.title = jsonObject.get("title").toString();
        this.message = jsonObject.get("message").toString();
        this.projectId = projectId;
    }

    public Commit(String commitId, Long projectId, String authorName, String committedDate, String createdAt, String title, String message) {
        this.commitId = commitId;
        this.projectId = projectId;
        this.authorName = authorName;
        this.committedDate = committedDate;
        this.createdAt = createdAt;
        this.title = title;
        this.message = message;
    }

    public String getCommitId() {
        return commitId;
    }

    public Long getProject() {
        return projectId;
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
