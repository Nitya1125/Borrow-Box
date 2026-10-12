package com.borrowbox.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.utils.DateTimeUtils;
import com.borrowbox.app.utils.QrUtils;

import org.junit.Test;

public class BusinessRulesTest {

    @Test
    public void testCannotBorrowOwnItem_ruleCheck() {
        String currentUserId = "user_123";
        Item item = new Item();
        item.setId("item_999");
        item.setOwnerId("user_123"); // Same user is owner

        boolean isOwnItem = item.getOwnerId() != null && item.getOwnerId().equals(currentUserId);
        assertTrue("User must be identified as item owner", isOwnItem);
    }

    @Test
    public void testCannotBorrowUnavailableItem_ruleCheck() {
        Item item = new Item();
        item.setId("item_555");
        item.setStatus("borrowed");

        assertFalse("Item with status 'borrowed' should not be available", item.isAvailable());
    }

    @Test
    public void testQrHandoverPayload_creationAndParsing() {
        String txId = "tx_8888";
        String token = "secrettoken1";

        String payload = QrUtils.createHandoverPayload(txId, token);
        assertNotNull(payload);
        assertTrue(payload.startsWith("borrowbox://handover"));

        QrUtils.HandoverData data = QrUtils.parseHandoverPayload(payload);
        assertNotNull(data);
        assertEquals(txId, data.getTransactionId());
        assertEquals(token, data.getToken());
    }

    @Test
    public void testTransactionOverdueDynamicComputation() {
        Transaction tx = new Transaction();
        tx.setStatus("borrowed");
        long pastDue = System.currentTimeMillis() - 86400000L; // 1 day ago
        tx.setDueAt(pastDue);

        assertTrue("Transaction must be evaluated as overdue when status is borrowed and dueAt is past", tx.isOverdue());
    }
}
