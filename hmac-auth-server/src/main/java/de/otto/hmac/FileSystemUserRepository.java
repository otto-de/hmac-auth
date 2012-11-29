package de.otto.hmac;

import de.otto.hmac.authentication.UserRepository;
import de.otto.hmac.authorization.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.util.Arrays.asList;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;

@Repository
public class FileSystemUserRepository implements UserRepository, RoleRepository {

    private final ConcurrentMap<String, String> userToKey = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> userToRole = new ConcurrentHashMap<>();
    private Resource resource;

    @Value("${hmac.auth.xml}")
    public void setResource(final String authXml) {
        this.resource = new ClassPathResource(authXml);
    }

    @PostConstruct
    public void postConstruct() throws IOException, ParserConfigurationException, SAXException {
        try (final InputStream inputStream = resource.getInputStream()) {
            final Document document = newInstance().newDocumentBuilder().parse(inputStream);
            loadAuthXml(document, userToKey, userToRole);
        }
    }

    @Override
    public String getKey(final String username) {
        return userToKey.get(username);
    }

    @Override
    public boolean hasRole(final String user, final String role) {
        return getRolesForUser(user).contains(role);
    }

    @Override
    public Set<String> getRolesForUser(final String user) {
        final Set<String> userRoles = userToRole.get(user);
        final Set<String> roles = new HashSet<>();
        if (userRoles != null) {
            roles.addAll(userRoles);
        }
        roles.add("everybody");
        return roles;
    }

    private void loadAuthXml(final Document document,
                             final ConcurrentMap<String, String> userToKey,
                             final ConcurrentMap<String, Set<String>> userToRole) {
        NodeList ndList = document.getElementsByTagName("user");
        for (int i = 0; i < ndList.getLength(); i++) {
            Node item = ndList.item(i);
            NamedNodeMap attributes = item.getAttributes();

            final String username = attributes.getNamedItem("name").getTextContent();
            userToKey.put(
                    username,
                    attributes.getNamedItem("key").getTextContent()
            );
            if (attributes.getNamedItem("roles") != null) {
                final String roles = attributes.getNamedItem("roles").getTextContent();
                userToRole.put(
                        username,
                        new ConcurrentSkipListSet<>(asList(roles.split(",")))
                );
            }
        }
    }
}
