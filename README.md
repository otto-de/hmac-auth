hmac-auth
=========

HMAC authentication for RESTful web applications

* Current release: 1.1.0 (in Maven Central repo)
* Current snapshot: 1.1.0-SNAPSHOT (in Sonatype snapshot repo)

# Release 1.1.0

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
   <version>1.1.0</version>
</dependency>
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-client</artifactId>
   <version>1.1.0</version>
</dependency>
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-proxy</artifactId>
   <version>1.1.0</version>
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
    compile ("de.otto:hmac-auth-server:1.1.0")
    compile ("de.otto:hmac-auth-client:1.1.0")
    compile ("de.otto:hmac-auth-proxy:1.1.0")
}
```

# Release 1.2.0-SNAPSHOT

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
   <version>1.2.0-SNAPSHOT</version>
</dependency>
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-client</artifactId>
   <version>1.2.0-SNAPSHOT</version>
</dependency>
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-proxy</artifactId>
   <version>1.2.0-SNAPSHOT</version>
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
    compile ("de.otto:hmac-auth-server:1.2.0-SNAPSHOT")
    compile ("de.otto:hmac-auth-client:1.2.0-SNAPSHOT")
    compile ("de.otto:hmac-auth-proxy:1.2.0-SNAPSHOT")
}
```
