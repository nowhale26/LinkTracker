package backend.academy.scrapper.kafka;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.redis.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaDLQService {

    private final String topic;
    private final KafkaTemplate<String, AddLinkRequest> kafkaDLQTemplate;

    public KafkaDLQService(ScrapperConfig config, KafkaTemplate<String, AddLinkRequest> kafkaDLQTemplate) {
        this.topic = config.DLQTopic();
        this.kafkaDLQTemplate = kafkaDLQTemplate;
    }

    public void sendToDLQ(AddLinkRequest request){
        kafkaDLQTemplate.send(topic, request);
    }
}
