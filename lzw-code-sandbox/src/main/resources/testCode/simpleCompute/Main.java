import java.util.Scanner;

public class Main {
    public static void main(String[] args)throws Exception {
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        System.out.println("结果:"+(a+b));
        return;
    }
}
//javac -encoding utf-8 .\Main.java
//java Main 1 2