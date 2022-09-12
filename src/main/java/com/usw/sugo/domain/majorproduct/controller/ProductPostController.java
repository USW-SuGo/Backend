package com.usw.sugo.domain.majorproduct.controller;


import com.querydsl.core.Tuple;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponseForm;
import com.usw.sugo.domain.majorproduct.repository.ProductPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class ProductPostController {

    private final ProductPostRepository productPostRepository;

    @GetMapping("/all")
    public ResponseEntity<List<MainPageResponseForm>> loadMainPage() {
        return ResponseEntity.status(HttpStatus.OK).body(productPostRepository.loadMainPagePostList());
    }
}
