import com.intuit.karate.junit5.Karate;

public class KarateTests {

    @Karate.Test
    Karate testClient() {
        return Karate.run("karate/client").relativeTo(getClass());
    }
}
