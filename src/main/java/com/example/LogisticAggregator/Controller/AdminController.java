package com.example.LogisticAggregator.Controller;

import com.example.LogisticAggregator.Model.Admin;
import com.example.LogisticAggregator.Model.LogisticProvider;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import com.example.LogisticAggregator.Model.User;
import com.example.LogisticAggregator.Service.AdminService;
import com.example.LogisticAggregator.Service.LogisticProviderService;
import com.example.LogisticAggregator.Service.ShipmentDetailsService;
import com.example.LogisticAggregator.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "${frontend.url}")
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final LogisticProviderService logisticProviderService;
    private final UserService userService;
    private final ShipmentDetailsService shipmentDetailsService;

    AdminController(AdminService adminService, LogisticProviderService logisticProviderService, UserService userService, ShipmentDetailsService shipmentDetailsService) {
        this.adminService = adminService;
        this.shipmentDetailsService = shipmentDetailsService;
        this.logisticProviderService = logisticProviderService;
        this.userService = userService;
    }

    @PostMapping
    public String createAdmin(@RequestBody Admin admin) {
        return adminService.createAdmin(admin);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Admin admin) {
        return adminService.verify(admin);
    }

    @GetMapping("/logistic-provider")
    public List<LogisticProvider> LogisticProvider() {
        return logisticProviderService.getLogisticProviders();
    }

    @PostMapping("/logistic-provider/approve/{id}")
    public ResponseEntity<String> approveLogisticProvider(@PathVariable long id) {
        LogisticProvider logisticProvider = logisticProviderService.findById(id);

        if(!Objects.isNull(logisticProvider)){
            logisticProvider.setApproved(true);
            logisticProviderService.update(logisticProvider);
            return ResponseEntity.ok("Logistic provider approved successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid id!");
    }

    @GetMapping("/user-details")
    public List<User> getAllUsers() {
        return userService.getAllUser();
    }

    @GetMapping("/get-all-shipments")
    public List<ShipmentDetails> getAllShipments() {
        return shipmentDetailsService.findAll();
    }
}
