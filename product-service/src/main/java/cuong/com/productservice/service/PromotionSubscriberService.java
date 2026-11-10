package cuong.com.productservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class PromotionSubscriberService {

    private final RedissonClient redissonClient;
    private final CacheManager cacheManager; // Công cụ xóa cache thật của Spring
    private RTopic topic;
    private int listenerId;

    public PromotionSubscriberService(RedissonClient redissonClient, CacheManager cacheManager) {
        this.redissonClient = redissonClient;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void subscribeToPromotionChannel() {
        topic = redissonClient.getTopic("promotion-updates");

        // Đăng ký lắng nghe tín hiệu
        listenerId = topic.addListener(String.class, (channel, productId) -> {
            System.out.println("🔔 [PRODUCT-SERVICE] Nhận được tín hiệu cập nhật giá từ channel [" + channel + "]");
            evictProductCache(productId);
        });
        System.out.println("✅ [PRODUCT-SERVICE] Đã đăng ký lắng nghe Pub/Sub: promotion-updates");
    }

    private void evictProductCache(String productId) {
        try {
            Long id = Long.parseLong(productId);
            // Kích hoạt XÓA CACHE THẬT SỰ trên vùng "products" khớp với @Cacheable
            if (cacheManager.getCache("products") != null) {
                cacheManager.getCache("products").evict(id);
                System.out.println("🗑️ [PRODUCT-SERVICE] Đã XÓA CACHE THẬT SỰ cho Product ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ [PRODUCT-SERVICE] ID sản phẩm không hợp lệ: " + productId);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (topic != null) {
            topic.removeListener(listenerId);
        }
    }
}