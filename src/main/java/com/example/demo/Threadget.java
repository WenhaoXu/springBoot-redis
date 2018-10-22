package com.example.demo;

import com.example.demo.service.JredisLockService;

public class Threadget extends Thread{
    private  JredisLockService service;
    public Threadget(JredisLockService service){
        this.service=service;
    }

    @Override
    public void run(){
        service.seckill();
    }

    public static void main(String[] args) {
        JredisLockService service = new JredisLockService();
        for (int i = 0; i < 50; i++) {
            Threadget threadA = new Threadget(service);
            threadA.start();
        }
    }

}
