package com.delfood.service;

import com.delfood.mapper.WorkMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class WorkServiceTest {

  @InjectMocks
  WorkService workService;

  @Mock
  WorkMapper workMapper;

  @Test
  public void addWork_성공() {
    // given
    Long shopId = 100L;

    // when
    given(workMapper.insertWorkToOpen(shopId)).willReturn(1);

    // then
    assertThatCode(() -> workService.addWork(shopId)).doesNotThrowAnyException();
  }

  @Test(expected = RuntimeException.class)
  public void addWork_실패() {
    // given (없는 shopID)
    Long noExistShopId = 101010L;

    // when
    given(workMapper.insertWorkToOpen(noExistShopId)).willThrow(RuntimeException.class);

    // then
    workService.addWork(noExistShopId);
  }

  @Test
  public void closeWork_성공() {
    // given
    Long shopId = 100L;

    // when
    given(workMapper.updateWorkToClose(shopId)).willReturn(1);

    // then
    assertThatCode(() -> workService.closeWork(shopId)).doesNotThrowAnyException();

  }

  @Test(expected = RuntimeException.class)
  public void closeWork_실패() {
    // given
    Long noExistShopId = 101010L;

    // when
    given(workMapper.updateWorkToClose(noExistShopId)).willReturn(0);

    // then
    workService.closeWork(noExistShopId);
  }
}
