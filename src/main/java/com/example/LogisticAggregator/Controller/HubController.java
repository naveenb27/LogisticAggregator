package com.example.LogisticAggregator.Controller;


import com.example.LogisticAggregator.Model.Hub;
import com.example.LogisticAggregator.Service.HubService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hub")
public class HubController {

    private final HubService hubService;

    HubController(HubService hubService) {
        this.hubService = hubService;
    }

    @PostMapping("/create-hub")
    public String createHub(@RequestBody Hub hub) {
        return hubService.createHub(hub);
    }

    @GetMapping("/get-all-hub")
    public List<Hub> getAllHubs(){
        return hubService.getAllHub();
    }
}
