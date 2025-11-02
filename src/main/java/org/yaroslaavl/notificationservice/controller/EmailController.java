package org.yaroslaavl.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yaroslaavl.notificationservice.dto.InitialRegistrationRequestDto;
import org.yaroslaavl.notificationservice.dto.NotificationShortDto;
import org.yaroslaavl.notificationservice.dto.PageShortDto;
import org.yaroslaavl.notificationservice.service.EmailService;
import org.yaroslaavl.notificationservice.service.NotificationService;
import org.yaroslaavl.notificationservice.validation.groups.CandidateAction;
import org.yaroslaavl.notificationservice.validation.groups.RecruiterAction;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
public class EmailController {

    private final EmailService emailService;
    private final NotificationService notificationService;

    @PostMapping("/request-verification-candidate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void sendRequestVerificationCandidate(@RequestBody @Validated(CandidateAction.class)
                                                 InitialRegistrationRequestDto initialRegistrationRequestDto) {
        emailService.requestVerification(initialRegistrationRequestDto);
    }

    @PostMapping("/request-verification-recruiter")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void sendRequestVerificationRecruiter(@RequestBody @Validated(RecruiterAction.class)
                                                     InitialRegistrationRequestDto initialRegistrationRequestDto) {
        emailService.requestVerification(initialRegistrationRequestDto);
    }

    @PostMapping("/verify-code")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void verifyEmailCode(@RequestParam("verificationCode") String verificationCode,
                                @RequestParam("email") String email) {
        emailService.verifyCode(verificationCode, email);
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkEmailVerification(@RequestParam("email") String email) {
        return ResponseEntity.ok(emailService.checkEmailVerification(email));
    }

    @GetMapping("/mine-notifications")
    public ResponseEntity<PageShortDto<NotificationShortDto>> mineNotifications(@RequestParam String userKeyId,
                                                                                @PageableDefault(size = 15) Pageable pageable) {
        return ResponseEntity.ok(notificationService.mine(userKeyId, pageable));
    }
}
