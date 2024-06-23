package kopo.poly.service;

public interface ILikeService {

    boolean toggleLike(Long noticeSeq, String userId);

    Long getLikeCount(Long noticeSeq);
}
