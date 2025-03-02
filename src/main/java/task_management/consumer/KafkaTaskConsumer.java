package task_management.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import task_management.dto.KafkaDto;
import task_management.notification.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTaskConsumer {

    private final NotificationService mailSendService;

    @Retryable(retryFor = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(KafkaDto message, Acknowledgment ack) {

        try {
            log.info("СООБЩЕНЕ ПОЛУЧЕНО {}", message);
            ack.acknowledge();

            mailSendService.sendEmail(message);
        } catch (Exception ex) {
            log.error("Ошибка при обработке сообщения: {}", ex.getMessage());
            throw ex;
        }
    }

}

