package org.yaroslaavl.notificationservice.feignClient.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", path = "/api/v1")
public interface UserAccountExists {

    @GetMapping("/user/exists")
    Boolean existsAccount(@RequestParam("email") String email);
}
