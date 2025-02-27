package backend.academy.bot.scrapperservice.controller;

import backend.academy.bot.scrapperservice.controller.model.LinkUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdatesController implements UpdatesApi{

    @Autowired
    UpdatesService service;

    @Override
    public ResponseEntity<Void> updatesPost(LinkUpdate body) {
        service.sendUpdate(body);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
