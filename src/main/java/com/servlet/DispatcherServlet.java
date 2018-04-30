package com.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.annotation.Controller;
import com.annotation.Qualifier;
import com.annotation.RequestMapping;
import com.annotation.Service;

public class DispatcherServlet extends HttpServlet {
	
	List<String> classNames = new ArrayList<String>();
	
	Map<String, Object> beans = new HashMap<String, Object>();
	
	Map<String, Method> handlerMap = new HashMap<String, Method>();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// 1.包的扫描
		scanPackage("com");
		
		// 2.controller service实例化
		instance();
		for (Entry<String, Object> entry : beans.entrySet()) {
			System.out.println("[" + entry.getKey() + ":" +entry.getValue() + "]");
		}
		
		// 3.依赖注入
		ioc();
		
		// 4.建立url与controller method 对象的映射关系
		handlermapping();
		for (Entry<String, Method> entry : handlerMap.entrySet()) {
			System.out.println("[" + entry.getKey() + ":" +entry.getValue() + "]");
		}
		
	}

	private void handlermapping() {
		// TODO Auto-generated method stub
		if (beans.isEmpty()) {
			return;
		}
		for (Entry<String, Object> entry : beans.entrySet()) {
			Object instance = entry.getValue();
			Class<? extends Object> clazz = instance.getClass();
			if (clazz.isAnnotationPresent(RequestMapping.class)) {
				RequestMapping rm = clazz.getAnnotation(RequestMapping.class);
				String classUrl = rm.value()[0];
				
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(RequestMapping.class)) {
						RequestMapping methodRm = method.getAnnotation(RequestMapping.class);
						String methodUrl = methodRm.value()[0];
						handlerMap.put(classUrl + methodUrl, method);
					}else {
						continue;
					}
				}
			}
			
		}
	}

	private void ioc() {
		// TODO Auto-generated method stub
		for (Entry<String, Object> entry : beans.entrySet()) {
			Object instance = entry.getValue();
			Class<? extends Object> clazz = instance.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if(field.isAnnotationPresent(Qualifier.class)) {
					Qualifier qualifier = field.getAnnotation(Qualifier.class);
					Object obj = beans.get(qualifier.value());
					field.setAccessible(true);
					try {
						field.set(instance, obj);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
		
	}

	private void instance() {
		// TODO Auto-generated method stub
		if (classNames.isEmpty()) {
			return;
		}
		for (String className : classNames) {
			className = className.replace(".class", "");
			try {
				Class clazz = Class.forName(className);
				if (clazz.isAnnotationPresent(Controller.class)) {
					Object obj = clazz.newInstance();
					RequestMapping rm = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
					beans.put(rm.value()[0], obj);
				}else if (clazz.isAnnotationPresent(Service.class)) {
					Object obj = clazz.newInstance();
					Service service = (Service) clazz.getAnnotation(Service.class);
					beans.put(service.value(), obj);
				}else {
					continue;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private void scanPackage(String packageName) {
		// TODO Auto-generated method stub   file:/F:/eclipse/servers/apache-tomcat-8.5.29/wtpwebapps/MyspringMvc/WEB-INF/classes/com/
		URL url = this.getClass().getClassLoader().getResource(ReplaceTo(packageName));
		//file:D:/workspace/com
		String fileStr = url.getFile();// /F:/eclipse/servers/apache-tomcat-8.5.29/wtpwebapps/MyspringMvc/WEB-INF/classes/com/
		System.out.println(fileStr);
		File file = new File(fileStr);
		String[] filesStr = file.list();
		for (String path : filesStr) {
			File filePath = new File(fileStr + path);
			//如果是目录则继续扫描
			if (filePath.isDirectory()) {
				scanPackage(packageName + "." +path);
			}else {
				//com.Controller.class
				classNames.add(packageName + "." + filePath.getName());
			}
		}
	}

	private String ReplaceTo(String string) {
		// TODO Auto-generated method stub
		return string.replaceAll("\\.", "/");
	}

	public DispatcherServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 9138642298798612615L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();//工程名
		String path = uri.replace(contextPath, "");
		Method method = handlerMap.get(path);
		Object instance = beans.get("/" + path.split("/")[1]);
		try {
			Object object = method.invoke(instance, null);
			System.out.println(instance.getClass().getName() + ": " + object);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	
	

}
