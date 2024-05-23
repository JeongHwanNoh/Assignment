package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record BookDTO(

        String userId,
        String title,
        String author,
        String description,
        String imageUrl

) {
}
