package kopo.poly.dto;

import lombok.Builder;

@Builder
public record RecommendDTO(


        String title,
        String author,
        String imageUrl,

        String isbn

) {
}
