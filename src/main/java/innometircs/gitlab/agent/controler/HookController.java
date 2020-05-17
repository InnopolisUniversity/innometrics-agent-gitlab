package innometircs.gitlab.agent.controler;

import innometircs.gitlab.agent.service.HookService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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


    @ApiOperation(
            value = "Handle hooks from gitlab server",
            notes = "Method listens for gitlab hook, and then hanldes hooks.\nNormally, this method is not to use by users"
    )
    @PostMapping("/hook")
    public void getHook(@RequestBody String request){
        JSONObject jsonObject = new JSONObject(request);
        System.out.println(jsonObject);
        hookService.get_hook(jsonObject);
    }
}
