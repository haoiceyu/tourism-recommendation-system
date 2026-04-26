package com.tourism.config;

import com.tourism.domain.ProductType;
import com.tourism.domain.UserRole;
import com.tourism.entity.*;
import com.tourism.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seed() {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .nickname("管理员")
                    .phone("13800000000")
                    .role(UserRole.ADMIN)
                    .createdAt(Instant.now())
                    .build();
            User demo = User.builder()
                    .username("demo")
                    .email("demo@example.com")
                    .passwordHash(passwordEncoder.encode("demo12345"))
                    .nickname("演示用户")
                    .phone("13900000000")
                    .role(UserRole.USER)
                    .createdAt(Instant.now())
                    .build();
            userRepository.save(admin);
            userRepository.save(demo);

            Category cNature = categoryRepository.save(Category.builder().name("自然风光").scope(ProductType.ATTRACTION).build());
            Category cHistory = categoryRepository.save(Category.builder().name("人文历史").scope(ProductType.ATTRACTION).build());
            Category hLux = categoryRepository.save(Category.builder().name("豪华酒店").scope(ProductType.HOTEL).build());
            Category hBnb = categoryRepository.save(Category.builder().name("精品民宿").scope(ProductType.HOTEL).build());

            attractionRepository.save(Attraction.builder()
                    .name("云海山国家公园")
                    .description("徒步与观景胜地，四季景色各异。")
                    .city("杭州")
                    .categoryId(cNature.getId())
                    .price(new BigDecimal("120.00"))
                    .imageUrl("https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800")
                    .keywordTags("山景,徒步,摄影")
                    .active(true)
                    .build());
            attractionRepository.save(Attraction.builder()
                    .name("古城墙遗址公园")
                    .description("明清古城墙遗存，夜间灯光秀。")
                    .city("西安")
                    .categoryId(cHistory.getId())
                    .price(new BigDecimal("80.00"))
                    .imageUrl("https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?w=800")
                    .keywordTags("历史,夜景,亲子")
                    .active(true)
                    .build());
            attractionRepository.save(Attraction.builder()
                    .name("湖滨湿地公园")
                    .description("城市绿肺，骑行与观鸟。")
                    .city("苏州")
                    .categoryId(cNature.getId())
                    .price(new BigDecimal("60.00"))
                    .imageUrl("https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800")
                    .keywordTags("湿地,骑行,亲子")
                    .active(true)
                    .build());

            hotelRepository.save(Hotel.builder()
                    .name("湖畔豪华酒店")
                    .description("湖景客房，室内泳池与SPA。")
                    .city("杭州")
                    .categoryId(hLux.getId())
                    .pricePerNight(new BigDecimal("688.00"))
                    .imageUrl("https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800")
                    .keywordTags("湖景,商务,泳池")
                    .active(true)
                    .build());
            hotelRepository.save(Hotel.builder()
                    .name("古城精品民宿")
                    .description("四合院改造，近地铁。")
                    .city("西安")
                    .categoryId(hBnb.getId())
                    .pricePerNight(new BigDecimal("398.00"))
                    .imageUrl("https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800")
                    .keywordTags("民宿,四合院,地铁")
                    .active(true)
                    .build());
            hotelRepository.save(Hotel.builder()
                    .name("商务快捷酒店")
                    .description("高性价比，含早餐。")
                    .city("苏州")
                    .categoryId(hLux.getId())
                    .pricePerNight(new BigDecimal("259.00"))
                    .imageUrl("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800")
                    .keywordTags("商务,早餐,性价比")
                    .active(true)
                    .build());
        };
    }
}
