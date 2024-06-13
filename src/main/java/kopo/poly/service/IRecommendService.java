package kopo.poly.service;
import kopo.poly.dto.RecommendDTO;

import java.util.List;


public interface IRecommendService {

    List<RecommendDTO> getRandomBook();
}
