package lu.cnw.tcp_simulator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class NamePseudonymizer {

    // Example name pools — replace with bigger lists for variety
    private static final List<String> FEMALE_FIRST_NAMES = Arrays.asList(
            "Emma", "Olivia", "Ava", "Sophia", "Isabella", "Mia", "Amelia", "Harper", "Evelyn", "Abigail",
            "Ella", "Elizabeth", "Camila", "Luna", "Sofia", "Avery", "Mila", "Aria", "Scarlett", "Penelope",
            "Layla", "Chloe", "Victoria", "Madison", "Eleanor", "Grace", "Nora", "Riley", "Zoey", "Hannah",
            "Hazel", "Lily", "Ellie", "Violet", "Aurora", "Savannah", "Audrey", "Brooklyn", "Bella", "Claire",
            "Skylar", "Lucy", "Paisley", "Everly", "Anna", "Caroline", "Nova", "Genesis", "Emilia", "Kennedy"
    );

    private static final List<String> MALE_FIRST_NAMES = Arrays.asList(
            "Liam", "Noah", "Oliver", "Elijah", "James", "William", "Benjamin", "Lucas", "Henry", "Alexander",
            "Mason", "Michael", "Ethan", "Daniel", "Jacob", "Logan", "Jackson", "Levi", "Sebastian", "Mateo",
            "Jack", "Owen", "Theodore", "Aiden", "Samuel", "Joseph", "John", "David", "Wyatt", "Matthew",
            "Luke", "Asher", "Carter", "Julian", "Grayson", "Leo", "Jayden", "Gabriel", "Isaac", "Lincoln",
            "Anthony", "Hudson", "Dylan", "Ezra", "Thomas", "Charles", "Christopher", "Jaxon", "Maverick", "Josiah"
    );

    private static final List<String> LAST_NAMES = Arrays.asList(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
            "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson",
            "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
            "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell", "Carter", "Roberts"
    );

    private final String secretKey;

    public NamePseudonymizer(String secretKey) {
        this.secretKey = secretKey;
    }

    private static byte[] hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        NamePseudonymizer pseudonymizer = new NamePseudonymizer("super-secret-key");

        System.out.println(pseudonymizer.pseudonymize("John Doe", true));
        System.out.println(pseudonymizer.pseudonymize("Jane Doe", false));
        System.out.println(pseudonymizer.pseudonymize("John. Doe", true));
        System.out.println(pseudonymizer.pseudonymize("John Doe", false)); // same as first
    }

    public Anonymised pseudonymize(String realName, boolean male) {
        try {
            byte[] hash = hmacSha256(realName, secretKey);

            // Use first few bytes to choose names
            int firstIndex = Byte.toUnsignedInt(hash[0]) % (male ? MALE_FIRST_NAMES : FEMALE_FIRST_NAMES).size();
            int lastIndex = Byte.toUnsignedInt(hash[1]) % LAST_NAMES.size();
            int middleIndex = Byte.toUnsignedInt(hash[2]) % LAST_NAMES.size();
            int year = Byte.toUnsignedInt(hash[3]) % 20 + 2005;
            int month = Byte.toUnsignedInt(hash[4]) % 12;
            int day = Byte.toUnsignedInt(hash[5]) % 28;
            int clubIndex = Byte.toUnsignedInt(hash[6]) % LAST_NAMES.size();
            var name = (male ? MALE_FIRST_NAMES : FEMALE_FIRST_NAMES).get(firstIndex) + " " + LAST_NAMES.get(middleIndex).charAt(0) + ". " + LAST_NAMES.get(lastIndex);
            var birth = String.format("%04d-%02d-%02d", year, month, day);
            var club = "S.C. " + LAST_NAMES.get(clubIndex);
            return new Anonymised(realName.isBlank() ? "" : name, birth, club);
        } catch (Exception e) {
            throw new RuntimeException("Error pseudonymizing name", e);
        }
    }

    public record Anonymised(String name, String birthdate, String club) {
    }
}
