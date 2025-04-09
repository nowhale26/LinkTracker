package backend.academy.scrapper.botclient;

import backend.academy.scrapper.botclient.model.LinkUpdate;

public interface UpdateSender {

    void sendUpdate(LinkUpdate update);
}
