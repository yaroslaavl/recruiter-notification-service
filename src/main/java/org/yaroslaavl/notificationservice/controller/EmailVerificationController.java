package org.yaroslaavl.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yaroslaavl.notificationservice.dto.InitialRegistrationRequestDto;
import org.yaroslaavl.notificationservice.service.EmailVerificationService;
import org.yaroslaavl.notificationservice.validation.groups.CandidateAction;
import org.yaroslaavl.notificationservice.validation.groups.RecruiterAction;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/request-verification-candidate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void sendRequestVerificationCandidate(@RequestBody @Validated(CandidateAction.class)
                                                 InitialRegistrationRequestDto initialRegistrationRequestDto) {
        emailVerificationService.requestVerification(initialRegistrationRequestDto);
    }

    @PostMapping("/request-verification-recruiter")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void sendRequestVerificationRecruiter(@RequestBody @Validated(RecruiterAction.class)
                                                     InitialRegistrationRequestDto initialRegistrationRequestDto) {
        emailVerificationService.requestVerification(initialRegistrationRequestDto);
    }

    @PostMapping("/verify-code")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void verifyEmailCode(@RequestParam("verificationCode") String verificationCode,
                                @RequestParam("email") String email) {
        emailVerificationService.verifyCode(verificationCode, email);
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkEmailVerification(@RequestParam("email") String email) {
        return ResponseEntity.ok(emailVerificationService.checkEmailVerification(email));
    }
}
