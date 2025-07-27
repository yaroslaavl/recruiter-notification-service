package org.yaroslaavl.notificationservice.service;
import org.yaroslaavl.notificationservice.dto.InitialRegistrationRequestDto;

public interface EmailVerificationService {

    void requestVerification(InitialRegistrationRequestDto initialRegistrationRequestDto);

    void verifyCode(String verificationCode, String email);

    String checkEmailVerification(String email);
}
