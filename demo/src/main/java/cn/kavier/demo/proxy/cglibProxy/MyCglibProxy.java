package cn.kavier.demo.proxy.cglibProxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MyCglibProxy implements MethodInterceptor {

    private final Enhancer enhancer = new Enhancer();

    private final Object beProxyObj;

    public MyCglibProxy(Object beProxyObj) {
        this.beProxyObj = beProxyObj;
    }

    public Object getProxyBean() {
        enhancer.setSuperclass(beProxyObj.getClass());
        enhancer.setCallback(this);
        enhancer.setClassLoader(this.getClass().getClassLoader());
        return enhancer.create();
    }

    @Override
    public Object intercept(Object targetO, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        boolean openTransactional = false;
        if (method.getAnnotation(Transactional.class) != null) {
            System.out.println("开启事务");
            openTransactional = true;
        }

        System.out.println("调用方法名称=" + method.getName() + "方法入参=" + Arrays.toString(params));

        Object invokeR = methodProxy.invokeSuper(targetO, params);

        System.out.println("方法执行完! 返回结果为="+invokeR.toString());

        if (openTransactional) {
            System.out.println("关闭事务");
        }
        return invokeR;
    }
}
