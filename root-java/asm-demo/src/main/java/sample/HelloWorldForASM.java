package sample;

public class HelloWorldForASM {

    @CustomAnnotation(name = "tomcat", age = 18)
    String name;

    public HelloWorldForASM() {
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

    public void test(boolean flag) {
        if (flag) {
            System.out.println("value is true");
        } else {
            System.out.println("value is false");
        }
    }

}

