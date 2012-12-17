hmac-auth
=========

HMAC authentication for RESTful web applications


*WORK IN PROGRESS - UNSTABLE!*

# Releases

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
   <version>0.2.1-SNAPSHOT</version>
</dependency>
<dependency>
   <groupId>de.otto</groupId>
   <artifactId>hmac-auth-client</artifactId>
   <version>0.2.1-SNAPSHOT</version>
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
    compile ("de.otto:hmac-auth-server:0.2.1-SNAPSHOT")
}
dependencies {
    compile ("de.otto:hmac-auth-client:0.2.1-SNAPSHOT")
}
```
