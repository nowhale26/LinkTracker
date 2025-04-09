package backend.academy.scrapper.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.externalapi.ExternalApiRequest;
import backend.academy.scrapper.externalapi.ExternalApiResponse;
import backend.academy.scrapper.repository.entity.Link;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DescriptionMessageTest extends BaseTest {

    private final Map<String, List<ExternalApiRequest>> externalApiMap = new HashMap<>();

    @Autowired
    public DescriptionMessageTest(List<ExternalApiRequest> externalApiRequestList) {
        for (var externalApi : externalApiRequestList) {
            if (externalApiMap.containsKey(externalApi.getSiteName())) {
                externalApiMap.get(externalApi.getSiteName()).add(externalApi);
            } else {
                List<ExternalApiRequest> externalApiList = new ArrayList<>();
                externalApiList.add(externalApi);
                externalApiMap.put(externalApi.getSiteName(), externalApiList);
            }
        }
    }

    @Test
    public void descriptionGithubTest() {
        String expectedMessage =
                """
            Ссылка: https://github.com/nowhale26/abc
            Issue: info
            Пользователь: username
            Превью описания: preview
            """;
        Link link = new Link();
        link.setUrl("https://github.com/nowhale26/abc");
        Set<String> descriptions = new HashSet<>();
        ExternalApiResponse response = new ExternalApiResponse();
        response.setUserName("username");
        response.setPreview("preview");
        response.setMainInfo("info");
        for (var externalApi : externalApiMap.get("github")) {
            descriptions.add(externalApi.formMessage(response, link));
        }
        assertThat(descriptions).contains(expectedMessage);
    }

    @Test
    public void descriptionStackoverflowTest() {
        String expectedMessage =
                """
            Ссылка: https://stackoverflow.com/questions/1/a
            Тема вопроса: info
            Пользователь: username
            Ответ: preview
            """;
        Link link = new Link();
        link.setUrl("https://stackoverflow.com/questions/1/a");
        Set<String> descriptions = new HashSet<>();
        ExternalApiResponse response = new ExternalApiResponse();
        response.setUserName("username");
        response.setPreview("preview");
        response.setMainInfo("info");
        for (var externalApi : externalApiMap.get("stackoverflow")) {
            descriptions.add(externalApi.formMessage(response, link));
        }
        assertThat(descriptions).contains(expectedMessage);
    }
}
