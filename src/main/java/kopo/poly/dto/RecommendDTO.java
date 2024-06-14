package kopo.poly.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record RecommendDTO(


        String title,
        String author,
        String image,

        String isbn

) implements Serializable {
}
