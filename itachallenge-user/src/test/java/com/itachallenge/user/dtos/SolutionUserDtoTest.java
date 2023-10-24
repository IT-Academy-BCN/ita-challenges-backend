package com.itachallenge.user.dtos;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SolutionUserDtoTest {

    @Test
    public void testConstructor() {
        UUID uuidSolutionUser = UUID.fromString("04d0c470-1661-4a9f-be13-9cbb914732a1");
        UUID uuidUser = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        UUID uuidChallenge = UUID.fromString("866853b8-ae7d-4daf-8c82-5e6f653e0fc1");
        UUID uuidLanguage = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        String solutionText = "Sample solution text";

        SolutionUserDto solutionUserDtoTest = SolutionUserDto.builder()
                .uuid_SolutionUser(uuidSolutionUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .uuid_user(uuidUser)
                .solution_Text(solutionText)
                .build();

        assertNotNull(solutionUserDtoTest);
        assertEquals(uuidSolutionUser, solutionUserDtoTest.getUuid_SolutionUser());
        assertEquals(uuidChallenge, solutionUserDtoTest.getUuid_challenge());
        assertEquals(uuidLanguage, solutionUserDtoTest.getUuid_language());
        assertEquals(uuidUser, solutionUserDtoTest.getUuid_user());
        assertEquals(solutionText, solutionUserDtoTest.getSolution_Text());
    }

    @Test
    public void testSettersAndNoArgsConstructor() {
        UUID uuidSolutionUser = UUID.fromString("04d0c470-1661-4a9f-be13-9cbb914732a1");
        UUID uuidUser = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        UUID uuidChallenge = UUID.fromString("866853b8-ae7d-4daf-8c82-5e6f653e0fc1");
        UUID uuidLanguage = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        String solutionText = "Sample solution text";

        SolutionUserDto solutionUserDtoTest = new SolutionUserDto();
        solutionUserDtoTest.setUuid_SolutionUser(uuidSolutionUser);
        solutionUserDtoTest.setUuid_challenge(uuidChallenge);
        solutionUserDtoTest.setUuid_language(uuidLanguage);
        solutionUserDtoTest.setUuid_user(uuidUser);
        solutionUserDtoTest.setSolution_Text(solutionText);

        assertNotNull(solutionUserDtoTest);
        assertEquals(uuidSolutionUser, solutionUserDtoTest.getUuid_SolutionUser());
        assertEquals(uuidChallenge, solutionUserDtoTest.getUuid_challenge());
        assertEquals(uuidLanguage, solutionUserDtoTest.getUuid_language());
        assertEquals(uuidUser, solutionUserDtoTest.getUuid_user());
        assertEquals(solutionText, solutionUserDtoTest.getSolution_Text());
    }

    @Test
    public void testEquality() {
        UUID uuidSolutionUser = UUID.fromString("04d0c470-1661-4a9f-be13-9cbb914732a1");
        UUID uuidUser = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        UUID uuidChallenge = UUID.fromString("866853b8-ae7d-4daf-8c82-5e6f653e0fc1");
        UUID uuidLanguage = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        String solutionText = "Sample solution text";

        SolutionUserDto solutionUserDto1 = SolutionUserDto.builder()
                .uuid_SolutionUser(uuidSolutionUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .uuid_user(uuidUser)
                .solution_Text(solutionText)
                .build();

        SolutionUserDto solutionUserDto2 = SolutionUserDto.builder()
                .uuid_SolutionUser(uuidSolutionUser)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .uuid_user(uuidUser)
                .solution_Text(solutionText)
                .build();

        assertEquals(solutionUserDto1, solutionUserDto2);
        assertEquals(solutionUserDto1.hashCode(), solutionUserDto2.hashCode());
    }

    @Test
    public void testInequality() {
        UUID uuidSolutionUser1 = UUID.fromString("04d0c470-1661-4a9f-be13-9cbb914732a1");
        UUID uuidSolutionUser2 = UUID.fromString("12345678-1234-1234-1234-1234567890AB");
        UUID uuidUser = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        UUID uuidChallenge = UUID.fromString("866853b8-ae7d-4daf-8c82-5e6f653e0fc1");
        UUID uuidLanguage = UUID.fromString("5bee04a9-6f82-4f69-b4a3-76cc280f9d9b");
        String solutionText = "Sample solution text";

        SolutionUserDto solutionUserDto1 = SolutionUserDto.builder()
                .uuid_SolutionUser(uuidSolutionUser1)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .uuid_user(uuidUser)
                .solution_Text(solutionText)
                .build();

        SolutionUserDto solutionUserDto2 = SolutionUserDto.builder()
                .uuid_SolutionUser(uuidSolutionUser2)
                .uuid_challenge(uuidChallenge)
                .uuid_language(uuidLanguage)
                .uuid_user(uuidUser)
                .solution_Text(solutionText)
                .build();

        assertNotEquals(solutionUserDto1, solutionUserDto2);
        assertNotEquals(solutionUserDto1.hashCode(), solutionUserDto2.hashCode());
    }

    @Test
    public void testNullFields() {
        SolutionUserDto solutionUserDtoTest = new SolutionUserDto();
        assertNull(solutionUserDtoTest.getUuid_SolutionUser());
        assertNull(solutionUserDtoTest.getUuid_challenge());
        assertNull(solutionUserDtoTest.getUuid_language());
        assertNull(solutionUserDtoTest.getUuid_user());
        assertNull(solutionUserDtoTest.getSolution_Text());
    }

}