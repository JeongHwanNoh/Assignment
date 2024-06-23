package kopo.poly.controller;

import kopo.poly.dto.LikeDTO;
import kopo.poly.service.ILikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {

    private final ILikeService likeService;

    @PostMapping("/toggle")
    @ResponseBody
    public String toggleLike(@RequestBody LikeDTO likeDTO) {
        boolean isLiked = likeService.toggleLike(likeDTO.getNoticeSeq(), likeDTO.getUserId());
        return isLiked ? "좋아요 처리 완료" : "좋아요 취소 완료";
    }

    @GetMapping("/count")
    @ResponseBody
    public Long getLikeCount(@RequestParam Long noticeSeq) {
        return likeService.getLikeCount(noticeSeq);
    }
}
