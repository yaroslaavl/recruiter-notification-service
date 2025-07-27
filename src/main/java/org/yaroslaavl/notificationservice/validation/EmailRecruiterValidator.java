package org.yaroslaavl.notificationservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.yaroslaavl.notificationservice.utils.AllowedEmails;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class EmailRecruiterValidator implements ConstraintValidator<EmailRecruiter, String> {

    private final AllowedEmails allowedEmails;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        String emailLocalPart = email.substring(email.indexOf("@") + 1).toLowerCase();
        if (!allowedEmails.getAllowedEmails().contains(emailLocalPart)) {
            Pattern pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }

        return false;
    }
}
