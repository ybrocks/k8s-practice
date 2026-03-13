package com.beyond.ordersystem.ordering.service;

import com.beyond.ordersystem.common.service.SseAlarmService;
import com.beyond.ordersystem.ordering.domain.OrderDetail;
import com.beyond.ordersystem.ordering.domain.Ordering;
import com.beyond.ordersystem.ordering.dtos.OrderCreateDto;
import com.beyond.ordersystem.ordering.dtos.OrderListDto;
import com.beyond.ordersystem.ordering.repository.OrderDetailRepository;
import com.beyond.ordersystem.ordering.repository.OrderingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final SseAlarmService sseAlarmService;

    public OrderingService(OrderingRepository orderingRepository, OrderDetailRepository orderDetailRepository, SseAlarmService sseAlarmService) {
        this.orderingRepository = orderingRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.sseAlarmService = sseAlarmService;
    }

    public Long create(List<OrderCreateDto> orderCreateDtoList, String email){
        Ordering ordering = Ordering.builder()
                .memberEmail(email)
                .build();
        orderingRepository.save(ordering);
        for (OrderCreateDto dto : orderCreateDtoList) {
//            1)재고조회요청(동기요청-http요청)
//            Product product = productRepository.findById(dto.getProductId()).orElseThrow(() -> new EntityNotFoundException("entity is not found"));
//            if (product.getStockQuantity() < dto.getProductCount()){
//                throw new IllegalArgumentException("재고가 부족합니다.");
//    }
//            product.updateStockQuantity(dto.getProductCount());
//            2)주문발생
            OrderDetail orderDetail = OrderDetail.builder()
                    .ordering(ordering)
                    .productName(null)
                    .productId(dto.getProductId())
                    .quantity(dto.getProductCount())
                    .build();
            orderDetailRepository.save(orderDetail);
//            3)재고감소요청(동기-http요청 / 비동기-이벤트기반)
        }
        return ordering.getId();
    }


    @Transactional(readOnly = true)
    public List<OrderListDto> findAll(){
        return orderingRepository.findAll().stream().map(o->OrderListDto.fromEntity(o)).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<OrderListDto> myorders(String email){
        return orderingRepository.findAllByMemberEmail(email).stream().map(o->OrderListDto.fromEntity(o)).collect(Collectors.toList());
    }

}
