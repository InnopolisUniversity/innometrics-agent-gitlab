package innometircs.gitlab.agent.controler;

import innometircs.gitlab.agent.service.HookService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HookController {
    @Autowired
    HookService hookService;
    @PostMapping("/hook")
    public void getHook(@RequestBody String request){
        JSONObject jsonObject = new JSONObject(request);
        System.out.println(jsonObject);
        hookService.get_hook(jsonObject);
    }
}
