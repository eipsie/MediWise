package com.wtu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "GLM聊天接口", description = "提供与智谱GLM大模型交互的API")
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

}
