package com.wtu.controller;

import com.wtu.entity.User;
import com.wtu.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "测试接口")
@Slf4j
public class TestController {



     @GetMapping("/getInfo")
     @Operation(description = "测试接口")
     public Result<User> test() {
         User user = new User("2204240115", "管海峰", 20);
         return Result.success(user);
     }


}
