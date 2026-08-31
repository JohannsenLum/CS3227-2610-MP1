package constella.application;

import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Creates a fictional four-year NUS journal for a useful first launch. */
public final class DemoJournalSeeder {
    private DemoJournalSeeder() {
    }

    public static JournalSnapshot create() {
        Map<String, Memory> memories = new LinkedHashMap<>();
        add(memories, "orientation", "First evening at UTown", "2022-08-05",
                "Met my orientation group over supper and watched the campus lights come on.", Mood.EXCITED, 5,
                List.of("nus", "orientation", "utown"), List.of("Orientation group"), "University Town");
        add(memories, "cs1010s", "My first CS1010S recursion clicked", "2022-09-19",
                "After hours in the programming lab, the recursive solution finally made sense.", Mood.JOYFUL, 4,
                List.of("nus", "cs1010s", "coding"), List.of("Lab partner"), "COM1");
        add(memories, "midautumn", "Mid-Autumn picnic at the field", "2022-09-10",
                "Shared mooncakes and stories with new friends under paper lanterns.", Mood.PEACEFUL, 3,
                List.of("nus", "friends", "festival"), List.of("Hall friends"), "UTown Green");
        add(memories, "japan", "Winter break in Kyoto", "2022-12-18",
                "A quiet morning walking through temple gardens after the semester ended.", Mood.PEACEFUL, 5,
                List.of("holiday", "japan", "travel"), List.of("Family"), "Kyoto, Japan");
        add(memories, "rain-walk", "A quiet walk home after the rain", "2022-10-27",
                "Walked alone from the library while the wet campus reflected every light.", Mood.PEACEFUL, 2,
                List.of("nus", "quiet", "campus"), List.of(), "Kent Ridge");

        add(memories, "cs2030s", "Survived the CS2030S practical", "2023-03-24",
                "Generics and functional programming finally came together during the practical.", Mood.JOYFUL, 4,
                List.of("nus", "cs2030s", "coding"), List.of("Course friends"), "COM3");
        add(memories, "rag", "Rag rehearsal after sunset", "2023-07-29",
                "Weeks of rehearsals turned into one energetic full run with the faculty team.", Mood.EXCITED, 4,
                List.of("nus", "rag", "friends"), List.of("Rag team"), "University Sports Centre");
        add(memories, "cs2040s", "CS2040S graph algorithms study night", "2023-10-12",
                "Worked through shortest paths on a whiteboard until the last campus bus.", Mood.NOSTALGIC, 3,
                List.of("nus", "cs2040s", "study"), List.of("Study group"), "Central Library");
        add(memories, "bali", "Reading week escape to Bali", "2023-11-15",
                "Took a short breather by the sea before returning for finals.", Mood.PEACEFUL, 4,
                List.of("holiday", "bali", "friends"), List.of("NUS friends"), "Bali, Indonesia");
        add(memories, "volunteering", "Saturday volunteering at Clementi", "2023-09-02",
                "Packed groceries with friends and listened to residents share stories about the neighbourhood.",
                Mood.JOYFUL, 3, List.of("community", "friends", "singapore"), List.of("Volunteer team"), "Clementi");

        add(memories, "cs2103t", "CS2103T product demo", "2024-04-12",
                "Our team presented the final desktop application after a semester of reviews and refactoring.",
                Mood.JOYFUL, 5, List.of("nus", "cs2103t", "software-engineering"), List.of("Project team"), "COM1");
        add(memories, "hackathon", "Overnight student hackathon", "2024-09-21",
                "Built a tiny campus tool through the night and presented it over breakfast.", Mood.EXCITED, 4,
                List.of("nus", "coding", "hackathon"), List.of("Hackathon team"), "i3 Building");
        add(memories, "cny-home", "Chinese New Year afternoon at home", "2024-02-11",
                "A slow afternoon of snacks, old photographs, and conversations with relatives.", Mood.NOSTALGIC, 3,
                List.of("holiday", "family", "home"), List.of("Family"), "Singapore");
        add(memories, "exchange-arrival", "Arrived for exchange in Stockholm", "2025-01-13",
                "Dragged two suitcases through the snow and found my new student residence.", Mood.EXCITED, 5,
                List.of("exchange", "sweden", "travel"), List.of("Exchange students"), "Stockholm, Sweden");
        add(memories, "lapland", "Northern lights in Lapland", "2025-02-22",
                "Waited in the cold until green ribbons appeared across the sky.", Mood.JOYFUL, 5,
                List.of("exchange", "finland", "holiday"), List.of("Exchange friends"), "Rovaniemi, Finland");
        add(memories, "europe", "Spring break rail trip", "2025-04-07",
                "Took the train through Copenhagen, Berlin, and Prague with only a backpack.", Mood.NOSTALGIC, 5,
                List.of("exchange", "europe", "travel"), List.of("Exchange friends"), "Prague, Czechia");
        add(memories, "fika", "Weekly fika with exchange classmates", "2025-03-06",
                "Our Thursday coffee break became the easiest place to compare classes and weekend plans.",
                Mood.PEACEFUL, 3, List.of("exchange", "sweden", "friends"), List.of("Exchange classmates"),
                "Stockholm, Sweden");
        add(memories, "museum", "Solo afternoon at the photography museum", "2025-05-17",
                "Spent hours moving quietly between exhibitions before taking the ferry home.", Mood.PEACEFUL, 2,
                List.of("exchange", "art", "quiet"), List.of(), "Stockholm, Sweden");

        add(memories, "return", "Back at NUS after exchange", "2025-08-11",
                "Campus felt familiar and different at the same time after six months away.", Mood.NOSTALGIC, 4,
                List.of("nus", "exchange", "homecoming"), List.of("Course friends"), "Kent Ridge");
        add(memories, "cs3230", "Finished the CS3230 algorithms project", "2026-03-30",
                "Our experiments finally matched the complexity analysis after several false starts.", Mood.JOYFUL, 4,
                List.of("nus", "cs3230", "algorithms"), List.of("Project partner"), "COM4");
        add(memories, "summer-trip", "Summer road trip around Tasmania", "2026-06-18",
                "Drove past misty mountains, quiet beaches, and more wallabies than expected.", Mood.PEACEFUL, 5,
                List.of("holiday", "australia", "travel"), List.of("University friends"), "Tasmania, Australia");
        add(memories, "year4", "Beginning Year 4 with CS3227", "2026-08-17",
                "Started building Constella while thinking about four years of memories worth preserving.",
                Mood.EXCITED, 5, List.of("nus", "cs3227", "year4"), List.of("Classmates"), "School of Computing");
        add(memories, "internship", "First production release during internship", "2026-05-08",
                "Watched a feature reach real users after reviews, testing, and a careful rollout.", Mood.JOYFUL, 4,
                List.of("internship", "software-engineering", "growth"), List.of("Engineering team"), "Singapore");
        add(memories, "final-supper", "Late supper after our final project meeting", "2026-08-22",
                "Stayed at supper longer than planned, talking about graduation and what comes next.",
                Mood.NOSTALGIC, 4, List.of("nus", "friends", "year4"), List.of("Project friends"), "UTown");

        List<Constellation> constellations = List.of(
                constellation("nus-journey", "My NUS Journey", "Four years around Kent Ridge and UTown",
                        memories, "orientation", "cs1010s", "midautumn", "cs2030s", "rag", "cs2040s",
                        "cs2103t", "hackathon", "return", "cs3230", "year4", "final-supper"),
                constellation("academic", "Academic Milestones", "Modules and projects that shaped the degree",
                        memories, "cs1010s", "cs2030s", "cs2040s", "cs2103t", "hackathon", "cs3230",
                        "internship", "year4"),
                constellation("exchange", "Stockholm Exchange", "A semester studying and travelling in Europe",
                        memories, "exchange-arrival", "lapland", "fika", "europe", "return"),
                constellation("travels", "Trips & Holidays", "Breaks, exchanges, and journeys beyond campus",
                        memories, "japan", "bali", "exchange-arrival", "lapland", "europe", "summer-trip"),
                constellation("community", "Friends & Community", "The people and shared moments around university",
                        memories, "orientation", "midautumn", "rag", "volunteering", "bali", "cs2103t",
                        "hackathon", "summer-trip", "final-supper"));

        JournalService service = new JournalService(new JournalSnapshot(
                new ArrayList<>(memories.values()), constellations, Map.of()));
        return service.snapshot();
    }

    private static void add(Map<String, Memory> memories, String key, String title, String date, String description,
            Mood mood, int importance, List<String> tags, List<String> people, String location) {
        memories.put(key, new Memory(id("memory:" + key), title, LocalDate.parse(date), description,
                mood, importance, tags, people, location));
    }

    private static Constellation constellation(String key, String name, String description,
            Map<String, Memory> memories, String... memberKeys) {
        Set<UUID> memberIds = java.util.Arrays.stream(memberKeys)
                .map(memories::get)
                .map(Memory::id)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        return new Constellation(id("constellation:" + key), name, description, memberIds);
    }

    private static UUID id(String value) {
        return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8));
    }
}
