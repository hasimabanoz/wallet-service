package com.playtomic.tests.wallet.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.playtomic.tests.wallet.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    List<User> findByName(String name);
    
}
