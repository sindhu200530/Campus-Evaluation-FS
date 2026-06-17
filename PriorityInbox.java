import java.time.LocalDateTime;
import java.util.*;

class Notification {
    int id;
    String type;
    String message;
    LocalDateTime createdAt;
    boolean isRead;

    public Notification(int id, String type, String message,
                        LocalDateTime createdAt, boolean isRead) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public int getPriorityScore() {
        switch (type) {
            case "Placement":
                return 3;
            case "Result":
                return 2;
            default:
                return 1;
        }
    }

    @Override
    public String toString() {
        return "ID=" + id +
                ", Type=" + type +
                ", Message=" + message +
                ", CreatedAt=" + createdAt;
    }
}

public class PriorityInbox {
    public static void main(String[] args) {

        List<Notification> notifications = Arrays.asList(
                new Notification(1, "Placement", "TCS Drive",
                        LocalDateTime.now().minusHours(1), false),

                new Notification(2, "Result", "Semester Results Published",
                        LocalDateTime.now().minusDays(1), false),

                new Notification(3, "Event", "Tech Symposium",
                        LocalDateTime.now().minusDays(2), false),

                new Notification(4, "Placement", "Infosys Hiring",
                        LocalDateTime.now().minusMinutes(30), false),

                new Notification(5, "Placement", "Wipro Recruitment",
                        LocalDateTime.now().minusHours(5), false),

                new Notification(6, "Event", "Workshop Registration",
                        LocalDateTime.now().minusDays(3), false),

                new Notification(7, "Result", "Internal Marks Released",
                        LocalDateTime.now().minusHours(10), false),

                new Notification(8, "Placement", "Accenture Hiring",
                        LocalDateTime.now().minusMinutes(10), false),

                new Notification(9, "Placement", "Zoho Drive",
                        LocalDateTime.now().minusMinutes(15), false),

                new Notification(10, "Event", "Hackathon",
                        LocalDateTime.now().minusDays(4), false),

                new Notification(11, "Result", "Revaluation Results",
                        LocalDateTime.now().minusHours(3), false),

                new Notification(12, "Placement", "Amazon Hiring",
                        LocalDateTime.now().minusMinutes(5), false)
        );

        notifications.stream()
                .filter(n -> !n.isRead)
                .sorted((a, b) -> {
                    int priorityCompare =
                            Integer.compare(b.getPriorityScore(), a.getPriorityScore());

                    if (priorityCompare != 0) {
                        return priorityCompare;
                    }

                    return b.createdAt.compareTo(a.createdAt);
                })
                .limit(10)
                .forEach(System.out::println);
    }
}