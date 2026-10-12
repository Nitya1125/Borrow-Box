package com.borrowbox.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.borrowbox.app.utils.PasswordHasher;

import org.junit.Test;

public class PasswordHasherTest {

    @Test
    public void testSaltGeneration_isRandomAndCorrectLength() {
        String salt1 = PasswordHasher.generateSalt();
        String salt2 = PasswordHasher.generateSalt();

        assertNotNull(salt1);
        assertNotNull(salt2);
        assertEquals(32, salt1.length()); // 16 bytes = 32 hex chars
        assertEquals(32, salt2.length());
        assertNotEquals("Two generated salts should not match", salt1, salt2);
    }

    @Test
    public void testHashPassword_andVerification_success() {
        String password = "CampusStudentPass@2026";
        String salt = PasswordHasher.generateSalt();

        String hash = PasswordHasher.hashPassword(password, salt);
        assertNotNull(hash);
        assertFalse(hash.isEmpty());

        // Verification with identical password and salt should succeed
        boolean valid = PasswordHasher.verifyPassword(password, salt, hash);
        assertTrue("Password verification should succeed with correct password", valid);
    }

    @Test
    public void testHashPassword_withWrongPassword_fails() {
        String correctPassword = "MySecurePassword123";
        String wrongPassword = "WrongPassword123";
        String salt = PasswordHasher.generateSalt();

        String hash = PasswordHasher.hashPassword(correctPassword, salt);
        boolean valid = PasswordHasher.verifyPassword(wrongPassword, salt, hash);

        assertFalse("Password verification must fail with incorrect password", valid);
    }

    @Test
    public void testDifferentSalts_produceDifferentHashes() {
        String password = "SamePassword123";
        String salt1 = PasswordHasher.generateSalt();
        String salt2 = PasswordHasher.generateSalt();

        String hash1 = PasswordHasher.hashPassword(password, salt1);
        String hash2 = PasswordHasher.hashPassword(password, salt2);

        assertNotEquals("Hashes for the same password with different salts must differ", hash1, hash2);
    }
}
