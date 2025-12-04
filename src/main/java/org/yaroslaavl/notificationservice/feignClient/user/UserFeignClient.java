package org.yaroslaavl.notificationservice.feignClient.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.yaroslaavl.notificationservice.config.FeignConfig;

@FeignClient(name = "user-service", path = "/api/v1", configuration = FeignConfig.class)
public interface UserFeignClient {

    @GetMapping("/user/exists")
    Boolean existsAccount(@RequestParam("email") String email);

    @GetMapping("/user/short-info")
    UserShortDto getUserShortInfo(@RequestParam("userId") String userId);
}
