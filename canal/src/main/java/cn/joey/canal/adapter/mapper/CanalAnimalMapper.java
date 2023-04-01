package cn.joey.canal.adapter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CanalAnimalMapper extends BaseMapper<CanalAnimal> {

    void insertB(List<CanalAnimal> list);

}
