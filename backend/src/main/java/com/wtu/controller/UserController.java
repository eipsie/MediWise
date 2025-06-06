package com.wtu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "GLM聊天接口", description = "用户接口计较")
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

}
