package cn.joey.canal.adapter.service;

import cn.joey.canal.adapter.mapper.CanalAnimal;
import cn.joey.canal.adapter.mapper.CanalAnimalMapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CanalAnimalServiceImpl extends ServiceImpl<CanalAnimalMapper, CanalAnimal> implements ICanalAnimalService {


    @Scheduled(fixedRate = 3000)
    @Transactional(rollbackFor = Exception.class)
    public void insert() {
        List<CanalAnimal> list  =new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(new CanalAnimal(IdWorker.getId(), "张三"+IdWorker.getId(), "描述"));
        }

        baseMapper.insertB(list);
    }
}
