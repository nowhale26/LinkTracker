package backend.academy.scrapper.kafka;

import backend.academy.scrapper.botclient.model.LinkUpdate;
import lombok.Data;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.concurrent.CountDownLatch;

@Profile("test")
@Component
@Data
public class KafkaListner {

    private final CountDownLatch messageLatch = new CountDownLatch(1);


    private LinkUpdate update;

    @KafkaListener(topics = "${app.update-topic}", groupId ="${spring.kafka.consumer.group-id}")
    public void getMessage(LinkUpdate update){
        setUpdate(update);
        messageLatch.countDown();
    }
}
