package sample;

public class HelloWorldForEdit {
    private int intValue;
    private String strValue;

    public int add(int a, int b) {
        return a + b;
    }

    public void test() {
        System.out.println("this is a test method.");
    }

    public int test(String name, int age, long idCard, Object obj) {
        int hashCode = 0;
        hashCode += name.hashCode();
        hashCode += age;
        hashCode += (int) (idCard % Integer.MAX_VALUE);
        hashCode += obj.hashCode();
        return hashCode;
    }

}
