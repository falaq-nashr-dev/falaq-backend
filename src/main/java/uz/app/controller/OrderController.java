package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.*;
import uz.app.entity.Order;
import uz.app.entity.OrderedProduct;
import uz.app.entity.Product;
import uz.app.entity.User;
import uz.app.entity.enums.OrderStatus;
import uz.app.repository.OrderRepository;
import uz.app.repository.OrderedProductRepository;
import uz.app.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management")
public class OrderController {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderedProductRepository orderedProductRepository;

    @PostMapping
    public ResponseEntity<?> addOrder(@RequestBody OrderDTO orderDTO, @AuthenticationPrincipal User user) {
        List<UUID> productIds = orderDTO.getProducts().stream()
                .map(OrderProductDTO::getProductId)
                .collect(Collectors.toList());

        Map<UUID, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<String> errors = new ArrayList<>();
        for (OrderProductDTO orderProduct : orderDTO.getProducts()) {
            Product product = productMap.get(orderProduct.getProductId());
            if (product == null) {
                errors.add("Product not found with ID: " + orderProduct.getProductId());
                continue;
            }
            if (orderProduct.getAmount() <= 0) {
                errors.add("Quantity must be greater than 0 for product: " + product.getName());
            }
            if (product.getQuantity() < orderProduct.getAmount()) {
                errors.add("Not enough stock available for product: " + product.getName());
            }
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setCustomerFullName(orderDTO.getCustomerFullName());
        newOrder.setCustomerPhoneNumber(orderDTO.getCustomerPhoneNumber());
        newOrder.setStatus(OrderStatus.IN_PROGRESS);
        newOrder.setTotalPrice(0.0);
        newOrder.setCreatedAt(LocalDateTime.now());

        newOrder = orderRepository.save(newOrder);

        double totalPrice = 0;
        List<OrderedProduct> orderedProducts = new ArrayList<>();

        for (OrderProductDTO orderProductDTO : orderDTO.getProducts()) {
            Product product = productMap.get(orderProductDTO.getProductId());
            int quantity = orderProductDTO.getAmount();

            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setOrder(newOrder);
            orderedProduct.setProduct(product);
            orderedProduct.setQuantity(quantity);
            orderedProducts.add(orderedProduct);

            totalPrice += product.getPrice() * quantity;

            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
        }

        orderedProductRepository.saveAll(orderedProducts);
        newOrder.setTotalPrice(totalPrice);
        newOrder.setOrderedProducts(orderedProducts);
        orderRepository.save(newOrder);

        return ResponseEntity.ok(convertToOrderResponseDTO(newOrder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable UUID id, @RequestBody OrderDTO orderDTO) {
        Optional<Order> existingOrderOptional = orderRepository.findById(id);
        if (existingOrderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order existingOrder = existingOrderOptional.get();

        for (OrderedProduct orderedProduct : existingOrder.getOrderedProducts()) {
            Product product = orderedProduct.getProduct();
            product.setQuantity(product.getQuantity() + orderedProduct.getQuantity());
            productRepository.save(product);
        }

        List<UUID> productIds = orderDTO.getProducts().stream()
                .map(OrderProductDTO::getProductId)
                .collect(Collectors.toList());

        Map<UUID, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<String> errors = new ArrayList<>();
        for (OrderProductDTO orderProduct : orderDTO.getProducts()) {
            Product product = productMap.get(orderProduct.getProductId());
            if (product == null) {
                errors.add("Product not found with ID: " + orderProduct.getProductId());
                continue;
            }
            if (orderProduct.getAmount() <= 0) {
                errors.add("Quantity must be greater than 0 for product: " + product.getName());
            }
            if (product.getQuantity() < orderProduct.getAmount()) {
                errors.add("Not enough stock available for product: " + product.getName());
            }
        }

        if (!errors.isEmpty()) {
            for (OrderedProduct orderedProduct : existingOrder.getOrderedProducts()) {
                Product product = orderedProduct.getProduct();
                product.setQuantity(product.getQuantity() - orderedProduct.getQuantity());
                productRepository.save(product);
            }
            return ResponseEntity.badRequest().body(errors);
        }

        existingOrder.setCustomerFullName(orderDTO.getCustomerFullName());
        existingOrder.setCustomerPhoneNumber(orderDTO.getCustomerPhoneNumber());

        existingOrder.getOrderedProducts().clear();
        orderRepository.save(existingOrder);

        double totalPrice = 0;
        List<OrderedProduct> newOrderedProducts = new ArrayList<>();

        for (OrderProductDTO orderProductDTO : orderDTO.getProducts()) {
            Product product = productMap.get(orderProductDTO.getProductId());
            int quantity = orderProductDTO.getAmount();

            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setOrder(existingOrder);
            orderedProduct.setProduct(product);
            orderedProduct.setQuantity(quantity);
            newOrderedProducts.add(orderedProduct);

            totalPrice += product.getPrice() * quantity;

            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
        }

        existingOrder.getOrderedProducts().addAll(newOrderedProducts);
        existingOrder.setTotalPrice(totalPrice);
        orderRepository.save(existingOrder);

        return ResponseEntity.ok(convertToOrderResponseDTO(existingOrder));
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        List<OrderResponseDTO> orderDTOs = orderRepository
                .findAll()
                .stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return ResponseEntity.ok(convertToOrderResponseDTO(order));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponseDTO> orderDTOs = orderRepository
                .findByStatus(status)
                .stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOs);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();
        for (OrderedProduct orderedProduct : order.getOrderedProducts()) {
            Product product = orderedProduct.getProduct();
            product.setQuantity(product.getQuantity() + orderedProduct.getQuantity());
            productRepository.save(product);
        }

        orderRepository.delete(order);
        return ResponseEntity.ok("Order deleted successfully");
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','OPERATOR')")
    @Tag(name = "Change order's status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable UUID id, @RequestParam OrderStatus status) {
        Optional<Order> existingOrderOptional = orderRepository.findById(id);
        if (existingOrderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = existingOrderOptional.get();

        if (status == OrderStatus.CANCELLED) {
            for (OrderedProduct orderedProduct : order.getOrderedProducts()) {
                Product product = orderedProduct.getProduct();
                product.setQuantity(product.getQuantity() + orderedProduct.getQuantity());
                productRepository.save(product);
            }
        } else if (order.getStatus() == OrderStatus.CANCELLED && status != OrderStatus.CANCELLED) {
            for (OrderedProduct orderedProduct : order.getOrderedProducts()) {
                Product product = orderedProduct.getProduct();
                if (product.getQuantity() < orderedProduct.getQuantity()) {
                    return ResponseEntity.badRequest().body("Not enough stock available for product: " + product.getName());
                }
                product.setQuantity(product.getQuantity() - orderedProduct.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(status);
        orderRepository.save(order);

        return ResponseEntity.ok(convertToOrderResponseDTO(order));
    }

    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        List<OrderResponseDTO.OrderProductResponseDTO> productDTOs = order
                .getOrderedProducts()
                .stream()
                .map(orderedProduct -> {
                    Product product = orderedProduct.getProduct();
                    return OrderResponseDTO.OrderProductResponseDTO.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .quantity(orderedProduct.getQuantity())
                            .price(product.getPrice())
                            .totalPrice(product.getPrice() * orderedProduct.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .customerFullName(order.getCustomerFullName())
                .customerPhoneNumber(order.getCustomerPhoneNumber())
                .products(productDTOs)
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .status(order.getStatus())
                .build();
    }
}