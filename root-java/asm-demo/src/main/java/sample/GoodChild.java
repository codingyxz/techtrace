package sample;

public class GoodChild {
    public String name;
    public int age;

    public GoodChild(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "GoodChild{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}