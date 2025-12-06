import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ProductStock Complete Test Suite")
public class ProductStockTest {

    private ProductStock stock;

    @BeforeAll
    static void setupAll() {
        System.out.println("Starting ProductStock Complete Test Suite");
    }

    @BeforeEach
    void setup() {
        stock = new ProductStock("P100", "LOC-1", 20, 5, 100);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test completed");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("All ProductStock tests finished");
    }

    // Constructor tests
    @Test @Order(1)
    @Tag("sanity") @Tag("regression")
    @DisplayName("Constructor creates object with valid input")
    void testValidConstructor() {
        ProductStock ps = new ProductStock("X1", "L1", 10, 3, 50);
        assertAll(
                () -> assertEquals("X1", ps.getProductId()),
                () -> assertEquals("L1", ps.getLocation()),
                () -> assertEquals(10, ps.getOnHand()),
                () -> assertEquals(0, ps.getReserved())
        );
    }

    @Test @Order(2)
    @DisplayName("Constructor rejects null or blank productId")
    void testInvalidProductId() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock(null, "L1", 10, 2, 50));
    }

    @Test @Order(3)
    @DisplayName("Constructor rejects null or blank location")
    void testInvalidLocation() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("A", "", 10, 2, 50));
    }

    @Test @Order(4)
    @DisplayName("Constructor rejects negative initialOnHand")
    void testNegativeOnHand() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("A", "L1", -1, 2, 50));
    }

    @Test @Order(5)
    @DisplayName("Constructor rejects initialOnHand exceeding maxCapacity")
    void testOnHandOverCapacity() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("A", "L1", 200, 2, 100));
    }

    // Location tests
    @Test @Order(6)
    @DisplayName("Change location successfully")
    void testChangeLocation() {
        stock.changeLocation("LOC-2");
        assertEquals("LOC-2", stock.getLocation());
    }

    @Test @Order(7)
    @DisplayName("Reject invalid location change")
    void testChangeLocationInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.changeLocation(""));
    }

    // Add stock tests
    @Test @Order(8)
    @DisplayName("Add valid stock amount")
    void testAddStock() {
        stock.addStock(10);
        assertEquals(30, stock.getOnHand());
    }

    @Test @Order(9)
    @DisplayName("Reject zero or negative stock addition")
    void testAddStockNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.addStock(0));
    }

    @Test @Order(10)
    @DisplayName("Reject stock addition exceeding max capacity")
    void testAddStockExceedCapacity() {
        assertThrows(IllegalStateException.class,
                () -> stock.addStock(500));
    }

    @Test @Order(11)
    @DisplayName("Add stock to reach max capacity exactly")
    void testAddStockBoundary() {
        stock.addStock(80); // 20 + 80 = 100 maxCapacity
        assertEquals(100, stock.getOnHand());
    }

    // Remove damaged stock tests
    @Test @Order(12)
    @DisplayName("Remove valid damaged stock")
    void testRemoveDamaged() {
        stock.removeDamaged(5);
        assertEquals(15, stock.getOnHand());
    }

    @Test @Order(13)
    @DisplayName("Reject removal exceeding onHand")
    void testRemoveDamagedTooMuch() {
        assertThrows(IllegalStateException.class,
                () -> stock.removeDamaged(999));
    }

    @Test @Order(14)
    @DisplayName("Remove all onHand stock")
    void testRemoveDamagedExact() {
        stock.removeDamaged(stock.getOnHand());
        assertEquals(0, stock.getOnHand());
    }

    @Test @Order(15)
    @DisplayName("Reject zero or negative removal")
    void testRemoveDamagedInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.removeDamaged(0));
    }

    // Reserve and release tests
    @Test @Order(16)
    @DisplayName("Reserve valid amount")
    void testReserve() {
        stock.reserve(5);
        assertEquals(5, stock.getReserved());
        assertEquals(15, stock.getAvailable());
    }

    @Test @Order(17)
    @DisplayName("Reject reservation exceeding available stock")
    void testReserveTooMuch() {
        assertThrows(IllegalStateException.class,
                () -> stock.reserve(999));
    }

    @Test @Order(18)
    @DisplayName("Reserve all available stock")
    void testReserveAll() {
        stock.reserve(stock.getAvailable());
        assertEquals(stock.getOnHand(), stock.getReserved());
        assertEquals(0, stock.getAvailable());
    }

    @Test @Order(19)
    @DisplayName("Reject zero or negative reservation")
    void testReserveInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.reserve(0));
    }

    @Test @Order(20)
    @DisplayName("Release reserved stock successfully")
    void testReleaseReservation() {
        stock.reserve(10);
        stock.releaseReservation(4);
        assertEquals(6, stock.getReserved());
    }

    @Test @Order(21)
    @DisplayName("Reject releasing more than reserved")
    void testReleaseReservationTooMuch() {
        assertThrows(IllegalStateException.class,
                () -> stock.releaseReservation(5));
    }

    // Ship reserved tests
    @Test @Order(22)
    @DisplayName("Ship reserved stock successfully")
    void testShipReserved() {
        stock.reserve(10);
        stock.shipReserved(5);
        assertEquals(15, stock.getOnHand());
        assertEquals(5, stock.getReserved());
    }

    @Test @Order(23)
    @DisplayName("Reject shipping more than reserved")
    void testShipReservedTooMuch() {
        assertThrows(IllegalStateException.class,
                () -> stock.shipReserved(1));
    }

    // Reorder tests
    @Test @Order(24)
    @DisplayName("Reorder needed when available stock below threshold")
    void testReorderNeeded() {
        stock.reserve(18);
        assertTrue(stock.isReorderNeeded());
    }

    @Test @Order(25)
    @DisplayName("No reorder needed when available stock meets threshold")
    void testReorderNotNeeded() {
        assertFalse(stock.isReorderNeeded());
    }

    // Update threshold and capacity
    @Test @Order(26)
    @DisplayName("Update reorder threshold successfully")
    void testUpdateReorderThreshold() {
        stock.updateReorderThreshold(20);
        assertEquals(20, stock.getReorderThreshold());
    }

    @Test @Order(27)
    @DisplayName("Reject negative reorder threshold")
    void testUpdateReorderThresholdNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.updateReorderThreshold(-1));
    }

    @Test @Order(28)
    @DisplayName("Reject reorder threshold exceeding max capacity")
    void testUpdateReorderThresholdTooHigh() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.updateReorderThreshold(999));
    }

    @Test @Order(29)
    @DisplayName("Increase max capacity successfully")
    void testUpdateMaxCapacity() {
        stock.updateMaxCapacity(200);
        assertEquals(200, stock.getMaxCapacity());
    }

    @Test @Order(30)
    @DisplayName("Reject max capacity less than onHand")
    void testUpdateMaxCapacityLow() {
        assertThrows(IllegalStateException.class,
                () -> stock.updateMaxCapacity(5));
    }

    @Test @Order(31)
    @DisplayName("Reject non-positive max capacity")
    void testUpdateMaxCapacityInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.updateMaxCapacity(0));
    }

    @Test @Order(32)
    @DisplayName("Reduce max capacity safely to onHand")
    void testUpdateMaxCapacityReservedBoundary() {
        ProductStock safeStock = new ProductStock("P200", "LOC-1", 15, 5, 100);
        safeStock.updateMaxCapacity(15); // safe: maxCapacity = onHand
        assertEquals(15, safeStock.getMaxCapacity());
    }

    // Disabled future feature
    @Disabled("Future feature not implemented")
    @Test @Order(99)
    @DisplayName("Auto location (future feature)")
    void testAutoLocationFeature() {}

    // Timeout
    @Test
    @Order(33)
    @DisplayName("Timeout test")
    @Timeout(1)
    void testTimeoutExample() {
        assertTimeout(Duration.ofSeconds(1), () -> stock.addStock(1));
    }
}
