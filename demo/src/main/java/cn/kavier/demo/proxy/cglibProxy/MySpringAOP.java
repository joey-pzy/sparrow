package cn.kavier.demo.proxy.cglibProxy;

import net.sf.cglib.proxy.Enhancer;

public class MySpringAOP {


    public static void main(String[] args) {
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(MyController.class);
//        enhancer.setCallback(new MyCglibProxy());
//        MyController myController = (MyController) enhancer.create();
//        myController.print("joey");

        MyCglibProxy myCglibProxy = new MyCglibProxy(new MyController());
        MyController myController = (MyController) myCglibProxy.getProxyBean();
        System.out.println(myController.getStudent(1).toString());
    }


}
