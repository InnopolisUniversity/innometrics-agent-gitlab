package innometircs.gitlab.agent.domain;

import org.json.JSONObject;

import javax.persistence.*;

@Entity
public class Event {
    @Id
    @GeneratedValue
    private Long eventId;


    private String actionName;


    @JoinColumn(name = "project", referencedColumnName = "projectId")
    private Long projectId;

    private Event(){}

    public Event(JSONObject jsonObject) {
        this.actionName = jsonObject.getString("action_name");
    }

    public Long getEventId() {
        return eventId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
