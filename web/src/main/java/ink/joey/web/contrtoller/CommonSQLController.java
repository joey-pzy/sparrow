package ink.joey.web.contrtoller;


import ink.joey.web.config.DataSourceContext;
import ink.joey.web.contrtoller.request.SQLRequest;
import ink.joey.web.mapper.CommonMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/common")
public class CommonSQLController {


    @Resource
    private CommonMapper commonMapper;

    @PostMapping("/executeSQL")
    public void execute(@RequestBody SQLRequest sqlRequest) {
        DataSourceContext.set(sqlRequest.getCode());

        List<Map<String, Object>> maps = commonMapper.executeSQL(sqlRequest.getSql());

        DataSourceContext.remove();
    }
}
