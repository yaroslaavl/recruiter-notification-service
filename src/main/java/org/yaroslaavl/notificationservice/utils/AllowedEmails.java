package org.yaroslaavl.notificationservice.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "email")
public class AllowedEmails {

    private Set<String> allowedEmails = new HashSet<>();
}
