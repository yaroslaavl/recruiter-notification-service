package org.yaroslaavl.notificationservice.service;
import org.yaroslaavl.notificationservice.dto.InitialRegistrationRequestDto;

public interface EmailService {

    void requestVerification(InitialRegistrationRequestDto initialRegistrationRequestDto);

    void verifyCode(String verificationCode, String email);

    String checkEmailVerification(String email);

    void sendEmail(String to, String subject, String body);
}
