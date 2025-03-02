package task_management.config.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import task_management.dto.KafkaDto;
import task_management.producer.KafkaTaskProducer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String server;

    @Value("${kafka.topic}")
    private String taskStatusUpdates;

    @Bean
    public ProducerFactory<String, KafkaDto> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean(name = "MYBEEN")
    public KafkaTemplate<String, KafkaDto> kafkaTemplate(ProducerFactory<String, KafkaDto> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(value = "kafka.producer.enable", havingValue = "true", matchIfMissing = true)
    public KafkaTaskProducer producerClient(@Qualifier("MYBEEN") KafkaTemplate template) {
        template.setDefaultTopic(taskStatusUpdates);

        return new KafkaTaskProducer(template);
    }

}
