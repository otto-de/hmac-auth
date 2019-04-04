hmac-auth
=========

HMAC authentication for RESTful web applications

# Release 2.3.5

* Use explicit utf-8 encoding for signature calculation

# Release 2.3.4

* Add jaxb dependency for Java 11+ compatibility

# Release 2.3.3

* Add PATCH functionality

# Release 2.3.2

* Added secure HTTPs destination option to hmac auth proxy application 

# Release: 2.3.1

* Fixed bug DateTimeParseException when validating hmac requests with version 2.3.0 #23
* Throw explicit exception if no secretKey is provided #25

# Release 2.3.0

Remove dependency of joda-time library and use java.time package instead.

By that Java8 is required to use this library.

# Release 2.2.0

Add property based user repository (PropertyUserRepository). You can inject a json file with the following structure:

    [
      {
        "user": "user1",
        "password": "password1",
        "roles": [
          "role1"
        ]
      },
      {
        "user": "user2",
        "password": "password2",
        "roles": [
          "role1",
          "role2"
        ]
      },
      {
        "user": "user3",
        "password": "password3",
        "roles": []
      }
    ]

# Release 2.1.0

Separation of hmac-auth-server and spring configuration.

* New module `hmac-auth-server-spring` which holds the configuration of the hmac-auth-server module.
* hmac-auth-server: Extraction of spring dependencies

If you have used the `hmac-auth-server` module, please switch to the `hmac-auth-server-spring` dependency instead. 

# Release 1.2.1

## Maven:

Repository:

```xml
<repositories>
   <repository>
        <id>central</id>
        <url>http://repo1.maven.org/maven2/</url>
   </repository>
</repositories>
```

Dependency:

```xml
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-server</artifactId>
   <version>1.2.1</version>
</dependency>
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-client</artifactId>
   <version>1.2.1</version>
</dependency>
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-proxy</artifactId>
   <version>1.2.1</version>
</dependency>
```

## Gradle:

Repository:

```groovy
repositories {
    mavenCentral()
}
```

Dependency:

```groovy
dependencies {
    compile ("de.otto:hmac-auth-server:1.2.1")
    compile ("de.otto:hmac-auth-client:1.2.1")
    compile ("de.otto:hmac-auth-proxy:1.2.1")
}
```

# Release 1.2.1-SNAPSHOT

## Maven:

Repository:

```xml
<repositories>
   <repository>
        <id>SonatypeSnapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
   </repository>
</repositories>
```

Dependency:

```xml
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-server</artifactId>
   <version>1.2.1-SNAPSHOT</version>
</dependency>
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-client</artifactId>
   <version>1.2.1-SNAPSHOT</version>
</dependency>
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-proxy</artifactId>
   <version>1.2.1-SNAPSHOT</version>
</dependency>
```

## Gradle:

Repository:

```groovy
repositories {
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}
```

Dependency:

```groovy
dependencies {
    compile ("de.otto:hmac-auth-server:1.2.1-SNAPSHOT")
    compile ("de.otto:hmac-auth-client:1.2.1-SNAPSHOT")
    compile ("de.otto:hmac-auth-proxy:1.2.1-SNAPSHOT")
}
```
