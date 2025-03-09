package task_management.notification;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import task_management.dto.KafkaDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    @Value("${spring.mail.mailTo}")
    private String mailTo;

    @Value("${spring.mail.subjectForEmail}")
    private String subjectForEmail;

    private final JavaMailSender mailSender;

    public void sendEmail(KafkaDto kafkaDto) {
        try {
            String forEmail = String.format("ID задачи: %s\nСтатус задачи: %s",
                    kafkaDto.getId(), kafkaDto.getStatus());
            log.info("Отправка электронного письма: {}, на {}", subjectForEmail, mailTo);

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setTo(mailTo);
            messageHelper.setSubject(subjectForEmail);
            messageHelper.setText(forEmail);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Не удалось отправить электронное письмо {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
