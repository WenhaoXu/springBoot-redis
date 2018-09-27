package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Cacheable(value = "users", key = "#userId", unless = "#result.followers < 12000")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public User getUser(@PathVariable String userId) {
        System.out.println("get user by " + userId);
        return userRepository.findById(Long.valueOf(userId)).get();
    }

    @CacheEvict(value = "users", allEntries = true)
    @DeleteMapping("/{id}")
    public void deleteUserByID(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @CachePut(value = "users", key = "#user.id")
    @PutMapping("/update")
    public User updatePersonByID(@RequestBody User user) {
        userRepository.save(user);
        return user;
    }


}
