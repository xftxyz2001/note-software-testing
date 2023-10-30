在本章中，我们为一个简单但完整的应用程序controller创建了一个测试用例。测试用例并不是测试单个的组件，而是检验多个组例，如何一起工作。我们从一个可以用于任何类的简单测试用例开始．然后把新的测试逐个添加到测试用例中，直到所有初始的组件都被测试到。由于断言变得越来越复杂、因此我们通过Hamcrest匹配器找到了一种简化断言的方法。我们预期这个包会日益增长，所以我们为测试类创建了另一个源代码目录。因为测试源代码和领域源代码的目录都位于同一个包中，所以我们仍然可以测试受保护成员和默认的包成员。

## 1.被测部分：Controller模式

### 1.1 Controller简介

一般而言， Controller可以处理以下事务  
- 接受请求  
- 根据请求执行任意常用极端  
- 选择 一个合适的请求处理器  
- 路由请求，以便处理器可以执行相关的业务逻辑  
- 可能提供一个顶层处理器来处理错误和异常

### 1.2 设计接口

Controller模式中涉及四个角色：  
- Request  
- Response  
- RequestHandler  
- Controller

> Controller接受一个Request，分发给一个RequestHandler，并返回一个Response对象。

```java
//首先，定义一个 Request 接口，这个接口只有一个返问请求的唯一名称的getName方法
public interface Request{
    String getName();
}
//其次指定一个空接口。要开始编写代码，你只需要返回一个 Response对象即可。Response 对象所封装的是你可以稍后处理的内容。
public interface Response{

}
//接下来，定义一个能够处理 Request 并返回 Response 的 RequestHandle,RequestHandle是一个辅助组件，被设计用来处理大部分的“肮脏工作”。它可以调用各种类，这些类可能抛出任意类型的异常。Exception就是由process万法抛出的。
public interface RequestHandler{
    Response procees(Request request) throws Exception;
}
//定义一个顶层方法来处理收到的请求。在接受请求之后， controller将请求分发给相应的RequestHandler 。
public interface Controller{
    Response process(Request request);
    //add Handler 方法允许你扩展Controller,而无须修改 Java原代码。
    void addHandler(Request request,RequestHandler requestHandler)
}
```

controller的目的是处理一个请求并返回一个响应。但是,在你处理一个请求之前，设计要求添加一个RequestHandler来做这个处理。

```java
package com.JUnittTest.mastery;

import java.util.HashMap;
import java.util.Map;


public class DefaultController {
//  请求处理器注册表，对每一个request注册对应的requestHandler
    private Map requestHandlers=new HashMap();
//  声明一个受保护的方法，为接受的请求获取RequestHandler
    protected RequestHandler getHandler(Request request){
        if(!this.requestHandlers.containsKey(request.getName())){
            String message="Cannot find handler for request name "+"["+request.getName()+"]";
            throw new RuntimeException(message);
        }
//      向调用者返回相应的requestHandler
        return (RequestHandler)this.requestHandlers.get(request.getName());
    }

//  是Controller类的核心，把response分派给相应的requestHandler，并传回requestHandler的response
    public Response processRequest(Request request){
        Response response;
        try {
//          getHandler(request)返回一个RequestHandler接口类型的对象
//          RequestHandler接口定义了process(request)方法，返回一个response对象
            response=getHandler(request).process(request);
        } catch (Exception exception) {
            response=new ErrorResponse(request,exception);
        }
        return response;
    }
//  检查requestHandler是否已经被注册
    public void addHandler(Request request,RequestHandler requestHandler){
//      如果被注册了就抛出一个异常
        if(this.requestHandlers.containsKey(request.getName())){
            throw new RuntimeException("A request handler has "+"already been registered for request name "+"["+request.getName()+"]");
        }else{
            this.requestHandlers.put(request.getName(), requestHandler);
        }
    }
}
```

我们还需要额外再定义一个ErrorPesponse接口，不同于posponse接口，ErrorPesponse接口返回的是错误的posponse。

```java
package com.JUnittTest.mastery;

public interface Response {
    String getName();
}
```

## 2. 设计单元测试

### 2.1 测试前部署 @Before @BeforeClass

@Before @After 注释方法会在每个@Test方法前后执行

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Before {
}
```

@BeforeClass @AfterClass 注释方法会在只会在所有@Test方法前后执行一次

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeClass {
}
```

从源代码分析，@Before @BeforeClass 注释非常简单，没有内部属性，只是起到对代码运行顺序的引导。（@After @AfterClass也是一样的）

一般在@BeforeClass @AfterClass注释方法中的代码完成测试环境的部署和拆除。  
在@Before @After注释方法中的代码是对公共属性和对象的声明，一般是从@Test注释方法代码中重构得来的。

