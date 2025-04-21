package backend.academy.scrapper.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.botclient.KafkaBotClient;
import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.links.model.AddLinkRequest;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

@Testcontainers
public class KafkaTest extends BaseTest {

    @Container
    private static final KafkaContainer kafkaContainer =
            new KafkaContainer("apache/kafka-native:3.8.1").withExposedPorts(9092);

    @Autowired
    private KafkaTemplate<String, LinkUpdate> kafkaLinkUpdateTemplate;

    @Autowired
    private KafkaDLQService dlqService;

    @Autowired
    private KafkaListner listner;

    private final ScrapperConfig config;

    @Autowired
    public KafkaTest(ScrapperConfig config) {
        this.config = config;
    }

    @DynamicPropertySource
    public static void kafkaProperties(DynamicPropertyRegistry registry) {
        kafkaContainer.start();
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    public void handleMessageAndDTOTest() throws Exception {
        KafkaBotClient client = new KafkaBotClient(config, kafkaLinkUpdateTemplate);

        LinkUpdate update = new LinkUpdate();
        update.setDescription("test");
        update.setTgChatId(1L);
        client.sendUpdate(update);

        boolean messageReceived = listner.getMessageLatch().await(10, TimeUnit.SECONDS);
        assertThat(messageReceived).isTrue();

        LinkUpdate sentUpdate = listner.getUpdate();
        assertThat(sentUpdate.getDescription()).isEqualTo("test");
        assertThat(sentUpdate.getTgChatId()).isEqualTo(1L);
    }

    @Test
    public void DLQTest() {
        AddLinkRequest dlqRequest = new AddLinkRequest();
        dlqRequest.setLink("http://example.com");
        dlqService.sendToDLQ(dlqRequest);

        AddLinkRequest receivedDlqRequest = consumeDLQMessage();
        assertThat(receivedDlqRequest).isNotNull();
        assertThat(receivedDlqRequest.getLink()).isEqualTo("http://example.com");
    }

    private AddLinkRequest consumeDLQMessage() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "myGroup");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        try (KafkaConsumer<String, AddLinkRequest> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(config.DLQTopic()));
            ConsumerRecords<String, AddLinkRequest> records = consumer.poll(Duration.ofSeconds(10));
            for (ConsumerRecord<String, AddLinkRequest> record : records) {
                return record.value();
            }
        }
        return null;
    }
}
