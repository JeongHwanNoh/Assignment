package kopo.poly.service;


import kopo.poly.dto.NoticeDTO;

import java.util.List;

public interface INoticeJoinService {

    List<NoticeDTO> getNoticeListUsingJoinColumn();

    List<NoticeDTO> getNoticeListUsingNativeQuery();

    List<NoticeDTO> getNoticeListUsingJPQL();

    List<NoticeDTO> getNoticeListForQueryDSL();

    NoticeDTO getNoticeInfoForQueryDSL(NoticeDTO pDTO, boolean type) throws Exception;

}