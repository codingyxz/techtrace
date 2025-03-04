package sample;

public class HelloWorldForState {

    public int val;

    public void test(int a, int b) {
        System.out.println("Before a + b");
        int c = a + b;
        int d = c + 0;
        System.out.println("After a + b");
        this.val = this.val;
        System.out.println(d);
    }
}
