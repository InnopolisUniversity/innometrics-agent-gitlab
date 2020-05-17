package innometircs.gitlab.agent.domain;

import org.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Hook {
    @Id
    @GeneratedValue
    private Long hookId;

    private String type;

    private String data;

    private Hook(){};

    public Hook(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public Hook(JSONObject jsonObject){
        this.type = jsonObject.getString("object_kind");
        this.data = jsonObject.toString();
    }

    public Long getHookId() {
        return hookId;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

}
