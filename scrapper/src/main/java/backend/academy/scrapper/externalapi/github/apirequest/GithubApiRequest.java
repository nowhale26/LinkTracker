package backend.academy.scrapper.externalapi.github.apirequest;

import backend.academy.scrapper.externalapi.github.models.GithubResponse;
import backend.academy.scrapper.repository.entity.Link;

public interface GithubApiRequest {

    GithubResponse checkUpdate(Link link);
}
