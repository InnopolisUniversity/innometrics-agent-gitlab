package innometircs.gitlab.agent.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
public class Event {
    @Id
    @GeneratedValue
    private Long eventId;


    private String actionName;

    private String targetId;
    private String targetType;
    private String authorId;
    private String targetTitle;
    private String createdAt;

//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    @JsonIgnore
//    private Project project;
    private long projectId;

    private Event(){}


    public Event(JSONObject jsonObject, Long projectId) {
        this.actionName = jsonObject.get("action_name").toString();
        this.targetId = jsonObject.get("target_id").toString();
        this.targetType = jsonObject.get("target_type").toString();
        this.authorId = jsonObject.get("author_id").toString();
        this.targetTitle = jsonObject.get("target_title").toString();
        this.createdAt = jsonObject.get("created_at").toString();
        this.projectId = projectId;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getActionName() {
        return actionName;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getProjectId() {
        return projectId;
    }
}
