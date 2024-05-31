package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record BookDTO(

        Long bookSeq,

        String userId,
        String title,
        String author,
        String description,
        String imageUrl,

        String isbn

) {
}
