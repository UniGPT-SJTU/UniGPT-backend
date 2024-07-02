package com.ise.unigpt.utils;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.ise.unigpt.model.Tool;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DynamicFunctionBodyExecutor {

    private final EntityManagerFactory entityManagerFactory;

    public DynamicFunctionBodyExecutor() {
        entityManagerFactory = Persistence.createEntityManagerFactory("your-persistence-unit-name");
    }

    public Tool loadTool(int id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return entityManager.find(Tool.class, id);
    }

    public Object executeTool(Tool tool, String... args) throws Exception {
        // Save the fileBody to a .java file
        String className = "DynamicTool";
        String fileBody = tool.getFileBody();

        // Ensure the class name in the fileBody matches the expected class name
        fileBody = fileBody.replaceFirst("public class \\w+", "public class " + className);

        File sourceFile = new File(className + ".java");
        try (FileWriter writer = new FileWriter(sourceFile)) {
            writer.write(fileBody);
        }

        // Compile the .java file
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath());

        // Load and instantiate the compiled class
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()});
        Class<?> cls = Class.forName(className, true, classLoader);

        // Assuming the handler method is static and returns an Object
        Method handlerMethod = cls.getDeclaredMethod("handler", String[].class);
        return handlerMethod.invoke(null, (Object) args);  // 调用handler并返回结果
    }

    public static void main(String[] args) {
        try {
            DynamicFunctionBodyExecutor executor = new DynamicFunctionBodyExecutor();
            Tool tool = executor.loadTool(1); // Load tool with ID 1
            Object result = executor.executeTool(tool, "param1", "param2"); // Pass actual parameters
            System.out.println(result);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
