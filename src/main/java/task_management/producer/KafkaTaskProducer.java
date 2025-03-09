package task_management.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTaskProducer {

    private final KafkaTemplate kafkaTemplate;

    @Retryable(retryFor = {Exception.class}, maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 3))
    public void sendTo(Object message) {
        try {
            log.info("Отправка сообщения в Kafka: {}", message);
            kafkaTemplate.sendDefault(message);
        } catch (Exception ex) {
            log.error("Ошибка при отправке сообщения в Kafka: {} ", message);
            throw new RuntimeException("Не удалось отправить сообщение в Kafka", ex);
        }
    }
}
