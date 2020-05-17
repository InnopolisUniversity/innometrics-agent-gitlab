package innometircs.gitlab.agent.domain;


import org.json.JSONObject;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;

@Entity
public class Project {
    @Id
    private Long projectId;

    @NotNull
    private String name;

    @NotNull
    private String path;

    @NotNull
    private String token;



    private Project(){}

    public Project(JSONObject jsonObject, String token) {
        this.projectId = (long) jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
        this.path = jsonObject.getString("path_with_namespace");
        this.token = token;
    }


    public Long getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getToken() {
        return token;
    }
    @Override
    public String toString() {
        return name + " " + path + " " + projectId;
    }


    //    public Set<Event> getEvents() {
//        return events;
//    }
//
//    public void setEvents(Set<Event> events) {
//        this.events = events;
//    }
//
//    public Set<Issue> getIssues() {
//        return issues;
//    }
//
//    public void setIssues(Set<Issue> issues) {
//        this.issues = issues;
//    }
//
//    public Set<Commit> getCommits() {
//        return commits;
//    }
//
//    public void setCommits(Set<Commit> commits) {
//        this.commits = commits;
//    }
}
