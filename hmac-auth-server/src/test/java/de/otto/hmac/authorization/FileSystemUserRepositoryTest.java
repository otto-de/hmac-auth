package de.otto.hmac.authorization;

import de.otto.hmac.FileSystemUserRepository;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

@Test
public class FileSystemUserRepositoryTest {

    @Test
    public void shouldKnowUsersPasswords() throws Exception {
        FileSystemUserRepository userRepository = userRepository();
        assertThat(userRepository.getKey("maxmustermann"), is("someSecretKey"));
    }

    @Test
    public void shouldGiveNullPasswordForUnknownUser() throws Exception {
        FileSystemUserRepository userRepository = userRepository();
        assertThat(userRepository.getKey("whoeverThisMayBe"), is(nullValue()));
    }

    @Test
    public void shouldKnowUserRoles() throws Exception {
        FileSystemUserRepository userRepository = userRepository();
        assertThat(userRepository.getRolesForUser("maxmustermann"), hasItems("admin", "backoffice"));
    }

    @Test
    public void unknownUserShouldHaveRoleEverybody() throws Exception {
        FileSystemUserRepository userRepository = userRepository();
        assertThat(userRepository.getRolesForUser("foo"), hasItem("everybody"));
    }

    @Test
    public void nullUserShouldHaveRoleEverybody() throws Exception {
        FileSystemUserRepository userRepository = userRepository();
        assertThat(userRepository.getRolesForUser(null), hasItem("everybody"));
    }

    private FileSystemUserRepository userRepository() throws IOException, SAXException, ParserConfigurationException {
        final FileSystemUserRepository repository = new FileSystemUserRepository("/hmac/auth.xml");
        return repository;
    }
}
