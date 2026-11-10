package cuong.com.promotionservice.service;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
public class PromotionPublisherService {

    private final RedissonClient redissonClient;

    public PromotionPublisherService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void publishPromotionUpdate(String productId) {
        // 1. Lấy đối tượng Topic với tên channel là "promotion-updates" từ Redisson
        RTopic topic = redissonClient.getTopic("promotion-updates");

        // 2. Publish (phát) thông điệp chứa mã sản phẩm cần làm mới cache
        long receivers = topic.publish(productId);

        System.out.println("📢 [PROMOTION-SERVICE] Đã phát sự kiện cập nhật giá cho sản phẩm: " + productId
                + " tới " + receivers + " subscriber(s).");
    }
}