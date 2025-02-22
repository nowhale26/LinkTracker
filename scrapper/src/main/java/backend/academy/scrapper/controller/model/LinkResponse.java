package backend.academy.scrapper.controller.model;

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


/**
 * LinkResponse
 */
@Validated
public class LinkResponse   {
  @JsonProperty("id")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long id = null;

  @JsonProperty("url")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String url = null;

  @JsonProperty("tags")
  @Valid
  private List<String> tags = null;
  @JsonProperty("filters")
  @Valid
  private List<String> filters = null;

  public LinkResponse id(Long id) {

    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/

  @Schema(description = "")

  public Long getId() {
    return id;
  }



  public void setId(Long id) {
    this.id = id;
  }

  public LinkResponse url(String url) {

    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
   **/

  @Schema(description = "")

  public String getUrl() {
    return url;
  }



  public void setUrl(String url) {
    this.url = url;
  }

  public LinkResponse tags(List<String> tags) {

    this.tags = tags;
    return this;
  }

  public LinkResponse addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<String>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * Get tags
   * @return tags
   **/

  @Schema(description = "")

  public List<String> getTags() {
    return tags;
  }



  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public LinkResponse filters(List<String> filters) {

    this.filters = filters;
    return this;
  }

  public LinkResponse addFiltersItem(String filtersItem) {
    if (this.filters == null) {
      this.filters = new ArrayList<String>();
    }
    this.filters.add(filtersItem);
    return this;
  }

  /**
   * Get filters
   * @return filters
   **/

  @Schema(description = "")

  public List<String> getFilters() {
    return filters;
  }



  public void setFilters(List<String> filters) {
    this.filters = filters;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LinkResponse linkResponse = (LinkResponse) o;
    return Objects.equals(this.id, linkResponse.id) &&
        Objects.equals(this.url, linkResponse.url) &&
        Objects.equals(this.tags, linkResponse.tags) &&
        Objects.equals(this.filters, linkResponse.filters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, url, tags, filters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LinkResponse {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
