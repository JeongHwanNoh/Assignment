package kopo.poly.service;

import kopo.poly.dto.CommentDTO;

import java.util.List;

public interface ICommentService {

    List<CommentDTO> getCommentList();

    void updateCommentInfo(CommentDTO pDTO) throws Exception;

    void deleteCommentInfo(CommentDTO pDTO) throws Exception;

    void insertCommentInfo(CommentDTO pDTO) throws Exception;
}
