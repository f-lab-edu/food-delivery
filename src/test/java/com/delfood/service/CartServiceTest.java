package com.delfood.service;

import static org.mockito.BDDMockito.given;

import com.delfood.dao.CartDao;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceTest {
  @InjectMocks
  CartService service;
  
  @Mock
  CartDao dao;
  
  
  // 로직이 확정되면 테스트코드를 다시 작성할 예정입니다
}
