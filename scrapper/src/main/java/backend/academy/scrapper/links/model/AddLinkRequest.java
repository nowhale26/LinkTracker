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

/** AddLinkRequest */
@SuppressWarnings("CPD-START")
@Validated
public class AddLinkRequest {
    @JsonProperty("link")
    @JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
    @JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
    private String link = null;

    @JsonProperty("tags")
    @Valid
    private List<String> tags = null;

    @JsonProperty("filters")
    @Valid
    private List<String> filters = null;

    public AddLinkRequest link(String link) {

        this.link = link;
        return this;
    }

    /**
     * Get link
     *
     * @return link
     */
    @Schema(description = "")
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public AddLinkRequest tags(List<String> tags) {

        this.tags = tags;
        return this;
    }

    public AddLinkRequest addTagsItem(String tagsItem) {
        if (this.tags == null) {
            this.tags = new ArrayList<String>();
        }
        this.tags.add(tagsItem);
        return this;
    }

    /**
     * Get tags
     *
     * @return tags
     */
    @Schema(description = "")
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public AddLinkRequest filters(List<String> filters) {

        this.filters = filters;
        return this;
    }

    public AddLinkRequest addFiltersItem(String filtersItem) {
        if (this.filters == null) {
            this.filters = new ArrayList<String>();
        }
        this.filters.add(filtersItem);
        return this;
    }

    /**
     * Get filters
     *
     * @return filters
     */
    @Schema(description = "")
    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddLinkRequest addLinkRequest = (AddLinkRequest) o;
        return Objects.equals(this.link, addLinkRequest.link)
                && Objects.equals(this.tags, addLinkRequest.tags)
                && Objects.equals(this.filters, addLinkRequest.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, tags, filters);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AddLinkRequest {\n");

        sb.append("    link: ").append(toIndentedString(link)).append("\n");
        sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
        sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
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
