package backend.academy.scrapper.botclient;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.botclient.model.LinkUpdate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

public class KafkaBotClient implements UpdateSender{

    private final String topic;
    private final KafkaTemplate<String, LinkUpdate> kafkaLinkUpdateTemplate;

    public KafkaBotClient(ScrapperConfig config, KafkaTemplate<String, LinkUpdate> kafkaLinkUpdateTemplate) {
        this.topic = config.updateTopic();
        this.kafkaLinkUpdateTemplate = kafkaLinkUpdateTemplate;
    }

    @Override
    public void sendUpdate(LinkUpdate update) {
        kafkaLinkUpdateTemplate.send(topic, update);
    }
}
