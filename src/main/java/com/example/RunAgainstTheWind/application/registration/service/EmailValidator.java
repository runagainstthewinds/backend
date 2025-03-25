package com.example.RunAgainstTheWind.application.registration.service;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

/*
 * Email validation
 * Contains @ and contrains .
 */
@Service
public class EmailValidator implements Predicate<String> {

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public boolean test(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
