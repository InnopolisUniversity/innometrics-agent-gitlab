package innometircs.gitlab.agent.service;

import innometircs.gitlab.agent.domain.Commit;
import innometircs.gitlab.agent.domain.Event;
import innometircs.gitlab.agent.domain.Issue;
import innometircs.gitlab.agent.domain.Project;
import innometircs.gitlab.agent.repo.CommitRepo;
import innometircs.gitlab.agent.repo.EventRepo;
import innometircs.gitlab.agent.repo.IssueRepo;
import innometircs.gitlab.agent.repo.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RESTService {

    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private IssueRepo issueRepo;
    @Autowired
    private CommitRepo commitRepo;

    public List<Project> getProjects(){
        return projectRepo.findAll();
    }

    public Project getProjectById(Long projectId){
        Project project = projectRepo.findById(projectId).orElseThrow();
        return project;
    }

    public Set<Event> getEvents(Long projectId){
        Project project = getProjectById(projectId);
        return project.getEvents();
    }
    public Set<Commit> getCommits(Long projectId){
        Project project = getProjectById(projectId);
        return project.getCommits();
    }
    public Set<Issue> getIssues(Long projectId){
        Project project = getProjectById(projectId);
        return project.getIssues();
    }
}