### 2.2 单元测试 @Test

分析@Test源代码可知，该注释拥有两个属性expected()，timeout()  
expected是单元测试预期抛出异常的类对象，带有该属性的单元测试在抛出预期的异常后测试才会通过。timeout是用来测试单元运行时间的属性，单位为毫秒。要求单元执行时间不能超过设定值，否则测试失败。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Test {

    /**
     * Default empty exception
     */
    static class None extends Throwable {
        private static final long serialVersionUID= 1L;     
        private None() {
        }
    }

    /**
     * Optionally specify <code>expected</code>, a Throwable, to cause a test method to succeed iff 
     * an exception of the specified class is thrown by the method.
     */
    Class<? extends Throwable> expected() default None.class;

    /** 
     * Optionally specify <code>timeout</code> in milliseconds to cause a test method to fail if it
     * takes longer than that number of milliseconds.*/
    long timeout() default 0L; 
}
```

### 2.3 测试类

测试类存于何处：  
- 放在包中作为公有类  
- 作为测试用例类的内部类（类很简单，且不会有后续的改变）

本例中，我们采用第二种方式，被测代码有四个角色  
- Request  
- Response  
- RequestHandler  
- Controller

因此我们创建前三个角色为Controller的内部类，实现原有的方法：

```java
public class TestDefaultController {
    ...
    private class SampleRequest implements Request{
        public String getName(){
            return "Test"
        }
    }
    private class SampleHandler implements RequestHandler{
        public Response process(Request request) throws Exception{
            return new SampleResponse();
        }
    }
    private class SampleResponse implements Response{
        ...
    }

}
```

### 2.4 两种测试对象

要创建一个单元测试，需要创建两种类型的对象：  
- Domain Object: 领域对象（被测对象）  
- Test Object：测试对象（与被测对象交互的对象）  
在一个@Test注释方法中，只测试一个对象（Domain Object），其他对象（Test Object）与被测对象交互完成单元测试

### 2.5 单元用例设计

#### 2.5.1 单元测试编写模式：

1. 通过把环境设直成已知状态（如创建对象，获取资源）来创建测试． 测试前的状态通常称为 Test Fixture.
2. 调用待测试的方法。
3. 确认测试结呆，通常通过调用一个或更多的assert方法来实现  
    主干+分支

#### 2.5.2 单元用例覆盖：主干+分支+可扩展性

先从程序的正向主干逻辑出发，覆盖最基本的逻辑单元。  
然后通过分析条件语句，try/catch语句，找到分支路径进行逐一覆盖。  
可扩展性主要通过测试单元运行时间是否达到要求来给出评估。

##### 主干用例

测试点1：是否能添加一个RequestHandler  
- 添加一个RequestHandler，引用一个Request  
- 获取一个RequestHandler并传递同一个Request  
- 检查获得的RequestHandler是否就是添加的那一个

```java

    @Test
//  测试ProcessRequest方法的主干流程
    public void testProcessRequest(){
//      这部分代码被多个测试类复用，因此我们把它移动到@Before注释的方法中
        /*Request request=new SampleRequest();
        RequestHandler handler=new SampleHandler();
        controller.addHandler(request, handler);*/
//      调用被测方法，然后验证会不会返回方法指定的对象
//      被测方法有参数列表，有返回值，测试方法仍然是无参且无返回值的方法
        Response response=controller.processRequest(request);
        assertNotNull("Must not return a null response: ",response);
//      让测试结果与预期结果的类进行比较，进一步确定返回值的正确性
//      assertEquals("Response should be of type SampleResponse",SampleResponse.class,response.getClass());
        assertEquals(new SampleResponse(),response);
    }
```

测试点2：

```java
@Test
//  测试addHandler方法的主干流程
    public void testAddHandler(){
//      这部分代码被多个测试类复用，因此我们把它移动到@Before注释的方法中
        /*Request request=new SampleRequest();
        RequestHandler handler=new SampleHandler();
//      引用一个Request，添加一个RequestHandler，
        controller.addHandler(request, handler);*/
//      获取一个RequestHandler并传递同一个Request
        RequestHandler handler2=controller.getHandler(request);
//      检查获得的RequestHandler是否就是添加的那一个
        assertSame("Handler we set in controller should be the same handler we got",handler2,handler);
    }
```

##### 分支用例

测试点3：

```java
@Test
//  测试ProcessRequest方法的异常处理流程
    public void testProcessRequestAnswersErrorResponse(){
//      使用SampleRequest带一个参数的构造方法为对象实例request初始化fixture
        SampleRequest request=new SampleRequest("testError");
        SampleExceptionHandler handler=new SampleExceptionHandler();
        controller.addHandler(request, handler);
        Response response=controller.processRequest(request);
//      检查被测方法是否得到返回值
        assertNotNull("Must not return a null response: ",response);
//      检查返回值是不是预期的异常对象
        assertEquals(ErrorResponse.class,response.getClass());
    }
