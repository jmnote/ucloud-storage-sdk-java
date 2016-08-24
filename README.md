ucloud storage SDK for Java
===========================

# maven
Add the JitPack repository to your build file (pom.xml)
```xml
		<dependency>
			<groupId>com.github.jmnote</groupId>
			<artifactId>ucloud-storage-sdk</artifactId>
			<version>0.0.2</version>
		</dependency>
```
Add the dependency
```xml
	<repositories>
		<repository>
			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		</repository>
	</repositories>
```

# gradle
Add it in your root build.gradle at the end of repositories
```gradle
	allprojects {
		repositories {
			maven { url "https://jitpack.io" }
		}
	}
```
Add the dependency
```gradle
	dependencies {
		compile 'com.github.jmnote:ucloud-storage-sdk:0.0.2'
	}
```
