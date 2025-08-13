package id.sahabat.yayasan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class YayasanSahabatQuranApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void main() {
        YayasanSahabatQuranApplication.main(new String[]{});
    }
}
