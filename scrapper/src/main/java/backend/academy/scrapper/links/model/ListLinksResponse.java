package backend.academy.scrapper.links.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.validation.annotation.Validated;

/** ListLinksResponse */
@Validated
public class ListLinksResponse {
    @JsonProperty("links")
    @Valid
    private List<LinkResponse> links = null;

    @JsonProperty("size")
    @JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
    @JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
    private Integer size = null;

    public ListLinksResponse links(List<LinkResponse> links) {

        this.links = links;
        return this;
    }

    public ListLinksResponse addLinksItem(LinkResponse linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<LinkResponse>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Get links
     *
     * @return links
     */
    @Schema(description = "")
    @Valid
    public List<LinkResponse> getLinks() {
        return links;
    }

    public void setLinks(List<LinkResponse> links) {
        this.links = links;
    }

    public ListLinksResponse size(Integer size) {

        this.size = size;
        return this;
    }

    /**
     * Get size
     *
     * @return size
     */
    @Schema(description = "")
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListLinksResponse listLinksResponse = (ListLinksResponse) o;
        return Objects.equals(this.links, listLinksResponse.links) && Objects.equals(this.size, listLinksResponse.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(links, size);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ListLinksResponse {\n");

        sb.append("    links: ").append(toIndentedString(links)).append("\n");
        sb.append("    size: ").append(toIndentedString(size)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /** Convert the given object to string with each line indented by 4 spaces (except the first line). */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
