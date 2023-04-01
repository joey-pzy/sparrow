package cn.joey.canal.adapter.mapper;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@TableName("tb_canal_animal")
@Data
@AllArgsConstructor
public class CanalAnimal {
    @TableId
    private Long id;

    private String name;
    private String description;
}
