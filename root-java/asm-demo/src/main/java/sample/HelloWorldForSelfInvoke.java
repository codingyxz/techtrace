package sample;

public class HelloWorldForSelfInvoke {

    public void test(int a, int b) {
        int c = Math.addExact(a, b);
        String line = String.format("%d + %d = %d", a, b, c);
        System.out.println(line);
    }

}
