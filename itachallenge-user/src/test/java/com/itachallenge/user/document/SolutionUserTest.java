package com.itachallenge.user.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SolutionUserTest {

    @Test
    public void testConstructor() {
        UUID uuidSolutionUser = UUID.fromString("04d0c470-1661-4a9f-be13-9cbb914732a1");
        UUID uuidUser = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        UUID uuidChallenge = UUID.fromString("866853b8-ae7d-4daf-8c82-5e6f653e0fc1");
        UUID uuidLanguage = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        String solutionText = "Sample solution text";

        SolutionUser solutionUserTest = SolutionUser.builder()
                .uuid_solutionUser(uuidSolutionUser)
                .uuid_user(uuidUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .solution_Text(solutionText)
                .build();

        assertNotNull(solutionUserTest);
        assertEquals(uuidSolutionUser, solutionUserTest.getUuid_solutionUser());
        assertEquals(uuidChallenge, solutionUserTest.getUuid_challenge());
        assertEquals(uuidLanguage, solutionUserTest.getUuid_language());
        assertEquals(uuidUser, solutionUserTest.getUuid_user());
        assertEquals(solutionText, solutionUserTest.getSolution_Text());
    }

    @Test
    public void testSettersAndNoArgsConstructor() {
        UUID uuidSolutionUser = UUID.fromString("04d0c470-1661-4a9f-be13-9cbb914732a1");
        UUID uuidUser = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        UUID uuidChallenge = UUID.fromString("866853b8-ae7d-4daf-8c82-5e6f653e0fc1");
        UUID uuidLanguage = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        String solutionText = "Sample solution text";

        SolutionUser solutionUserTest = new SolutionUser();
        solutionUserTest.setUuid_solutionUser(uuidSolutionUser);
        solutionUserTest.setUuid_challenge(uuidChallenge);
        solutionUserTest.setUuid_language(uuidLanguage);
        solutionUserTest.setUuid_user(uuidUser);
        solutionUserTest.setSolution_Text(solutionText);

        assertNotNull(solutionUserTest);
        assertEquals(uuidSolutionUser, solutionUserTest.getUuid_solutionUser());
        assertEquals(uuidChallenge, solutionUserTest.getUuid_challenge());
        assertEquals(uuidLanguage, solutionUserTest.getUuid_language());
        assertEquals(uuidUser, solutionUserTest.getUuid_user());
        assertEquals(solutionText, solutionUserTest.getSolution_Text());
    }

    @Test
    public void testEquality() {
        UUID uuidSolutionUser = UUID.fromString("04d0c470-1661-4a9f-be13-9cbb914732a1");
        UUID uuidUser = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        UUID uuidChallenge = UUID.fromString("866853b8-ae7d-4daf-8c82-5e6f653e0fc1");
        UUID uuidLanguage = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        String solutionText = "Sample solution text";

        SolutionUser solutionUser1 = SolutionUser.builder()
                .uuid_solutionUser(uuidSolutionUser)
                .uuid_user(uuidUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .solution_Text(solutionText)
                .build();

        SolutionUser solutionUser2 = SolutionUser.builder()
                .uuid_solutionUser(uuidSolutionUser)
                .uuid_user(uuidUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .solution_Text(solutionText)
                .build();

        assertEquals(solutionUser1, solutionUser2);
        assertEquals(solutionUser1.hashCode(), solutionUser2.hashCode());
    }

    @Test
    public void testInequality() {
        UUID uuidSolutionUser1 = UUID.fromString("04d0c470-1661-4a9f-be13-9cbb914732a1");
        UUID uuidSolutionUser2 = UUID.fromString("12345678-1234-1234-1234-1234567890AB");
        UUID uuidUser = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        UUID uuidChallenge = UUID.fromString("866853b8-ae7d-4daf-8c82-5e6f653e0fc1");
        UUID uuidLanguage = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        String solutionText = "Sample solution text";

        SolutionUser solutionUser1 = SolutionUser.builder()
                .uuid_solutionUser(uuidSolutionUser1)
                .uuid_user(uuidUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .solution_Text(solutionText)
                .build();

        SolutionUser solutionUser2 = SolutionUser.builder()
                .uuid_solutionUser(uuidSolutionUser2)
                .uuid_user(uuidUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .solution_Text(solutionText)
                .build();

        assertNotEquals(solutionUser1, solutionUser2);
        assertNotEquals(solutionUser1.hashCode(), solutionUser2.hashCode());
    }

    @Test
    public void testConstructorWithNulls() {
        UUID uuidSolutionUser = UUID.fromString("04d0c470-1661-4a9f-be13-9cbb914732a1");
        UUID uuidUser = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        UUID uuidChallenge = null; // Establece un valor nulo
        UUID uuidLanguage = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        String solutionText = null; // Establece un valor nulo

        SolutionUser solutionUserTest = SolutionUser.builder()
                .uuid_solutionUser(uuidSolutionUser)
                .uuid_user(uuidUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .solution_Text(solutionText)
                .build();

        assertNotNull(solutionUserTest);
        assertEquals(uuidSolutionUser, solutionUserTest.getUuid_solutionUser());
        assertEquals(uuidUser, solutionUserTest.getUuid_user());
        assertNull(solutionUserTest.getUuid_challenge());
        assertNull(solutionUserTest.getSolution_Text());
    }

}