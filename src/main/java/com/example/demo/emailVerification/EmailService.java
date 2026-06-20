package com.example.demo.emailVerification;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(
            String email,
            String verificationLink
    ){
        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(from, "Rooted");
            helper.setTo(email);
            helper.setSubject("Verify your email");

            helper.setText("""
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif; background:#f5f5f5; padding:40px;">
                    
                        <div style="
                            max-width:600px;
                            margin:auto;
                            background:white;
                            padding:40px;
                            border-radius:12px;
                            box-shadow:0 2px 10px rgba(0,0,0,0.08);
                        ">
                    
                            <h1 style="margin-top:0;color:#111827;">
                                Welcome to Rooted 👋
                            </h1>
                    
                            <p style="font-size:16px;color:#374151;">
                                Thanks for joining Rooted.
                            </p>
                    
                            <p style="font-size:16px;color:#374151;">
                                Click the button below to verify your email address:
                            </p>
                    
                            <div style="margin:32px 0;">
                                <a href="%s"
                                   style="
                                       background:#2563eb;
                                       color:white;
                                       padding:14px 28px;
                                       text-decoration:none;
                                       border-radius:8px;
                                       display:inline-block;
                                       font-weight:bold;
                                   ">
                                    Verify Email
                                </a>
                            </div>
                    
                            <p style="font-size:14px;color:#6b7280;">
                                If the button doesn't work, copy and paste this link into your browser:
                            </p>
                    
                            <p>
                                <a href="%s">%s</a>
                            </p>
                    
                            <hr style="border:none;border-top:1px solid #e5e7eb;margin:32px 0;">
                    
                            <p style="font-size:13px;color:#9ca3af;">
                                Rooted Team
                            </p>
                    
                        </div>
                    </body>
                    </html>
                    """.formatted(
                    verificationLink,
                    verificationLink,
                    verificationLink
            ), true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendGreetingEmail(String email, String username) {

        try {
            String safeUsername = HtmlUtils.htmlEscape(username);

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(from, "Rooted");
            helper.setTo(email);
            helper.setSubject("Nice to meet you, " + username + "!");

            helper.setText("""
        <!DOCTYPE html>
        <html>
        <body style="font-family: Arial, sans-serif; background:#f5f5f5; padding:40px;">

            <div style="
                max-width:600px;
                margin:auto;
                background:white;
                padding:40px;
                border-radius:12px;
            ">

                <h1 style="color:#111827;">
                    Nice to meet you, %s 👋
                </h1>

                <p>
                    Your email has been successfully verified.
                </p>

                <p>
                    Welcome to Rooted. Your account is now fully activated.
                </p>

                <p>
                    Join discussions, share ideas, and connect with the community.
                </p>

                <p style="margin-top:32px;">
                    See you on the forum!
                </p>

                <p>
                    <strong>— Rooted Team</strong>
                </p>

            </div>

        </body>
        </html>
        """.formatted(safeUsername), true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send greeting email", e);
        }
    }
}