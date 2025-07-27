package org.yaroslaavl.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaroslaavl.notificationservice.dto.InitialRegistrationRequestDto;
import org.yaroslaavl.notificationservice.exception.EmailException;
import org.yaroslaavl.notificationservice.feignClient.user.UserAccountExists;
import org.yaroslaavl.notificationservice.service.EmailVerificationService;
import org.yaroslaavl.notificationservice.service.RedisService;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Value("${mail.send_from}")
    private String mailFrom;

    private final JavaMailSender javaMailSender;
    private final UserAccountExists userAccountExists;
    private final RedisService redisService;

    private static final String VERIFICATION = "VERIFICATION_";
    private static final String EMAIL_STATUS_VERIFICATION = "VERIFIED_EMAIL";
    private static final String MAIL_SUBJECT = "REGISTRATION CONFIRMATION CODE";

    @Async
    @Override
    public void requestVerification(InitialRegistrationRequestDto initialRegistrationRequestDto) {
        String email = initialRegistrationRequestDto.getEmail();
        Boolean isExist = userAccountExists.existsAccount(email);

        if (!isExist) {
            String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1_000_000));
            String redisKey = VERIFICATION + email;

            String hasToken = redisService.hasToken(redisKey);
            if (hasToken != null && !hasToken.isEmpty()) {
                redisService.deleteToken(redisKey);
            }

            try {
                sendMailVerificationCode(email, code);
                log.info("Mail sent to: {}", email);
            } catch (MailException e) {
                log.error(e.getMessage());
                throw new MailSendException("Unable to send email to: " + email);
            }

            redisService.setToken(redisKey, code, 10, TimeUnit.MINUTES);
        } else {
            log.error("{} already registered.", email);
            throw new EmailException("The mail has been registered in the past");
        }
    }

    @Override
    public void verifyCode(String verificationCode, String email) {
        String redisKey = VERIFICATION + email;
        Boolean isExist = userAccountExists.existsAccount(email);

        if (isExist) {
            log.error("Email {} is already registered in the system", email);
            throw new EmailException("The email has been registered in the past");
        }

        try {
            String storedCode = redisService.hasToken(redisKey);
            if (storedCode == null || storedCode.isEmpty()) {
                throw new EmailException("Email verification session is expired");
            }

            if (!verificationCode.equals(storedCode)) {
                throw new EmailException("Verification code does not match the stored code");
            }

            redisService.setToken(redisKey, EMAIL_STATUS_VERIFICATION, 100, TimeUnit.MINUTES);
            log.info("Email {} successfully verified", email);
        } catch (Exception e) {
            log.error("Failed to verify code for email {}: {}", email, e.getMessage());
            throw new EmailException("Failed to verify email due to an internal error");
        }
    }

    @Override
    public String checkEmailVerification(String email) {
        String hasToken = redisService.hasToken(VERIFICATION + email);

        if (hasToken == null || !hasToken.equals(EMAIL_STATUS_VERIFICATION)) {
            log.warn("Verification code is expired or not valid");
            throw new EmailException("Verification code is expired or not valid");
        }
        return email;
    }

    private void sendMailVerificationCode(String email, String verificationCode) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(mailFrom);
        message.setTo(email);
        message.setSubject(MAIL_SUBJECT);
        message.setText(verificationCode);

        this.javaMailSender.send(message);
    }
}
