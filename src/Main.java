import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Add meg a homersekletet:");
        int szam = input.nextInt();

        System.out.println("Beirt homerseklet: " + szam);

        if (szam <= 0) {
            System.out.println("Fagypont vagy az alatt van.");
        } else {
            System.out.println("Fagypont felett van.");
        }

        if (szam >= 30) {
            System.out.println("Nagyon meleg.");
        }

        input.close();
    }
}
