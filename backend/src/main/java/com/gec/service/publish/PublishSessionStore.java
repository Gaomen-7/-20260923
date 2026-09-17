package com.gec.service.publish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 商品发布流程的 Redis 会话存储：统一管理四步缓存的 key 命名。
 *
 * <p>四步分别对应：基础信息（base）、商品属性（goods-attr）、
 * 销售属性（sale-attr）、SKU（sku），key 形如 {@code {pubKey}-{step后缀}}。
 */
@Service
public class PublishSessionStore {

    /**
     * 发布流程四步缓存，每个步骤对应一个 key 后缀。
     */
    public enum PublishStep {
        /** 基础信息 */
        BASE("base"),
        /** 商品属性 */
        GOODS_ATTR("goods-attr"),
        /** 销售属性 */
        SALE_ATTR("sale-attr"),
        /** SKU */
        SKU("sku");

        private final String suffix;

        PublishStep(String suffix) {
            this.suffix = suffix;
        }

        public String getSuffix() {
            return suffix;
        }
    }

    @Autowired
    @Qualifier("myJacksonTemp")
    private RedisTemplate<String, Object> jacksonTemp;

    /**
     * 生成发布会话 key：16位大写UUID（去掉横杠后取前16位）。
     *
     * @return pubKey
     */
    public String generatePubKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    /**
     * 保存某一步的缓存数据。
     *
     * @param pubKey 发布会话 key
     * @param step   发布步骤
     * @param data   缓存数据
     */
    public void save(String pubKey, PublishStep step, Object data) {
        String key = pubKey + "-" + step.getSuffix();
        jacksonTemp.opsForValue().set(key, data);
    }

    /**
     * 读取某一步的缓存数据。
     *
     * @param pubKey 发布会话 key
     * @param step   发布步骤
     * @return 缓存数据；不存在时返回 null
     */
    public Object get(String pubKey, PublishStep step) {
        String key = pubKey + "-" + step.getSuffix();
        return jacksonTemp.opsForValue().get(key);
    }

    /**
     * 清除该发布会话下所有步骤的缓存。
     *
     * @param pubKey 发布会话 key
     */
    public void clear(String pubKey) {
        for (PublishStep step : PublishStep.values()) {
            jacksonTemp.delete(pubKey + "-" + step.getSuffix());
        }
    }
}
