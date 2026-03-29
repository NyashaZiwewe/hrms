package hrms.performance.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PerspectiveRequest {

    @NotBlank
    private String name;

    private String description;
}
