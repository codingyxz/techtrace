package sample;

/**
 * 期望：
 * public class HelloWorld {
 * *    public void verify(String username, String password) throws IllegalArgumentException {
 * *        return;
 * *    }
 * }
 */
public class HelloWorldForEmptyMethod {

    public void verify(String username, String password) throws IllegalArgumentException {
        if ("tomcat".equals(username) && "123456".equals(password)) {
            return;
        }
        throw new IllegalArgumentException("username or password is not correct");
    }

}
