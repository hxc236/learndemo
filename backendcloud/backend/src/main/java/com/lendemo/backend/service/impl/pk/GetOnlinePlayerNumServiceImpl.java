package com.lendemo.backend.service.impl.pk;

import com.lendemo.backend.consumer.WebSocketServer;
import com.lendemo.backend.service.pk.GetOnlinePlayerNumService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GetOnlinePlayerNumServiceImpl implements GetOnlinePlayerNumService {
    @Override
    public Map<String, String> getOnlinePlayerNum() {
        Map<String, String> resp = new HashMap<>();

        int num = WebSocketServer.users.size();
        resp.put("online_count", Integer.toString(num));
        resp.put("error_message", "success");
        return resp;
    }
}
