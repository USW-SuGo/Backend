package com.usw.sugo.global.aws.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BucketDetailPath {

    PRODUCT_POST("post-resources"),
    NOTE("note-resources");

    private final String path;
}
