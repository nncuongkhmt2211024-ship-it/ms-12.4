package cuong.com.promotionservice.controller;

import cuong.com.promotionservice.service.PromotionPublisherService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionPublisherService publisherService;

    public PromotionController(PromotionPublisherService publisherService) {
        this.publisherService = publisherService;
    }

    // API này giả lập việc Admin bấm nút cập nhật giá sản phẩm
    @PostMapping("/update-price/{productId}")
    public String updatePrice(@PathVariable String productId) {
        publisherService.publishPromotionUpdate(productId);
        return "Đã phát tín hiệu cập nhật giá cho sản phẩm: " + productId;
    }
}