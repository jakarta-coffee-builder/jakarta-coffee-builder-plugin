package com.apuntesdejava.jakartacoffeebuilder.util;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MavenProjectUtilTest {

    @Mock
    private MavenProject mockProject;

    @Nested
    @DisplayName("getProjectPackage")
    class GetProjectPackage {

        @Test
        @DisplayName("should derive package from groupId and artifactId")
        void derivesPackageFromCoordinates() {
            when(mockProject.getGroupId()).thenReturn("com.example");
            when(mockProject.getArtifactId()).thenReturn("my-app");

            String result = MavenProjectUtil.getProjectPackage(mockProject);

            assertEquals("com.example.my.app", result);
        }

        @Test
        @DisplayName("should replace non-alphanumeric characters with dots")
        void replacesSpecialChars() {
            when(mockProject.getGroupId()).thenReturn("com.apuntesdejava");
            when(mockProject.getArtifactId()).thenReturn("jakarta-coffee-builder");

            String result = MavenProjectUtil.getProjectPackage(mockProject);

            assertEquals("com.apuntesdejava.jakarta.coffee.builder", result);
        }
    }

    @Nested
    @DisplayName("Layer package methods")
    class LayerPackages {

        @BeforeEach
        void setUp() {
            when(mockProject.getGroupId()).thenReturn("com.example");
            when(mockProject.getArtifactId()).thenReturn("myapp");
        }

        @Test
        @DisplayName("getEntityPackage should append infrastructure.entity")
        void entityPackage() {
            assertEquals("com.example.myapp.infrastructure.entity",
                MavenProjectUtil.getEntityPackage(mockProject));
        }

        @Test
        @DisplayName("getModelPackage should append domain.model")
        void modelPackage() {
            assertEquals("com.example.myapp.domain.model",
                MavenProjectUtil.getModelPackage(mockProject));
        }

        @Test
        @DisplayName("getMapperPackage should append infrastructure.mapper")
        void mapperPackage() {
            assertEquals("com.example.myapp.infrastructure.mapper",
                MavenProjectUtil.getMapperPackage(mockProject));
        }

        @Test
        @DisplayName("getServicePackage should append infrastructure.domain")
        void servicePackage() {
            assertEquals("com.example.myapp.infrastructure.domain",
                MavenProjectUtil.getServicePackage(mockProject));
        }

        @Test
        @DisplayName("getRepositoryPackage should append infrastructure.repository")
        void repositoryPackage() {
            assertEquals("com.example.myapp.infrastructure.repository",
                MavenProjectUtil.getRepositoryPackage(mockProject));
        }

        @Test
        @DisplayName("getProviderPackage should append infrastructure.provider")
        void providerPackage() {
            assertEquals("com.example.myapp.infrastructure.provider",
                MavenProjectUtil.getProviderPackage(mockProject));
        }

        @Test
        @DisplayName("getEnumsPackage should append enums")
        void enumsPackage() {
            assertEquals("com.example.myapp.enums",
                MavenProjectUtil.getEnumsPackage(mockProject));
        }

        @Test
        @DisplayName("getFacesPackage should append app.faces")
        void facesPackage() {
            assertEquals("com.example.myapp.app.faces",
                MavenProjectUtil.getFacesPackage(mockProject));
        }

        @Test
        @DisplayName("getApiResourcesPackage should append app.resources")
        void apiResourcesPackage() {
            assertEquals("com.example.myapp.app.resources",
                MavenProjectUtil.getApiResourcesPackage(mockProject));
        }

        @Test
        @DisplayName("getDomainModelPackage should append domain.model")
        void domainModelPackage() {
            assertEquals("com.example.myapp.domain.model",
                MavenProjectUtil.getDomainModelPackage(mockProject));
        }

        @Test
        @DisplayName("getModelRepositoryPackage should append domain.repository")
        void modelRepositoryPackage() {
            assertEquals("com.example.myapp.domain.repository",
                MavenProjectUtil.getModelRepositoryPackage(mockProject));
        }
    }

    @Nested
    @DisplayName("getProfile")
    class GetProfile {

        @Test
        @DisplayName("should return existing profile when found")
        void returnsExistingProfile() {
            var model = new Model();
            var existingProfile = new Profile();
            existingProfile.setId("test-profile");
            model.addProfile(existingProfile);
            when(mockProject.getOriginalModel()).thenReturn(model);

            Profile result = MavenProjectUtil.getProfile(mockProject, "test-profile");

            assertSame(existingProfile, result);
        }

        @Test
        @DisplayName("should create and return new profile when not found")
        void createsNewProfile() {
            var model = new Model();
            when(mockProject.getOriginalModel()).thenReturn(model);

            Profile result = MavenProjectUtil.getProfile(mockProject, "new-profile");

            assertEquals("new-profile", result.getId());
            assertEquals(1, model.getProfiles().size());
        }
    }

    @Nested
    @DisplayName("getParent")
    class GetParent {

        @Test
        @DisplayName("should return parent path of POM file")
        void returnsParentPath() {
            File pomFile = new File("/projects/my-app/pom.xml");
            when(mockProject.getFile()).thenReturn(pomFile);

            Path result = MavenProjectUtil.getParent(mockProject);

            assertEquals(pomFile.toPath().getParent(), result);
        }
    }

    @Nested
    @DisplayName("getBuild")
    class GetBuild {

        @Test
        @DisplayName("should return existing build from profile")
        void returnsExistingBuild() {
            var model = new Model();
            var profile = new Profile();
            profile.setId("some-profile");
            var build = new Build();
            build.setPlugins(new ArrayList<>());
            profile.setBuild(build);
            model.addProfile(profile);
            when(mockProject.getOriginalModel()).thenReturn(model);

            var result = MavenProjectUtil.getBuild(mockProject, "some-profile");

            assertSame(build, result);
        }

        @Test
        @DisplayName("should create new build when profile has none")
        void createsNewBuild() {
            var model = new Model();
            var profile = new Profile();
            profile.setId("empty-profile");
            model.addProfile(profile);
            when(mockProject.getOriginalModel()).thenReturn(model);

            var result = MavenProjectUtil.getBuild(mockProject, "empty-profile");

            assertNotNull(result);
            assertNotNull(result.getPlugins());
        }
    }
}