```

这里还需要添加一个测试类，用于模拟processRequest(Request request)抛出异常的情况。

```java
//  用来测试processRequest(Request request)的异常处理流程创建的类
//  processRequest(Request request)方法中调用了接口RequestHandler的方法
//  直接抛出一个异常看程序的try-catch语句块能否如预期捕捉到
    private class SampleExceptionHandler implements RequestHandler{
        @Override
        public Response process(Request request) throws Exception {
            throw new Exception("error processing request");
        }

    }
```

测试点4：

```java
@Test(expected=RuntimeException.class)
//  测试getHandler方法的异常处理流程，预期希望抛出一个异常，则在@Test的属性expected属性中定义异常类，这样这条测试就不会因为有异常而阻塞
    public void testGetHandlerNotDefined(){
        SampleRequest request=new SampleRequest("testGetHandlerNotDefined");
        //The following line is supposed to throw a RuntimeException 
        controller.getHandler(request);
    }

```

测试点5：

```java
    @Test(expected=RuntimeException.class)
//  测试addHandler方法的异常处理流程,预期希望抛出一个异常，则在@Test的属性expected属性中定义异常类,这样这条测试就不会因为有异常而阻塞
    public void testAddRequestDuplicateName(){
        SampleRequest request=new SampleRequest();
        SampleHandler handler=new SampleHandler();
//      The following line is supposed to throw a RuntimeException
        controller.addHandler(request, handler);
    }
```

##### 可扩展性测试

测试点6：

```java

    @Test(timeout=120)
    @Ignore(value="Skip for now")//@Ignore将会对本单元测试跳过执行 value声明跳过的原因
        public void testProcessMultipleRequestsTimeout(){
            Request request;
            Response response=new SampleResponse();
            RequestHandler handler=new SampleHandler();
            for(int i=0;i<99999;i++){
                request=new SampleRequest(String.valueOf(i));
                controller.addHandler(request, handler);
                response=controller.processRequest(request);
                assertNotNull(response);
                assertNotSame(ErrorResponse.class,response.getClass());
            }
        }
