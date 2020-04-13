package innometircs.gitlab.agent.service;

import innometircs.gitlab.agent.domain.Commit;
import innometircs.gitlab.agent.domain.Hook;
import innometircs.gitlab.agent.repo.CommitRepo;
import innometircs.gitlab.agent.repo.HookRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HookService {

    @Autowired
    HookRepository hookRepository;

    @Autowired
    CommitRepo commitRepo;

    public void get_hook(JSONObject hookJson) {
        String type = hookJson.getString("object_kind");

        switch (type) {
            case "push":
                for (Object o : hookJson.getJSONArray("commits")) {
                    JSONObject commitJson = (JSONObject) o;
                    Commit commit = new Commit(
                            commitJson.getString("id"),
                            hookJson.getLong("project_id"),
                            commitJson.getJSONObject("author").getString("name"),
                            commitJson.getString("timestamp"),
                            commitJson.getString("timestamp"),
                            commitJson.getString("title"),
                            commitJson.getString("message"));

                    System.out.println(commit);
                    commitRepo.save(commit);
                }
                hookRepository.save(new Hook(hookJson));

            case "issue":
                break;
        }
    }
}
