package com.yxz.java.proxy;

public class HelloServiceImpl implements HelloService {

    private boolean flag = false;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public String sayHello(String word) {
        String res = word;
        if (flag) {
            res = res + "--- true------";
        } else {
            res = res + "--- false------";
        }
        System.out.println(res);
        return res;
    }
}