```

##### 完整的单元测试代码

```java
package com.JUnittTest.mastery;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class TestDefaultController {
    private DefaultController controller;
    private Request request;
    private RequestHandler handler;
    @Before
//  实例化DefaultController
    public void instantiate() throws Exception{
        controller=new DefaultController();
        request=new SampleRequest();
        handler=new SampleHandler();
        controller.addHandler(request, handler);
    }

    /*@Test
//  对还没有实现的测试代码抛出一个异常
    public void testMethod(){
        throw new RuntimeException("implement me");
    }*/

    @Test
//  测试ProcessRequest方法的主干流程
    public void testProcessRequest(){
//      这部分代码被多个测试类复用，因此我们把它移动到@Before注释的方法中
        /*Request request=new SampleRequest();
        RequestHandler handler=new SampleHandler();
        controller.addHandler(request, handler);*/
//      调用被测方法，然后验证会不会返回方法指定的对象
//      被测方法有参数列表，有返回值，测试方法仍然是无参且无返回值的方法
        Response response=controller.processRequest(request);
        assertNotNull("Must not return a null response: ",response);
//      让测试结果与预期结果的类进行比较，进一步确定返回值的正确性
//      assertEquals("Response should be of type SampleResponse",SampleResponse.class,response.getClass());
        assertEquals(new SampleResponse(),response);
    }

    @Test
//  测试addHandler方法的主干流程
    public void testAddHandler(){
//      这部分代码被多个测试类复用，因此我们把它移动到@Before注释的方法中
        /*Request request=new SampleRequest();
        RequestHandler handler=new SampleHandler();
//      引用一个Request，添加一个RequestHandler，
        controller.addHandler(request, handler);*/
//      获取一个RequestHandler并传递同一个Request
        RequestHandler handler2=controller.getHandler(request);
//      检查获得的RequestHandler是否就是添加的那一个
        assertSame("Handler we set in controller should be the same handler we got",handler2,handler);
    }
    @Test
//  测试ProcessRequest方法的异常处理流程
    public void testProcessRequestAnswersErrorResponse(){
//      使用SampleRequest带一个参数的构造方法为对象实例request初始化fixture
        SampleRequest request=new SampleRequest("testError");
        SampleExceptionHandler handler=new SampleExceptionHandler();
        controller.addHandler(request, handler);
        Response response=controller.processRequest(request);
//      检查被测方法是否得到返回值
        assertNotNull("Must not return a null response: ",response);
//      检查返回值是不是预期的异常对象
        assertEquals(ErrorResponse.class,response.getClass());
    }

    @Test(expected=RuntimeException.class)
//  测试getHandler方法的异常处理流程，预期希望抛出一个异常，则在@Test的属性expected属性中定义异常类，这样这条测试就不会因为有异常而阻塞
    public void testGetHandlerNotDefined(){
        SampleRequest request=new SampleRequest("testGetHandlerNotDefined");
        //The following line is supposed to throw a RuntimeException 
        controller.getHandler(request);
    }

    @Test(expected=RuntimeException.class)
//  测试addHandler方法的异常处理流程,预期希望抛出一个异常，则在@Test的属性expected属性中定义异常类,这样这条测试就不会因为有异常而阻塞
    public void testAddRequestDuplicateName(){
        SampleRequest request=new SampleRequest();
        SampleHandler handler=new SampleHandler();
//      The following line is supposed to throw a RuntimeException
        controller.addHandler(request, handler);
    }

    @Test(timeout=120)
    @Ignore(value="Skip for now")//@Ignore将会对本单元测试跳过执行 value声明跳过的原因
        public void testProcessMultipleRequestsTimeout(){
            Request request;
            Response response=new SampleResponse();
            RequestHandler handler=new SampleHandler();
            for(int i=0;i<99999;i++){
                request=new SampleRequest(String.valueOf(i));
                controller.addHandler(request, handler);
                response=controller.processRequest(request);
                assertNotNull(response);
                assertNotSame(ErrorResponse.class,response.getClass());
            }
        }


    private class SampleRequest implements Request{
//      SampleRequest类有一个默认属性，作为其实例的fixture
        private static final String DEFAULT_NAME="Test";
        private String name;
//      无参的构造方法将默认属性载入初始化
        public SampleRequest(){
            this(DEFAULT_NAME);
        }
//      带String参数的构造方法可以自定义对象实例的属性
        public SampleRequest(String name) {
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
    }

    private class SampleHandler implements RequestHandler{

        public Response process(Request request) throws Exception{
            return new SampleResponse();
        }

    }
    private class SampleResponse implements Response{
        private static final String NAME="Test";
        public SampleResponse() {
            // TODO Auto-generated constructor stub
        }
        //给SampleResponse创建标识，主要是为了测试而设计
        public String getName(){
            return NAME;
        }
        public boolean equals(Object object){
            boolean result=false;
            if(object instanceof SampleResponse){
                result=((SampleResponse)object).getName().equals(getName());
            }
            return result;
        }
        public int hashCode(){
            return NAME.hashCode();
        }
    }
//  用来测试processRequest(Request request)的正常处理，创建的类
//  processRequest(Request request)方法中调用了接口RequestHandler的方法
//  直接抛出一个异常看程序的try-catch语句块能否如预期捕捉到
    private class SampleExceptionHandler implements RequestHandler{
        @Override
        public Response process(Request request) throws Exception {
            throw new Exception("error processing request");
        }

    }
}
```

## 3. 测试结果

![这里写图片描述](https://img-blog.csdn.net/20161220223544431)

去掉testProcessMultipleRequestsTimeout的@Ignore注释后的结果  
![这里写图片描述](https://img-blog.csdn.net/20161220223603016)

## 4. 引入Hamcrest匹配器

```java
package com.JUnittTest.mastery;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 * A sample hamcrest test.
 * 
 * @version $Id: HamcrestTest.java 553 2010-03-06 12:29:58Z paranoid12 $
 */
public class HamcrestTest {

    private List<String> values;

    @Before
    public void setUpList() {
        values = new ArrayList<String>();
        values.add("x");
        values.add("y");
        values.add("z");
    }

    @Test
    public void testWithoutHamcrest() {
        assertTrue(values.contains("one") || values.contains("two")
                || values.contains("three"));
    }

    @Test
//  引入Hamcrest匹配器可以简化测试断言，使断言更具有可读性，Hamcrest语句是可以嵌套使用的
    public void testWithHamcrest() {
        assertThat(values, hasItem(anyOf(equalTo("one"), equalTo("two"),
            equalTo("three"))));
    }

}
```

引入Hamcrest匹配器之后，会显示下面的结果，而没有Hamcrest匹配器的话，只会显示Failure Trace，可读性比较差  
![这里写图片描述](https://img-blog.csdn.net/20161220224149939)

## 5. JUnit最佳实践

1. 一次只能单元测试一个对象  
   1. 选择有意义的测试方法名字
   2. 在assert调用中解释失败的原因
   3. 一个单元测试等于一个@Test方法
   4. 测试任何可能失败的事物
   5. 让测试改善代码
   6. 是异常测试更易于阅读
   7. 总是为跳过的测试说明原因
   8. 相同的包，分离的目录
