package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.botclient.BotClient;
import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.common.exception.ScrapperException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LinksScheduler {

    @Autowired
    SchedulerService service;

    @Autowired
    BotClient botClient;

    //fixedDelay = 3600000, initialDelay = 3600000 раз в час
    @Scheduled(fixedDelay = 60000, initialDelay = 60000) //fixedDelay = 60000, initialDelay = 60000 раз в минуту
    public void checkAllUpdates() {
        HashMap<String, List<Long>> updatedLinks = service.findUpdatedLinks();
        if(updatedLinks!=null){
            for(var updatedLink : updatedLinks.entrySet()){
                LinkUpdate linkUpdate = formLinkUpdate(updatedLink);
                try {
                    botClient.sendUpdate(linkUpdate);
                } catch(ScrapperException e){
                    log.error(e.getMessage());
                }
            }
        }
    }

    private LinkUpdate formLinkUpdate(Map.Entry<String, List<Long>> updatedLink){
        LinkUpdate linkUpdate = new LinkUpdate();
        linkUpdate.setUrl(updatedLink.getKey());
        linkUpdate.setTgChatIds(updatedLink.getValue());
        linkUpdate.setDescription("Обновление");
        linkUpdate.setId(1L);
        return linkUpdate;
    }
}
