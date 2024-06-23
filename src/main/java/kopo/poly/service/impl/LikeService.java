package kopo.poly.service.impl;

import kopo.poly.repository.LikeRepository;
import kopo.poly.repository.entity.LikeEntity;
import kopo.poly.service.ILikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeService implements ILikeService {

    private final LikeRepository likeRepository;

    @Transactional
    @Override
    public boolean toggleLike(Long noticeSeq, String userId) {
        Optional<LikeEntity> existingLike = likeRepository.findByNoticeSeqAndUserId(noticeSeq, userId);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false; // 좋아요가 취소됨
        } else {
            likeRepository.save(LikeEntity.builder().noticeSeq(noticeSeq).userId(userId).build());
            return true; // 좋아요가 추가됨
        }
    }

    @Override
    public Long getLikeCount(Long noticeSeq) {
        return likeRepository.countByNoticeSeq(noticeSeq);
    }
}
