package fametro.edu.br.evently;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootTest
class EventlyApplicationTests {

    @Test
    void generatePasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        List<String> senhasPuras = List.of("miguel123", "euton123", "gabriel123");

        senhasPuras.stream()
                .map(encoder::encode)
                .forEach(System.out::println);
    }


}
