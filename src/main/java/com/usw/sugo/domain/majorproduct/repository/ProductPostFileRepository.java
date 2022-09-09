package com.usw.sugo.domain.majorproduct.repository;

import com.usw.sugo.domain.majorproduct.ProductPostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPostFileRepository extends JpaRepository<ProductPostFile, Long> {

}
