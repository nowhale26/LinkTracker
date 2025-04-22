package backend.academy.bot.kafka;

import backend.academy.bot.scrapperservice.controller.UpdatesService;
import backend.academy.bot.scrapperservice.controller.model.LinkUpdate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaUpdatesListener {

    private final UpdatesService service;

    public KafkaUpdatesListener(UpdatesService service) {
        this.service = service;
    }

    @KafkaListener(topics = "${app.kafka.consumer.topic}", groupId = "${app.kafka.consumer.group-id}")
    public void listener(LinkUpdate update) {
        service.sendUpdate(update);
    }
}
