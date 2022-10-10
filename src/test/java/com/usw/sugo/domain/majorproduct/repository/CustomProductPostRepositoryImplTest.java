//package com.usw.sugo.domain.majorproduct.repository;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.usw.sugo.domain.majorproduct.repository.productpost.ProductPostRepository;
//import com.usw.sugo.domain.majorproduct.repository.productpostfile.ProductPostFileRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class CustomProductPostRepositoryImplTest {
//
//    @Autowired
//    ProductPostRepository productPostRepository;
//
//    @Autowired
//    ProductPostFileRepository productPostFileRepository;
//    @Autowired
//    JPAQueryFactory queryFactory;
//
////    @Test
////    @BeforeEach
////    void PostAndPostFilePreSetting() {
////        ProductPostFile productPostFile1 = ProductPostFile.builder()
////                .id(1L)
////                .imageLink("테스트 이미지링크 1")
////                .build();
////
////        ProductPostFile productPostFile2 = ProductPostFile.builder()
////                .id(2L)
////                .imageLink("테스트 이미지링크 2")
////                .build();
////
////        ProductPostFile productPostFile3 = ProductPostFile.builder()
////                .id(3L)
////                .imageLink("테스트 이미지링크 3")
////                .build();
////
////        ProductPostFile productPostFile4 = ProductPostFile.builder()
////                .id(4L)
////                .imageLink("테스트 이미지링크 4")
////                .build();
////
////        ProductPostFile productPostFile5 = ProductPostFile.builder()
////                .id(5L)
////                .imageLink("테스트 이미지링크 5")
////                .build();
////
////        ProductPostFile productPostFile6 = ProductPostFile.builder()
////                .id(6L)
////                .imageLink("테스트 이미지링크 6")
////                .build();
////
////        ProductPostFile productPostFile7 = ProductPostFile.builder()
////                .id(7L)
////                .imageLink("테스트 이미지링크 7")
////                .build();
////
////        ProductPostFile productPostFile8 = ProductPostFile.builder()
////                .id(8L)
////                .imageLink("테스트 이미지링크 8")
////                .build();
////
////        ProductPostFile productPostFile9 = ProductPostFile.builder()
////                .id(9L)
////                .imageLink("테스트 이미지링크 9")
////                .build();
////
////        ProductPostFile productPostFile10 = ProductPostFile.builder()
////                .id(10L)
////                .imageLink("테스트 이미지링크 10")
////                .build();
////
////        productPostFileRepository.save(productPostFile1);
////        productPostFileRepository.save(productPostFile2);
////        productPostFileRepository.save(productPostFile3);
////        productPostFileRepository.save(productPostFile4);
////        productPostFileRepository.save(productPostFile5);
////        productPostFileRepository.save(productPostFile6);
////        productPostFileRepository.save(productPostFile7);
////        productPostFileRepository.save(productPostFile8);
////        productPostFileRepository.save(productPostFile9);
////        productPostFileRepository.save(productPostFile10);
////
////        ProductPost productPost1 = ProductPost.builder()
////                .id(1L)
////                .contactPlace("테스트 장소 1")
////                .title("테스트 제목 1")
////                .productPostFile(productPostFile1)
////                .build();
////
////        ProductPost productPost2 = ProductPost.builder()
////                .id(2L)
////                .contactPlace("테스트 장소 2")
////                .title("테스트 제목 2")
////                .productPostFile(productPostFile2)
////                .build();
////
////        ProductPost productPost3 = ProductPost.builder()
////                .id(3L)
////                .contactPlace("테스트 장소 3")
////                .title("테스트 제목 3")
////                .productPostFile(productPostFile3)
////                .build();
////
////        ProductPost productPost4 = ProductPost.builder()
////                .id(4L)
////                .contactPlace("테스트 장소 4")
////                .title("테스트 제목 4")
////                .productPostFile(productPostFile4)
////                .build();
////
////        ProductPost productPost5 = ProductPost.builder()
////                .id(5L)
////                .contactPlace("테스트 장소 5")
////                .title("테스트 제목 5")
////                .productPostFile(productPostFile5)
////                .build();
////
////        ProductPost productPost6 = ProductPost.builder()
////                .id(6L)
////                .title("테스트 제목 6")
////                .contactPlace("테스트 장소 6")
////                .productPostFile(productPostFile6)
////                .build();
////
////        ProductPost productPost7 = ProductPost.builder()
////                .id(7L)
////                .title("테스트 제목 7")
////                .contactPlace("테스트 장소 7")
////                .productPostFile(productPostFile7)
////                .build();
////
////        ProductPost productPost8 = ProductPost.builder()
////                .id(8L)
////                .title("테스트 제목 8")
////                .contactPlace("테스트 장소 8")
////                .productPostFile(productPostFile8)
////                .build();
////
////        ProductPost productPost9 = ProductPost.builder()
////                .id(9L)
////                .title("테스트 제목 9")
////                .contactPlace("테스트 장소 9")
////                .productPostFile(productPostFile9)
////                .build();
////
////        ProductPost productPost10 = ProductPost.builder()
////                .id(10L)
////                .title("테스트 제목 10")
////                .contactPlace("테스트 장소 10")
////                .productPostFile(productPostFile10)
////                .build();
////
////        productPostRepository.save(productPost1);
////        productPostRepository.save(productPost2);
////        productPostRepository.save(productPost3);
////        productPostRepository.save(productPost4);
////        productPostRepository.save(productPost5);
////        productPostRepository.save(productPost6);
////        productPostRepository.save(productPost7);
////        productPostRepository.save(productPost8);
////        productPostRepository.save(productPost9);
////        productPostRepository.save(productPost10);
////    }
////
////    @Test
////    void loadMainPagePostList() {
////        List<PostDomainResponse> fetch = queryFactory
////                .select(Projections.constructor(PostDomainResponse.class,
////                        productPost.title, productPost.contactPlace, productPost.productPostFile.imageLink))
////                .from(productPost)
////                .orderBy(productPost.updatedAt.desc())
////                .limit(10)
////                .fetch();
////
////        for (MainPageResponseForm mainPageResponseForm : fetch) {
////            System.out.println(mainPageResponseForm);
////        }
////    }
//}