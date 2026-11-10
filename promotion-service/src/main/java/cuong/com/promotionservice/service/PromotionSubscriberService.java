package cuong.com.promotionservice.service;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
public class PromotionSubscriberService {

    private final RedissonClient redissonClient;
    private RTopic topic;
    private int listenerId;

    public PromotionSubscriberService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    // Tự động đăng ký lắng nghe ngay khi Service vừa khởi động xong
    @PostConstruct
    public void subscribeToPromotionChannel() {
        topic = redissonClient.getTopic("promotion-updates");

        // Lắng nghe kiểu nhận dữ liệu dạng chuỗi (String.class)
        listenerId = topic.addListener(String.class, (channel, productId) -> {
            System.out.println("🔔 [PRODUCT-SERVICE] Nhận được tín hiệu từ channel [" + channel + "]");

            // Thực hiện hành động xóa cache của sản phẩm này
            evictProductCache(productId);
        });

        System.out.println("✅ [PRODUCT-SERVICE] Đã sẵn sàng lắng nghe kênh Pub/Sub: promotion-updates");
    }

    private void evictProductCache(String productId) {
        // Giả lập logic xóa cache trong Redis (ví dụ: redisTemplate.delete("product:" + productId))
        System.out.println("🗑️ [PRODUCT-SERVICE] Đã xóa cache Redis cho sản phẩm ID: " + productId);
        System.out.println("-> Lần truy vấn tiếp theo hệ thống sẽ tự động fetch giá mới từ DB!");
    }

    // Hủy đăng ký khi ứng dụng tắt để giải phóng tài nguyên
    @PreDestroy
    public void cleanup() {
        if (topic != null) {
            topic.removeListener(listenerId);
        }
    }
}