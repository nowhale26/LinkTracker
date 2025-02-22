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

/**
 * ApiErrorResponse
 */
@Validated
public class ApiErrorResponse   {
  @JsonProperty("description")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String description = null;

  @JsonProperty("code")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String code = null;

  @JsonProperty("exceptionName")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String exceptionName = null;

  @JsonProperty("exceptionMessage")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String exceptionMessage = null;

  @JsonProperty("stacktrace")
  @Valid
  private List<String> stacktrace = null;

  public ApiErrorResponse description(String description) {

    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   **/

  @Schema(description = "")

  public String getDescription() {
    return description;
  }



  public void setDescription(String description) {
    this.description = description;
  }

  public ApiErrorResponse code(String code) {

    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   **/

  @Schema(description = "")

  public String getCode() {
    return code;
  }



  public void setCode(String code) {
    this.code = code;
  }

  public ApiErrorResponse exceptionName(String exceptionName) {

    this.exceptionName = exceptionName;
    return this;
  }

  /**
   * Get exceptionName
   * @return exceptionName
   **/

  @Schema(description = "")

  public String getExceptionName() {
    return exceptionName;
  }



  public void setExceptionName(String exceptionName) {
    this.exceptionName = exceptionName;
  }

  public ApiErrorResponse exceptionMessage(String exceptionMessage) {

    this.exceptionMessage = exceptionMessage;
    return this;
  }

  /**
   * Get exceptionMessage
   * @return exceptionMessage
   **/

  @Schema(description = "")

  public String getExceptionMessage() {
    return exceptionMessage;
  }



  public void setExceptionMessage(String exceptionMessage) {
    this.exceptionMessage = exceptionMessage;
  }

  public ApiErrorResponse stacktrace(List<String> stacktrace) {

    this.stacktrace = stacktrace;
    return this;
  }

  public ApiErrorResponse addStacktraceItem(String stacktraceItem) {
    if (this.stacktrace == null) {
      this.stacktrace = new ArrayList<String>();
    }
    this.stacktrace.add(stacktraceItem);
    return this;
  }

  /**
   * Get stacktrace
   * @return stacktrace
   **/

  @Schema(description = "")

  public List<String> getStacktrace() {
    return stacktrace;
  }



  public void setStacktrace(List<String> stacktrace) {
    this.stacktrace = stacktrace;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiErrorResponse apiErrorResponse = (ApiErrorResponse) o;
    return Objects.equals(this.description, apiErrorResponse.description) &&
        Objects.equals(this.code, apiErrorResponse.code) &&
        Objects.equals(this.exceptionName, apiErrorResponse.exceptionName) &&
        Objects.equals(this.exceptionMessage, apiErrorResponse.exceptionMessage) &&
        Objects.equals(this.stacktrace, apiErrorResponse.stacktrace);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, code, exceptionName, exceptionMessage, stacktrace);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiErrorResponse {\n");

    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    exceptionName: ").append(toIndentedString(exceptionName)).append("\n");
    sb.append("    exceptionMessage: ").append(toIndentedString(exceptionMessage)).append("\n");
    sb.append("    stacktrace: ").append(toIndentedString(stacktrace)).append("\n");
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
