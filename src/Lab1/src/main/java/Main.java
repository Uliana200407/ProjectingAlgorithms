import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose an option:");
        System.out.println("1. Use DefaultExternalMergeSortFile");
        System.out.println("2. Use ExternalMergeSortFile");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                DefaultExternalMergeSortFile.main(args);
                break;
            case 2:
                ExternalMergeSortFile.main(args);
                break;
            default:
                System.out.println("Invalid choice");
                break;
        }
    }
}
