package sample;

public class HelloWorld {

    @CustomAnnotation(name = "tomcat", age = 18)
    String name;

    public HelloWorld() {
    }

    public static int add1(int a, int b) {
        int s = a + b;
        return s;
    }

    public int add2(int a, int b) {
        return a + b;
    }

    public long add3(long a, long b) {
        return a + b;
    }

}

