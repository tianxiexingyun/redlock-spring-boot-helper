package org.chobit.spring.redlock.interceptor;

public class RedLockAttribute {


    private String name;

    private String methodId;

    public RedLockAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

}
