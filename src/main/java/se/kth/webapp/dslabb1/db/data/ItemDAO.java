package se.kth.webapp.dslabb1.db.data;

import java.util.UUID;

public record ItemDAO(UUID orderId, String sku, int quantity) {
}
