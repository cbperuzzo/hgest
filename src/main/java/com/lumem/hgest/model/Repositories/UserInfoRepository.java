package com.lumem.hgest.model.Repositories;

import com.lumem.hgest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<User,Long>{
    User getByUserName(String userName);
}
