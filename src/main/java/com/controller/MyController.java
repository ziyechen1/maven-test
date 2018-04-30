package com.controller;


import com.annotation.Controller;
import com.annotation.Qualifier;
import com.annotation.RequestMapping;
import com.service.MyService;

@Controller
@RequestMapping("/my")
public class MyController {
	
	@Qualifier("myServiceImp")
	MyService myService;
	
	@RequestMapping("/mytest")
	public String mytest(String name) {
		String result = myService.getName(name);
		System.out.println(result);
		return result;
		
	}
	
	@RequestMapping("/hello")
	public String hello() {
		String result = myService.getName("hello");
		System.out.println(result);
		return result;
		
	}
}
