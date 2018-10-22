package com.example.demo;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCaching
public class DemoApplication implements CommandLineRunner{
    @Autowired
    private  UserRepository userRepository;
    public static ConfigurableApplicationContext ac;
    public static void main(String[] args) {
        DemoApplication.ac=  SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User shubham = new User("Shubham", 2000);
        User pankaj = new User("Pankaj", 29000);
        User lewis = new User("Lewis", 550);

        userRepository.save(shubham);
        userRepository.save(pankaj);
        userRepository.save(lewis);
    }
}
