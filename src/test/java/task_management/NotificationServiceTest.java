package task_management;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import task_management.dto.KafkaDto;
import task_management.entity.TaskStatus;
import task_management.notification.NotificationService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Отправка почты")
    void sendEmail_ToAddressNull() {

        KafkaDto kafkaDto = new KafkaDto(UUID.randomUUID(), TaskStatus.CANCELED);

        assertThrows(IllegalArgumentException.class, () -> notificationService.sendEmail(kafkaDto));
    }

    @Test
    @DisplayName("Отправка почты с null ID задачи")
    void sendEmail_NullTaskId_ThrowsException() {

        KafkaDto kafkaDto = new KafkaDto(null, TaskStatus.CANCELED);

        assertThrows(IllegalArgumentException.class, () -> notificationService.sendEmail(kafkaDto));
    }

    @Test
    @DisplayName("Отправка почты с ошибкой при вызове метода send")
    void sendEmail_MessagingExceptionThrown() {

        KafkaDto kafkaDto = new KafkaDto(UUID.randomUUID(), TaskStatus.CANCELED);

        doThrow(new RuntimeException("Ошибка отправки"))
                .when(mailSender).send(any(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> notificationService.sendEmail(kafkaDto));
    }

    @Test
    @DisplayName("Отправка почты с ошибкой Jakarta MessagingException")
    void sendEmail_JakartaMessagingExceptionThrown() throws Exception {

        KafkaDto kafkaDto = new KafkaDto(UUID.randomUUID(), TaskStatus.CANCELED);

        MimeMessageHelper spyHelper = Mockito.spy(new MimeMessageHelper(mimeMessage, true));

        doThrow(new jakarta.mail.MessagingException("Ошибка Jakarta"))
                .when(spyHelper).setText(anyString());

        assertThrows(RuntimeException.class, () -> notificationService.sendEmail(kafkaDto));
    }

}