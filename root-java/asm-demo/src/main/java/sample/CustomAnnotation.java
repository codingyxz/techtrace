package sample;

public @interface CustomAnnotation {
    String name() default "";

    int age() default 0;
}
