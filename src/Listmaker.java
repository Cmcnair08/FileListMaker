import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Listmaker {
    private static ArrayList<String> list = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static boolean needsToBeSaved = false;
    private static String currentFileName = null;

    public static void main(String[] args) {
        try {
            menuLoop();
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }

    private static void menuLoop() throws IOException {
        String command;
        do {
            displayMenu();
            command = SafeInput.getRegExString(scanner,
                    "Enter command: ", "[AaDdIiVvQqMmOoSsCc]");
            executeCommand(command.toUpperCase());
        } while (!command.equalsIgnoreCase("Q"));
    }

    private static void displayMenu() {
        System.out.println("\nCurrent List:");
        printList();
        System.out.println("Options:");
        System.out.println("A - Add an item");
        System.out.println("D - Delete an item");
        System.out.println("I - Insert an item");
        System.out.println("M - Move an item");
        System.out.println("V - View the list");
        System.out.println("C - Clear the list");
        System.out.println("S - Save list to file");
        System.out.println("O - Open list from file");
        System.out.println("Q - Quit the program");
    }

    private static void executeCommand(String command) throws IOException {
        switch (command) {
            case "A": addItem(); break;
            case "D": deleteItem(); break;
            case "I": insertItem(); break;
            case "M": moveItem(); break;
            case "V": printList(); break;
            case "C": clearList(); break;
            case "S": saveFile(); break;
            case "O": openFile(); break;
            case "Q":
                if (needsToBeSaved) {
                    boolean save = SafeInput.getYNConfirm(scanner, "You have unsaved changes. Save before quitting?");
                    if (save) saveFile();
                }
                if (SafeInput.getYNConfirm(scanner, "Are you sure you want to quit?")) {
                    System.out.println("Exiting program.");
                } else {
                    return; // skip quitting
                }
                break;
        }
    }

    private static void addItem() {
        String item = SafeInput.getNonZeroLenString(scanner, "Enter item to add: ");
        list.add(item);
        needsToBeSaved = true;
    }

    private static void deleteItem() {
        if (list.isEmpty()) {
            System.out.println("List is empty.");
            return;
        }
        printList();
        int index = SafeInput.getRangedInt(scanner, "Enter item number to delete: ", 1, list.size()) - 1;
        list.remove(index);
        needsToBeSaved = true;
    }

    private static void insertItem() {
        printList();
        String item = SafeInput.getNonZeroLenString(scanner, "Enter item to insert: ");
        int index = SafeInput.getRangedInt(scanner, "Enter position to insert at: ", 1, list.size() + 1) - 1;
        list.add(index, item);
        needsToBeSaved = true;
    }

    private static void moveItem() {
        if (list.size() < 2) {
            System.out.println("Need at least two items to move.");
            return;
        }
        printList();
        int fromIndex = SafeInput.getRangedInt(scanner, "Enter item number to move: ", 1, list.size()) - 1;
        int toIndex = SafeInput.getRangedInt(scanner, "Enter new position: ", 1, list.size()) - 1;
        String item = list.remove(fromIndex);
        list.add(toIndex, item);
        needsToBeSaved = true;
    }

    private static void clearList() {
        list.clear();
        needsToBeSaved = true;
        System.out.println("List cleared.");
    }

    private static void printList() {
        if (list.isEmpty()) {
            System.out.println("The list is empty.");
        } else {
            for (int i = 0; i < list.size(); i++) {
                System.out.println((i + 1) + ": " + list.get(i));
            }
        }
    }

    private static void saveFile() throws IOException {
        if (currentFileName == null) {
            currentFileName = SafeInput.getNonZeroLenString(scanner, "Enter filename to save as (no extension): ");
        }
        Path path = Paths.get(currentFileName + ".txt");
        Files.write(path, list);
        needsToBeSaved = false;
        System.out.println("List saved to " + path.toString());
    }

    private static void openFile() throws IOException {
        if (needsToBeSaved) {
            boolean save = SafeInput.getYNConfirm(scanner, "You have unsaved changes. Save before opening a new file?");
            if (save) {
                saveFile();
            }
        }

        String fileName = SafeInput.getNonZeroLenString(scanner, "Enter filename to open (no extension): ");
        Path path = Paths.get(fileName + ".txt");

        if (!Files.exists(path)) {
            System.out.println("File does not exist.");
            return;
        }

        List<String> lines = Files.readAllLines(path);
        list = new ArrayList<>(lines);
        currentFileName = fileName;
        needsToBeSaved = false;
        System.out.println("List loaded from " + path.toString());
    }
}
