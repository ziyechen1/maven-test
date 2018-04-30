package com.service.imp;


import com.annotation.Service;
import com.service.MyService;

@Service("myServiceImp")
public class MyServiceImp implements MyService {

	public String getName(String name) {
		System.out.println("test my spring mvc: " + name);
		return name + ": test.";
	}

}
