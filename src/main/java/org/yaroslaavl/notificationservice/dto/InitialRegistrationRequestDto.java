package org.yaroslaavl.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.yaroslaavl.notificationservice.validation.EmailCandidate;
import org.yaroslaavl.notificationservice.validation.EmailRecruiter;
import org.yaroslaavl.notificationservice.validation.groups.CandidateAction;
import org.yaroslaavl.notificationservice.validation.groups.RecruiterAction;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitialRegistrationRequestDto {

    @EmailCandidate(groups = CandidateAction.class)
    @EmailRecruiter(groups = RecruiterAction.class)
    private String email;
}
