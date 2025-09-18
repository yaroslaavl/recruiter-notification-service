package org.yaroslaavl.notificationservice.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaroslaavl.notificationservice.dto.InitialRegistrationRequestDto;
import org.yaroslaavl.notificationservice.exception.EmailException;
import org.yaroslaavl.notificationservice.feignClient.user.UserFeignClient;
import org.yaroslaavl.notificationservice.service.EmailService;
import org.yaroslaavl.notificationservice.service.RedisService;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailServiceImpl implements EmailService {

    @Value("${mail.send_from}")
    private String mailFrom;

    private final JavaMailSender javaMailSender;
    private final UserFeignClient userAccountExists;
    private final RedisService redisService;

    private static final String VERIFICATION = "VERIFICATION_";
    private static final String EMAIL_STATUS_VERIFICATION = "VERIFIED_EMAIL";
    private static final String MAIL_SUBJECT = "REGISTRATION CONFIRMATION CODE";

    /**
     * Sends a verification code to the user's email if the email is not already registered.
     * The verification code is stored in a Redis datastore with a specified time-to-live (TTL).
     * If a token already exists for the email, it is deleted before creating a new one.
     *
     * @param initialRegistrationRequestDto the data transfer object containing user registration details,
     *                                      primarily the email to be verified.
     * @throws MailSendException if an error occurs while sending the verification email.
     * @throws EmailException if the email is already registered or other validation failures occur.
     */
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

    /**
     * Verifies the provided verification code for the specified email.
     * Ensures that the email has not been previously registered and the verification code
     * matches the code stored in the system's Redis cache.
     * If the verification is successful, marks the email as verified in the cache.
     *
     * @param verificationCode the verification code provided by the user for validation.
     * @param email the email address being verified.
     * @throws EmailException if the email is already registered in the system, the verification
     *                        session has expired, the verification code does not match,
     *                        or any internal error occurs during the process.
     */
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

            redisService.setToken(redisKey, EMAIL_STATUS_VERIFICATION, 30, TimeUnit.MINUTES);
            log.info("Email {} successfully verified", email);
        } catch (Exception e) {
            log.error("Failed to verify code for email {}: {}", email, e.getMessage());
            throw new EmailException("Failed to verify email due to an internal error");
        }
    }

    /**
     * Checks the verification status of a given email address.
     * Ensures that the email has been marked as verified in the Redis datastore.
     *
     * @param email the email address to check for verification status.
     * @return the email address if it has been successfully verified.
     * @throws EmailException if the verification token is expired, invalid, or the email has not been verified.
     */
    @Override
    public String checkEmailVerification(String email) {
        String hasToken = redisService.hasToken(VERIFICATION + email);

        if (hasToken == null || !hasToken.equals(EMAIL_STATUS_VERIFICATION)) {
            log.warn("Verification code is expired or not valid");
            throw new EmailException("Verification code is expired or not valid");
        }
        return email;
    }

    @Override
    @SneakyThrows
    public void sendEmail(String to, String subject, String body) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setFrom(mailFrom);
        helper.setSubject(subject);
        helper.setText(body, true);
        this.javaMailSender.send(message);
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
