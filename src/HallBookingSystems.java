
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HallBookingSystems {
    static Scanner input = new Scanner(System.in);
    private static String AVAILABLE = "AV";
    private static String BOOKED = "BO";
    private static  int MAX_BOOKINGS = 100;
    private static final int MAX_REBOOT_INTERVAL_DAYS = 1;
    private static int historyIndex = 0;
    private static String[] seatHistory = new String[MAX_BOOKINGS];
    private static String[] hallHistory = new String[MAX_BOOKINGS];
    private static String[] studentIdHistory = new String[MAX_BOOKINGS];
    private static String[] createdAtHistory = new String[MAX_BOOKINGS];

    // Track the last reboot date for each hall
    private static LocalDate lastRebootDateA = LocalDate.now();
    private static LocalDate lastRebootDateB = LocalDate.now();
    private static LocalDate lastRebootDateC = LocalDate.now();

    public static void main(String[] args) {
        while (true) {
            System.out.println();
            System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
            System.out.println("                            CSTAD HALL ");
            System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
            String regex = "\\d+"; // Improved regex for positive integers
            String rows = "";
            String seats = "";

            System.out.print("Enter rows: ");
            rows = input.nextLine();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(rows);

            if (matcher.matches()) {
                int r = Integer.parseInt(rows);
                System.out.print("Rows: " + r);

                boolean validSeats = false;
                while (!validSeats) {
                    System.out.println("Enter seats per row: ");
                    seats = input.nextLine();
                    Matcher matcher1 = pattern.matcher(seats);

                    if (matcher1.matches()) {
                        int s = Integer.parseInt(seats);
                        System.out.println("Seats per row: " + s);

                        String[][] hallA = new String[r][s];
                        String[][] hallB = new String[r][s];
                        String[][] hallC = new String[r][s];

                        String[] seatNames = new String[MAX_BOOKINGS];
                        String[] hallNames = new String[MAX_BOOKINGS];
                        String[] studentIds = new String[MAX_BOOKINGS];
                        String[] createdAts = new String[MAX_BOOKINGS];

                        while (true) {
                            menu();
                            System.out.println("----------------------- Menu --------------------------");
                            System.out.println("Please select menu no: ");
                            char option = input.next().charAt(0);

                            switch (option) {
                                case 'a', 'A':
                                    showtime();
                                    bookSeat( hallA, hallB, hallC);
                                    break;
                                case 'b', 'B':
                                    displayHalls(hallA, hallB, hallC);
                                    break;
                                case 'c', 'C':
                                    showtime();
                                    break;
                                case 'd', 'D':
                                    rebootShowtime(hallA,hallB,hallC);
                                    break;
                                case 'e', 'E':
                                    displayHistory();
                                    break;
                                case 'f', 'F':
                                    System.exit(0);
                                    break;
                                default:
                                    System.out.println("Invalid option. Try again!");
                                    break;
                            }
                        }

                    } else {
                        System.out.println("Please input a positive integer value for seats.");
                    }
                }
            } else {
                System.out.println("Please input a positive integer value for rows.");
            }
        }
    }

    static void bookSeat(String[][] hallA, String[][] hallB, String[][] hallC) {
        System.out.println("Select hall you want to book (A/B/C): ");
        char hallOption = input.next().charAt(0);

        String[] seatNamesToBook;
        String studentId = "";
        String hallName = "";

        switch (hallOption) {
            case 'a', 'A':
                hallName = "Hall A";
                seatNamesToBook = inputSeats("Hall A", hallA);
                break;
            case 'b', 'B':
                hallName = "Hall B";
                seatNamesToBook = inputSeats("Hall B", hallB);
                break;
            case 'c', 'C':
                hallName = "Hall C";
                seatNamesToBook = inputSeats("Hall C", hallC);
                break;
            default:
                System.out.println("Invalid hall option. Try again!");
                return;
        }

        System.out.println("Enter student id: ");
        input.nextLine(); // Consume newline character
        studentId = input.nextLine();

        bookSeatsInHall(hallName, hallA, hallB, hallC, seatNamesToBook, studentId);
    }

    static String[] inputSeats(String hallName, String[][] hall) {
        displayHall(hallName, hall);
        System.out.println("Input seat(s)'s name you want to book (comma-separated): ");
        return input.next().split(",");
    }

    static void bookSeatsInHall(String hallName, String[][] hallA, String[][] hallB, String[][] hallC,
                                String[] seatNamesToBook, String studentId) {
        String[][] selectedHall = null;

        switch (hallName) {
            case "Hall A":
                selectedHall = hallA;
                break;
            case "Hall B":
                selectedHall = hallB;
                break;
            case "Hall C":
                selectedHall = hallC;
                break;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();

        StringBuilder bookedSeats = new StringBuilder();
        for (String seatName : seatNamesToBook) {
            boolean seatBooked = updateSeatStatus(selectedHall, seatName.trim());
            if (seatBooked) {
                System.out.println("Seat " + seatName.trim() + " in " + hallName + " booked successfully.");

                // Record booking history
                if (bookedSeats.length() > 0) {
                    bookedSeats.append(", ");
                }
                bookedSeats.append(seatName.trim());
            } else {
                System.out.println("Seat " + seatName.trim() + " in " + hallName + " not found or already booked.");
            }
        }

        if (bookedSeats.length() > 0) {
            seatHistory[historyIndex] = bookedSeats.toString();
            hallHistory[historyIndex] = hallName;
            studentIdHistory[historyIndex] = studentId;
            createdAtHistory[historyIndex] = now.format(formatter);

            historyIndex = (historyIndex + 1) % MAX_BOOKINGS; // Circular array to overwrite old history
        }

        input.nextLine(); // Consume the newline character after reading seat names
    }

    static int getFirstAvailableIndex(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                return i;
            }
        }
        return -1; // No available index found
    }

    static void displayHalls(String[][] hallA, String[][] hallB, String[][] hallC) {
        displayHall("Hall A", hallA);
        displayHall("Hall B", hallB);
        displayHall("Hall C", hallC);
    }

    static void displayHall(String hallName, String[][] hall) {
        System.out.println("+++++++++++++++++++++++++++ " + hallName + " ++++++++++++++++++++++++++++++++");
        for (int i = 0; i < hall.length; i++) {
            for (int j = 0; j < hall[0].length; j++) {
                if (hall[i][j] != null && hall[i][j].endsWith("BO")) {
                    System.out.print("|" + hall[i][j] + "|    ");
                } else {
                    hall[i][j] = String.valueOf((char) ('A' + i) + "-" + (j + 1)) + "::" + AVAILABLE;
                    System.out.print("|" + hall[i][j] + "|    ");
                }
            }
            System.out.println();
        }
    }

    static boolean updateSeatStatus(String[][] hall, String seatName) {
        for (int i = 0; i < hall.length; i++) {
            for (int j = 0; j < hall[0].length; j++) {
                if (hall[i][j].startsWith(seatName) && hall[i][j].endsWith("AV")) {
                    hall[i][j] = hall[i][j].replace("AV", "BO");
                    return true; // Seat found and updated
                }
            }
        }
        return false; // Seat not found or already booked
    }
    static void rebootShowtime(String[][] hallA, String[][] hallB, String[][] hallC) {
        clearHall(hallA);
        clearHall(hallB);
        clearHall(hallC);

        // Clear booking history
        Arrays.fill(seatHistory, null);
        Arrays.fill(hallHistory, null);
        Arrays.fill(studentIdHistory, null);
        Arrays.fill(createdAtHistory, null);

        System.out.println("Showtime rebooted successfully.");
    }

    static void clearHall(String[][] hall) {
        for (int i = 0; i < hall.length; i++) {
            for (int j = 0; j < hall[0].length; j++) {
                hall[i][j] = null;
            }
        }
    }

    static boolean canReboot() {
        // Check if a day has passed since the last reboot
        LocalDate currentDate = LocalDate.now();

        return currentDate.isAfter(lastRebootDateA.plusDays(MAX_REBOOT_INTERVAL_DAYS))
                && currentDate.isAfter(lastRebootDateB.plusDays(MAX_REBOOT_INTERVAL_DAYS))
                && currentDate.isAfter(lastRebootDateC.plusDays(MAX_REBOOT_INTERVAL_DAYS));
    }

    static void menu() {
        System.out.println("<A> Booking");
        System.out.println("<B> Hall");
        System.out.println("<C> Showtime");
        System.out.println("<D> Reboot Showtime");
        System.out.println("<E> History ");
        System.out.println("<F> Exit");
    }

    static void showtime() {
        System.out.println("# Showtime Information");
        System.out.println("# A) Morning (10:00AM-12:30PM)");
        System.out.println("# B) Afternoon (3:00PM-5:30PM)");
        System.out.println("# C) Night (7:00PM-9:30PM)");
    }

    static void displayHistory() {
        for (int i = 0; i < MAX_BOOKINGS; i++) {
            if (seatHistory[i] != null) {
                String formattedString = String.format("""
                            #NO: %d
                            #Seat: %s
                            #Hall              #STU.ID              #Created-At
                            %-20s%-20s%-20s
                            """,
                        i + 1, "[" + seatHistory[i] + "]", "" + hallHistory[i],
                        studentIdHistory[i], createdAtHistory[i]);
                System.out.println(formattedString);
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }else {
                System.out.println("there is no history \n");
                break;
            }
        }
    }

}

