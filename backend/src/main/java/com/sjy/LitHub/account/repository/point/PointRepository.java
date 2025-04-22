package com.sjy.LitHub.account.repository.point;

import com.sjy.LitHub.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<User, Long>, PointRepositoryCustom { }