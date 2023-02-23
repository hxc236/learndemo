package com.lendemo.backend.controller.pk;

import com.lendemo.backend.service.pk.GetOnlinePlayerNumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetOnlinePlayerNumController {

    @Autowired
    private GetOnlinePlayerNumService getOnlinePlayerNumService;

    @GetMapping("/pk/getonlineplayernum/")
    Map<String, String> getOnlinePlayerNum() {
        return getOnlinePlayerNumService.getOnlinePlayerNum();
    }
}
