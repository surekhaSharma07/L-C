package ProductServiceTests;

import com.example.inventory.dto.ProductRequest;
import com.example.inventory.dto.ProductResponse;
import com.example.inventory.service.ProductService;
import com.example.inventory.repository.InMemoryProductRepository;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.service.DefaultProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        ProductRepository repository = new InMemoryProductRepository();
        productService = new DefaultProductService(repository);
    }

    @Test
    public void testUpdateProduct() {
        ProductRequest request = new ProductRequest();
        request.setId(103);
        request.setName("Old Name");
        request.setQuantity(20);

        productService.addProduct(request);

        ProductRequest updatedRequest = new ProductRequest();
        updatedRequest.setId(103);
        updatedRequest.setName("New Name");
        updatedRequest.setQuantity(50);

        productService.updateProduct(103, updatedRequest);
        ProductResponse result = productService.getProductById(103);

        assertEquals("New Name", result.getName());
        assertEquals(50, result.getQuantity());
    }

    @Test
    public void testUpdateStockOnly() {
        ProductRequest request = new ProductRequest();
        request.setId(104);
        request.setName("Stock Item");
        request.setQuantity(15);

        productService.addProduct(request);
        productService.updateStock(104, 45);

        ProductResponse result = productService.getProductById(104);
        assertEquals(45, result.getQuantity());
    }

    @Test
    public void testAvailability() {
        ProductRequest request = new ProductRequest();
        request.setId(105);
        request.setName("Check Item");
        request.setQuantity(0);

        productService.addProduct(request);
        assertFalse(productService.isAvailable(105));

        productService.updateStock(105, 5);
        assertTrue(productService.isAvailable(105));
    }

    @Test
    public void testSearchProduct() {
        ProductRequest request = new ProductRequest();
        request.setId(106);
        request.setName("Webcam");
        request.setQuantity(20);

        productService.addProduct(request);

        assertEquals(1, productService.searchProducts("cam").size());
    }

    @Test
    public void testLowStock() {
        ProductRequest request = new ProductRequest();
        request.setId(107);
        request.setName("Cable");
        request.setQuantity(3);

        productService.addProduct(request);
        assertEquals(1, productService.getLowStockProducts(5).size());
    }
}